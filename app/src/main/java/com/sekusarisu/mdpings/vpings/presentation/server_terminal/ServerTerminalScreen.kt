package com.sekusarisu.mdpings.vpings.presentation.server_terminal

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sekusarisu.mdpings.MainActivity
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsState
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListState

private const val TAG = "ServerTerminalScreen"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ServerTerminalScreen(
    state: ServerTerminalState,
    serverListState: ServerListState,
    selectedServerId: Int,
    appSettingsState: AppSettingsState,
    onAction: (ServerTerminalAction) -> Unit,
    viewModel: ServerTerminalViewModel,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context as? MainActivity
    val baseUrl = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance].baseUrl
    val wsServer = serverListState.wsServers.firstOrNull { it.id == selectedServerId }
    val connectTo = wsServer?.let { "${it.countryCode} ${it.name}" } ?: "Unknown Server"

    // xterm就绪状态
    var xtermReady by remember { mutableStateOf(false) }

    // 记录最后一次触发尺寸调整的时间，避免频繁调用
    var lastResizeTime by remember { mutableStateOf(0L) }

    // 注册音量键处理
    DisposableEffect(Unit) {
        activity?.onVolumeKeyPressed = { isVolumeUp ->
            val delta = if (isVolumeUp) 2 else -2
            onAction(ServerTerminalAction.OnChangeFontSize(delta))
            true // 消费事件，防止系统音量调整
        }
        onDispose {
            activity?.onVolumeKeyPressed = null
        }
    }

    // 创建WebView实例
    val webView = remember {
        // 启用WebView调试（仅在Debug版本）
        WebView.setWebContentsDebuggingEnabled(true)

        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                setSupportZoom(false)
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                    Log.d(TAG, "JS Console: ${message.message()} [${message.sourceId()}:${message.lineNumber()}]")
                    return true
                }
            }

            // 监听页面加载
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d(TAG, "WebView page loaded: $url")
                }
            }

            // 监听WebView布局变化 - 只在 xterm 准备好后才响应
            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val width = right - left
                val height = bottom - top
                val oldHeight = oldBottom - oldTop
                val oldWidth = oldRight - oldLeft

                // 记录所有布局变化
                Log.d(TAG, "WebView layout: ${width}x${height} (was: ${oldWidth}x${oldHeight}), xtermReady: $xtermReady")

                // 只有在 xterm 准备好后才触发调整
                if (!xtermReady) {
                    Log.d(TAG, "Skipping resize - xterm not ready yet")
                    return@addOnLayoutChangeListener
                }

                // 检查是否有显著的尺寸变化
                if (height > 100 && width > 100) {
                    val heightChanged = Math.abs(height - oldHeight) > 10
                    val widthChanged = Math.abs(width - oldWidth) > 10

                    if (heightChanged || widthChanged) {
                        // 防抖：避免在300ms内重复触发
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastResizeTime < 300) {
                            Log.d(TAG, "Skipping resize - too soon (debounce)")
                            return@addOnLayoutChangeListener
                        }
                        lastResizeTime = currentTime

                        post {
                            Log.d(TAG, "Triggering terminal resize to ${width}x${height}")

                            evaluateJavascript("""
                                (function() {
                                    if(window.xterm && window.xterm.setContainerSize) {
                                        window.xterm.setContainerSize($width, $height);
                                        return 'success';
                                    }
                                    return 'xterm not ready';
                                })();
                            """.trimIndent()) { result ->
                                Log.d(TAG, "Terminal resize result: $result")
                            }
                        }
                    }
                }
            }

            // 移除触摸监听器 - 不强制滚动，让用户自由查看历史输出
            // setOnTouchListener 已移除

            // 添加JavaScript接口
            addJavascriptInterface(object {
                @JavascriptInterface
                fun onData(data: String) {
                    // 从 ViewModel 获取当前状态
                    val currentState = viewModel.state.value
                    val transformedData = when {
                        currentState.isCtrlPressed -> {
                            // Ctrl 组合键：将字母转换为控制字符
                            if (data.length == 1) {
                                val char = data[0]
                                when {
                                    char in 'a'..'z' -> {
                                        // Ctrl+a = 0x01, Ctrl+b = 0x02, ..., Ctrl+z = 0x1A
                                        String(charArrayOf((char - 'a' + 1).toChar()))
                                    }
                                    char in 'A'..'Z' -> {
                                        String(charArrayOf((char - 'A' + 1).toChar()))
                                    }
                                    else -> data
                                }
                            } else {
                                data
                            }
                        }
                        currentState.isAltPressed -> {
                            // Alt 组合键：发送 ESC + 字符
                            "\u001B$data"
                        }
                        else -> data
                    }

                    val hex = transformedData.toByteArray().joinToString(" ") { byte ->
                        String.format("%02X", byte)
                    }
                    Log.d(TAG, "User input: '$transformedData' (hex: $hex)")
                    onAction(ServerTerminalAction.OnSendCommand(transformedData))

                    // 发送后重置修饰键状态
                    if (currentState.isCtrlPressed || currentState.isAltPressed) {
                        onAction(ServerTerminalAction.OnResetModifiers)
                    }
                }

                @JavascriptInterface
                fun onResize(cols: Int, rows: Int) {
                    Log.d(TAG, "Terminal resized: ${cols}x${rows}")
                    if (rows <= 1) {
                        Log.w(TAG, "Terminal height is too small! WebView might not be laid out yet.")
                    }
                }

                @JavascriptInterface
                fun onXtermReady() {
                    Log.d(TAG, "Xterm is ready!")
                    xtermReady = true
                }
            }, "AndroidBridge")

            // 加载terminal.html
            loadUrl("file:///android_asset/terminal.html")
        }
    }

    // 等待xterm就绪后再初始化连接
    LaunchedEffect(xtermReady) {
        if (xtermReady) {
            Log.d(TAG, "Xterm ready, initializing SSH connection")

            // 只初始化连接，不设置尺寸
            // 让 terminal.html 的初始化逻辑自己处理尺寸
            onAction(
                ServerTerminalAction.OnInitConnection(baseUrl, selectedServerId, connectTo)
            )
        }
    }

    // 监听字体大小变化并更新
    LaunchedEffect(state.fontSize, xtermReady) {
        if (xtermReady) {
            webView.evaluateJavascript("if(window.xterm && window.xterm.setFontSize) { window.xterm.setFontSize(${state.fontSize}); }") { result ->
                Log.d(TAG, "Set font size to ${state.fontSize}, result: $result")
            }
        }
    }

    // 收集终端数据流并写入xterm（使用Base64编码避免转义问题）
    LaunchedEffect(xtermReady) {
        if (!xtermReady) return@LaunchedEffect

        viewModel.terminalDataFlow.collect { data ->
            try {
                // 将数据转为字节数组，然后Base64编码
                val bytes = data.toByteArray(Charsets.UTF_8)
                val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

                // 调用JavaScript的writeBase64方法，内部将Base64解码为Uint8Array
                val jsCode = "if(window.xterm && window.xterm.writeBase64) { window.xterm.writeBase64('$base64Data'); }"

                webView.post {
                    webView.evaluateJavascript(jsCode) { result ->
                        if (result == "null" || result == null) {
                            Log.w(TAG, "writeBase64 returned null")
                        }
                    }
                }

                // 打印hex以便调试
                val hex = bytes.take(20).joinToString(" ") { byte ->
                    String.format("%02X", byte)
                }
                val preview = data.take(50).replace("\n", "\\n").replace("\r", "\\r").replace("\u001B", "\\e")
                if (bytes.size > 20) {
                    Log.d(TAG, "Wrote ${bytes.size} bytes to terminal: $preview (hex: $hex...)")
                } else {
                    Log.d(TAG, "Wrote ${bytes.size} bytes to terminal: $preview (hex: $hex)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error writing to terminal", e)
            }
        }
    }

    // 组件销毁时断开连接
    DisposableEffect(lifecycleOwner) {
        onDispose {
            Log.d(TAG, "Disposing terminal, disconnecting...")
            onAction(ServerTerminalAction.OnDisconnect)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding() // 关键：应用在整个Column，让整个界面上移
    ) {
        // WebView显示xterm终端
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    // 不在这里设置尺寸，让 terminal.html 自己初始化
                    // 布局变化时会通过 onLayoutChangeListener 触发调整
                }
            )

            // 加载指示器
            if (state.isLoading || !xtermReady) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 终端常用按键工具栏
        TerminalKeyboardToolbar(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TerminalKeyboardToolbar(
    state: ServerTerminalState,
    onAction: (ServerTerminalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // 参考截图的两行布局
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 1.dp, vertical = 4.dp)
    ) {
        // 第一行：ESC / | - HOME ↑ END PGUP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TerminalKey(
                text = "ESC",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("ESC")) }
            )
            TerminalKey(
                text = "/",
                onClick = { onAction(ServerTerminalAction.OnSendCommand("/")) }
            )
            TerminalKey(
                text = "|",
                onClick = { onAction(ServerTerminalAction.OnSendCommand("|")) }
            )
            TerminalKey(
                text = "-",
                onClick = { onAction(ServerTerminalAction.OnSendCommand("-")) }
            )
            TerminalKey(
                icon = { Icon(Icons.Filled.KeyboardArrowUp, "Up") },
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("UP")) }
            )
            TerminalKey(
                text = "HOME",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("HOME")) }
            )
            TerminalKey(
                text = "END",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("END")) }
            )
            TerminalKey(
                text = "PGUP",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("PAGEUP")) }
            )
        }

        // 第二行：TAB CTRL ALT ← ↓ → PGDN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TerminalKey(
                text = "TAB",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("TAB")) }
            )
            TerminalKey(
                text = "CTRL",
                isPressed = state.isCtrlPressed,
                onClick = { onAction(ServerTerminalAction.OnToggleCtrl) }
            )
            TerminalKey(
                text = "ALT",
                isPressed = state.isAltPressed,
                onClick = { onAction(ServerTerminalAction.OnToggleAlt) }
            )
            TerminalKey(
                icon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Left") },
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("LEFT")) }
            )
            TerminalKey(
                icon = { Icon(Icons.Filled.KeyboardArrowDown, "Down") },
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("DOWN")) }
            )
            TerminalKey(
                icon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Right") },
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("RIGHT")) }
            )
            TerminalKey(
                text = "PGDN",
                onClick = { onAction(ServerTerminalAction.OnSendSpecialKey("PAGEDOWN")) }
            )
        }
    }
}

@Composable
fun TerminalKey(
    text: String? = null,
    icon: @Composable (() -> Unit)? = null,
    isPressed: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(32.dp)
            .padding(horizontal = 1.dp),  // 减小水平padding避免挤压
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        when {
            icon != null -> icon()
            text != null -> Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}
