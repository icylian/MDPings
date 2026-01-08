package com.sekusarisu.mdpings.vpings.presentation.server_terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekusarisu.mdpings.core.domain.util.onError
import com.sekusarisu.mdpings.core.domain.util.onSuccess
import com.sekusarisu.mdpings.vpings.domain.RealtimeServerDataClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServerTerminalViewModel(
    private val realtimeServerDataClient: RealtimeServerDataClient
): ViewModel() {
    private val _state = MutableStateFlow(ServerTerminalState())
    val state = _state
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ServerTerminalState()
        )

    private val _terminalDataFlow = MutableSharedFlow<String>()
    val terminalDataFlow = _terminalDataFlow.asSharedFlow()

    private var webSocketJob: Job? = null

    fun onAction(action: ServerTerminalAction) {
        when(action) {
            is ServerTerminalAction.OnInitConnection -> {
                refreshToken(action.baseUrl)
                initConnection(action.baseUrl, action.selectedServerId, action.connectTo)
            }
            is ServerTerminalAction.OnConnectToTerminal -> {
                connectToTerminal(action.baseUrl, action.sessionId, action.connectTo)
            }
            is ServerTerminalAction.OnSendCommand -> {
                sendCommand(action.command)
            }
            is ServerTerminalAction.OnDisconnect -> {
                closeSession()
            }
            is ServerTerminalAction.OnCleanScreen -> {
                cleanScreen()
            }
            is ServerTerminalAction.OnChangeFontSize -> {
                changeFontSize(action.delta)
            }
            is ServerTerminalAction.OnToggleCtrl -> {
                _state.update { it.copy(isCtrlPressed = !it.isCtrlPressed) }
            }
            is ServerTerminalAction.OnToggleAlt -> {
                _state.update { it.copy(isAltPressed = !it.isAltPressed) }
            }
            is ServerTerminalAction.OnResetModifiers -> {
                _state.update { it.copy(isCtrlPressed = false, isAltPressed = false) }
            }
            is ServerTerminalAction.OnSendSpecialKey -> {
                sendSpecialKey(action.key)
            }
        }
    }

    private fun cleanScreen() {
        // 发送清屏命令
        sendCommand("\u000c")
    }

    private fun refreshToken(baseUrl: String) {
        viewModelScope.launch{
            realtimeServerDataClient
                .refreshToken(baseUrl)
        }
    }

    private fun initConnection(baseUrl: String, selectedServerId: Int, connectTo: String) {
        viewModelScope.launch{
            _state.update { it.copy(isLoading = true) }
            realtimeServerDataClient
                .getSession(baseUrl, selectedServerId)
                .onSuccess { result ->
                    connectToTerminal(baseUrl, result.sessionId, connectTo)
                }
                .onError { error ->
                    println(error)
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun connectToTerminal(baseUrl: String, sessionId: String, connectTo: String) {
        webSocketJob?.cancel()
        _state.update { it.copy(isConnected = true, isLoading = false) }

        viewModelScope.launch {
            _terminalDataFlow.emit("已和 $connectTo 建立终端连接\r\n")
        }

        webSocketJob = viewModelScope.launch{
            realtimeServerDataClient
                .getServerTerminalStream(baseUrl, sessionId)
                .collect { terminalData ->
                    _terminalDataFlow.emit(terminalData)
                }
        }
    }

    private fun sendCommand(command: String) {
        viewModelScope.launch{
            realtimeServerDataClient.sendCommand(command)
        }
    }

    private fun closeSession() {
        webSocketJob?.cancel()
        _state.update { it.copy(isConnected = false) }

        viewModelScope.launch{
            _terminalDataFlow.emit("\r\n已断开终端连接\r\n")
            realtimeServerDataClient.disconnect()
        }
    }

    private fun changeFontSize(delta: Int) {
        _state.update { currentState ->
            val newSize = (currentState.fontSize + delta).coerceIn(8, 24)
            currentState.copy(fontSize = newSize)
        }
    }

    private fun sendSpecialKey(key: String) {
        val command = when (key) {
            "ESC" -> "\u001B"
            "TAB" -> "\t"
            "UP" -> "\u001B[A"
            "DOWN" -> "\u001B[B"
            "RIGHT" -> "\u001B[C"
            "LEFT" -> "\u001B[D"
            "HOME" -> "\u001B[H"
            "END" -> "\u001B[F"
            "PAGEUP" -> "\u001B[5~"
            "PAGEDOWN" -> "\u001B[6~"
            else -> return
        }

        sendCommand(command)
    }

}