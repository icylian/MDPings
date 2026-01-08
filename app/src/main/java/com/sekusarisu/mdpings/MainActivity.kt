package com.sekusarisu.mdpings

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sekusarisu.mdpings.core.presentation.util.ObserveAsEvents
import com.sekusarisu.mdpings.core.presentation.util.toString
import com.sekusarisu.mdpings.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings.vpings.presentation.AboutScreen
import com.sekusarisu.mdpings.vpings.presentation.ListDetailLayoutScreen
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsScreen
import com.sekusarisu.mdpings.vpings.presentation.app_settings.AppSettingsViewModel
import com.sekusarisu.mdpings.vpings.presentation.app_settings.child_screens.VisualSettingsScreen
import com.sekusarisu.mdpings.vpings.presentation.components.DrawerContent
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailScreen
import com.sekusarisu.mdpings.vpings.presentation.server_detail.ServerDetailViewModel
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListScreen
import com.sekusarisu.mdpings.vpings.presentation.server_list.ServerListViewModel
import com.sekusarisu.mdpings.vpings.presentation.components.MDAppTopBar
import com.sekusarisu.mdpings.vpings.presentation.user_login.LoginEvent
import com.sekusarisu.mdpings.vpings.presentation.user_login.LoginScreen
import com.sekusarisu.mdpings.vpings.presentation.user_login.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.sekusarisu.mdpings.vpings.presentation.server_terminal.ServerTerminalScreen
import com.sekusarisu.mdpings.vpings.presentation.server_terminal.ServerTerminalViewModel


class MainActivity : ComponentActivity() {

    // 音量键处理回调，可以被Screen设置
    var onVolumeKeyPressed: ((isVolumeUp: Boolean) -> Boolean)? = null

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                return onVolumeKeyPressed?.invoke(true) ?: false
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                return onVolumeKeyPressed?.invoke(false) ?: false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class,
        ExperimentalMaterial3WindowSizeClassApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 移除 enableEdgeToEdge() 以修复终端键盘遮挡问题
        setContent {

            val appSettingsViewModel = koinViewModel<AppSettingsViewModel>()
            val appSettingsState by appSettingsViewModel.state.collectAsStateWithLifecycle()

            MDPingsTheme(
                themeConfig = appSettingsState.appSettings.themeConfig
            ) {

                // TopAppBar scrollBehavior
                val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                    state = rememberTopAppBarState()
                )

                // Drawer
                val drawerState = rememberDrawerState(
                    initialValue = DrawerValue.Closed
                )
                val scope = rememberCoroutineScope()

                // States
                val loginViewModel = koinViewModel<LoginViewModel>()
                val loginState by loginViewModel.state.collectAsStateWithLifecycle()
                val serverListViewModel = koinViewModel<ServerListViewModel>()
                val serverListState by serverListViewModel.state.collectAsStateWithLifecycle()
                val serverDetailViewModel = koinViewModel<ServerDetailViewModel>()
                val serverDetailState by serverDetailViewModel.state.collectAsStateWithLifecycle()
                val serverTerminalViewModel = koinViewModel<ServerTerminalViewModel>()
                val serverTerminalState by serverTerminalViewModel.state.collectAsStateWithLifecycle()


                // Error Handle
                val context = LocalContext.current

                ObserveAsEvents(events = loginViewModel.events) { event ->
                    when (event) {
                        is LoginEvent.Error -> {
                            Toast.makeText(
                                context,
                                event.error.toString(context),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                // AppSettings
//                val baseUrl = appSettingsViewModel.getBaseUrl()
//                val apiTOKEN = appSettingsViewModel.getApiTOKEN()
//                val activeInstance = appSettingsState.appSettings.instances[appSettingsState.appSettings.activeInstance]

                // Nav && currentRoute -> topAppBarTitle
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val topAppBarTitle =
                    when {
                        currentRoute == Screen.Login.route ->
                            stringResource(R.string.appbar_title_login)
                        currentRoute == Screen.AppSettings.route ->
                            stringResource(R.string.appbar_title_appsettings)
                        currentRoute == Screen.About.route ->
                            stringResource(R.string.appbar_title_about)
                        currentRoute?.startsWith(Screen.ServerListDetailPane.route) == true -> {
                            if (serverDetailState.wsServerUi != null) {
                                "${serverListState.selectedServer!!.countryCode} ${serverListState.selectedServer!!.name}"
                            } else {
                                stringResource(R.string.app_name)
                            }
                        }
                        else -> stringResource(R.string.app_name)
                    }

                // Orientation && windowSizeClass -> ListDetailPane Navigator's maxHorizontalPartitions
                val configuration = LocalConfiguration.current
                val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                val width = windowSizeClass.widthSizeClass
                val navigator = rememberListDetailPaneScaffoldNavigator<Any>(
                    scaffoldDirective = PaneScaffoldDirective(
                        maxHorizontalPartitions = when {
                            isPortrait -> when (width) {
                                WindowWidthSizeClass.Compact -> 1
                                else -> 2
                            }
                            else -> 2
                        },
                        horizontalPartitionSpacerSize = 0.dp,
                        maxVerticalPartitions = 2,
                        verticalPartitionSpacerSize = 0.dp,
                        defaultPanePreferredWidth = 400.dp,
                        excludedBounds = emptyList()
                    )
                )

                // 在终端页面：只有当抽屉关闭时才禁用手势
                // 如果抽屉已打开，保持手势启用以便用户可以滑动关闭
                val isTerminalScreen = currentRoute?.startsWith("${Screen.ServerListDetailPane.route}/") == true
                val drawerGesturesEnabled = if (isTerminalScreen) {
                    drawerState.isOpen  // 终端页面：打开时启用手势，关闭时禁用
                } else {
                    true  // 其他页面：始终启用手势
                }

                ModalNavigationDrawer(
                    gesturesEnabled = drawerGesturesEnabled,
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerContent(
                                modifier = Modifier.width(280.dp),
                                navController = navController,
                                currentRoute = currentRoute,
                                drawerState = drawerState
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MDAppTopBar(
                                modifier = Modifier,
                                scrollBehavior = scrollBehavior,
                                title = topAppBarTitle,
                                isLoading = serverListState.isLoading,
                                onNavigationIconClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                },
                                onUserClick = {
                                    if (currentRoute != Screen.Login.route) {
                                        navController.navigate(Screen.Login.route)
                                    }
                                },
                                onAction = appSettingsViewModel::onAction,
                                appSettingsState = appSettingsState
                            )
                        }
                    ) { innerPadding ->

                        LaunchedEffect(Unit) {
                            val instance = appSettingsViewModel.getInstances()
                            if (!instance.isNullOrEmpty()) {
                                val activeInstance = instance[appSettingsViewModel.getActiveInstanceIndex() ?: 0]
                                val testResult = loginViewModel.testConnectionToBoolean(
                                    activeInstance.baseUrl,
                                    activeInstance.username,
                                    activeInstance.password
                                )
                                navController.navigate(
                                    if (testResult) Screen.ServerListDetailPane.route else Screen.Login.route
                                ) {
                                    popUpTo(Screen.Loading.route) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(
                                    Screen.Login.route
                                ) {
                                    popUpTo(Screen.Loading.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Loading.route,
                            // TODO 部分机型切换日夜间模式的时候会negative闪退？
                            modifier = Modifier.padding(top = innerPadding.calculateTopPadding() - 4.dp)
                        ) {
                            // Loading screen
                            composable(Screen.Loading.route) {
                                LoadingScreen()
                            }

                            // Auth navigation graph
                            navigation(
                                startDestination = Screen.Login.route,
                                route = Screen.Auth.route
                            ) {
                                composable(Screen.Login.route) {
                                    LoginScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        state = loginState,
                                        appSettingsState = appSettingsState,
                                        onAction = loginViewModel::onAction,
                                        onNavigateToServer = {
                                            serverListViewModel.onSwitchInstanceCleanUp()
                                            navController.navigate(Screen.ServerListDetailPane.route) {
                                                popUpTo(Screen.Auth.route) { inclusive = true }
                                            }
                                        },
                                        onLoad = appSettingsViewModel::onAction
                                    )
                                }
                            }

                            // Main navigation graph
                            navigation(
                                startDestination = Screen.ServerList.route,
                                route = Screen.Main.route
                            ) {
                                composable(Screen.ServerList.route) {
                                    ServerListScreen(
                                        state = serverListState,
                                        onAction = serverListViewModel::onAction,
                                        onNavigateToDetail = { serverId ->
                                            navController.navigate(Screen.ServerDetail.createRoute(
                                                serverId.toString()
                                            ))
                                        },
                                        onLoad = appSettingsViewModel::onAction,
                                        appSettingsState = appSettingsState,
                                    )
                                }

//                                composable(
//                                    route = Screen.ServerDetail.route,
//                                    arguments = listOf(navArgument("serverId") { type = NavType.StringType })
//                                ) { backStackEntry ->
//                                    val serverId = backStackEntry.arguments?.getString("serverId")?.toInt()
//                                    val selectedServer = serverListState.servers.first { it.id == serverId }
//                                    ServerDetailScreen(
//                                        state = serverDetailState,
//                                        selectedServerUi = selectedServer,
//                                        onAction = serverDetailViewModel::onAction,
//                                        appSettingsState = appSettingsState,
//                                    )
//                                }

                                composable(Screen.ServerListDetailPane.route) {
                                    ListDetailLayoutScreen(
                                        modifier = Modifier,
                                        navigator = navigator,
                                        serverListState = serverListState,
                                        serverDetailState = serverDetailState,
                                        appSettingsState = appSettingsState,
                                        onServerListAction = serverListViewModel::onAction,
                                        onServerDetailAction = serverDetailViewModel::onAction,
                                        onAppSettingsAction = appSettingsViewModel::onAction,
                                        onNavigateToTerminal = { serverId ->
                                            navController.navigate("${Screen.ServerListDetailPane.route}/$serverId")
                                        },
                                        onLoginAction = loginViewModel::onAction
                                    )
                                }

                                composable(
                                    route = "${Screen.ServerListDetailPane.route}/{serverId}",
                                    arguments = listOf(navArgument("serverId") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val serverId = backStackEntry.arguments?.getString("serverId")?.toIntOrNull() ?: 0
                                    ServerTerminalScreen(
                                        state = serverTerminalState,
                                        serverListState = serverListState,
                                        selectedServerId = serverId,
                                        appSettingsState = appSettingsState,
                                        onAction = serverTerminalViewModel::onAction,
                                        viewModel = serverTerminalViewModel,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                            }

                            // Settings navigation graph
                            navigation(
                                startDestination = Screen.AppSettings.route,
                                route = Screen.Settings.route
                            ) {
                                composable(Screen.AppSettings.route) {
                                    AppSettingsScreen(
                                        state = appSettingsState,
                                        onAction = appSettingsViewModel::onAction,
                                        onNavigateToLogin = {
                                            navController.navigate(Screen.Login.route)
                                        },
                                        onNavigateToVisualSettings = {
                                            navController.navigate(Screen.VisualSettings.route)
                                        }
                                    )
                                }
                                composable(Screen.VisualSettings.route) {
                                    VisualSettingsScreen(
                                        state = appSettingsState,
                                        onAction = appSettingsViewModel::onAction
                                    )
                                }
                                composable(Screen.About.route) {
                                    AboutScreen()
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Loading : Screen("loading")

    // Auth graph
    object Auth : Screen("auth")
    object Login : Screen("auth/login")

    // Main graph
    object Main : Screen("main")
    object ServerList : Screen("main/servers")
    object ServerDetail : Screen("main/servers/{serverId}") {
        fun createRoute(serverId: String) = "main/servers/$serverId"
    }
    object ServerListDetailPane : Screen("main/serverListDetail/{serverId}") {
        fun createRoute(serverId: String) = "main/serverListDetail/$serverId"
    }

    // Settings graph
    object Settings : Screen("settings")
    object AppSettings : Screen("settings/app")
    object VisualSettings : Screen("settings/visual")
    object About : Screen("settings/about")
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.initializing_mdpings))
    }
}