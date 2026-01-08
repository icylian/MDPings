package com.sekusarisu.mdpings.vpings.presentation.server_terminal

interface ServerTerminalAction {
    data class OnRefreshToken(val baseUrl: String): ServerTerminalAction
    data class OnInitConnection(val baseUrl: String, val selectedServerId: Int, val connectTo: String): ServerTerminalAction
    data class OnConnectToTerminal(val baseUrl: String, val sessionId: String, val connectTo: String): ServerTerminalAction
    object OnDisconnect: ServerTerminalAction
    data class OnSendCommand(val command: String): ServerTerminalAction
    object OnCleanScreen: ServerTerminalAction
    data class OnChangeFontSize(val delta: Int): ServerTerminalAction
    object OnToggleCtrl: ServerTerminalAction
    object OnToggleAlt: ServerTerminalAction
    object OnResetModifiers: ServerTerminalAction
    data class OnSendSpecialKey(val key: String): ServerTerminalAction
}