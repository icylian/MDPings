package com.sekusarisu.mdpings_v0.vpings.presentation.user_login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekusarisu.mdpings_v0.ui.theme.MDPingsTheme
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import com.sekusarisu.mdpings_v0.vpings.domain.Instance
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsAction
import com.sekusarisu.mdpings_v0.vpings.presentation.app_settings.AppSettingsState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoad: (AppSettingsAction) -> Unit,
    appSettingsState: AppSettingsState,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToServer: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var selectedInstanceIndex by remember { mutableIntStateOf(0) }
    var openAlertDialog by remember { mutableStateOf("") }
    var deleteInstanceIndex by remember { mutableIntStateOf(999) }
    val activated = appSettingsState.appSettings.activeInstance

    when (openAlertDialog) {
        "Add" -> {
            CredentialDialog(
                index = selectedInstanceIndex,
                onDismissRequest = {
                    openAlertDialog = ""
                },
                dialogTitle = "Create",
                dialogText = "请输入一个哪吒监控的API实例、取一个容易记住的名字，并点击Test测试服务器连接状况",
                icon = Icons.Rounded.Add,
                state = state,
                onAction = onAction,
                instance = Instance("", "", "")
            )
        }
        "Edit" -> {
            CredentialDialog(
                index = selectedInstanceIndex,
                onDismissRequest = {
                    openAlertDialog = ""
                },
                dialogTitle = "Edit",
                dialogText = "编辑当前实例配置",
                icon = Icons.Rounded.Edit,
                state = state,
                onAction = onAction,
                instance = appSettingsState.appSettings.instances[selectedInstanceIndex],
            )
        }
        "Delete" -> {
            DeleteConfirmDialog(
                appSettingsState = appSettingsState,
                deleteInstanceIndex = deleteInstanceIndex,
                onDismissRequest = {
                    openAlertDialog = ""
                },
                onAction = onAction,
                onLoad = onLoad
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openAlertDialog = "Add"
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        if (appSettingsState.appSettings.instances.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
            ) {
//                item(
//                    key = "Test",
//                    content = { Text("Test") }
//                )
                itemsIndexed(
                    items = appSettingsState.appSettings.instances,
                ) { index, instance ->
                    ListItem(
                        modifier = Modifier
                            .animateItem()
                            .clickable(
                            onClick = {
                                scope.launch{
                                    onLoad(
                                        AppSettingsAction.OnChangeActiveInstance(index)
                                    )
                                    onNavigateToServer()
                                }
                            }
                        ),
                        headlineContent = {
                            Text(
                                text = instance.name,
                                color = MaterialTheme.colorScheme.secondary
                            ) },
                        supportingContent = {
                            Text(text = instance.apiUrl,
                                color = MaterialTheme.colorScheme.tertiary
                            ) },
                        leadingContent = {
                            Icon(
                                imageVector =
                                if (activated == index) Icons.Rounded.Star
                                else Icons.Rounded.StarBorder,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        trailingContent = {
                            Row {
                                IconButton(
                                    onClick = {
                                        selectedInstanceIndex = index
                                        openAlertDialog = "Edit"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        deleteInstanceIndex = index
                                        openAlertDialog = "Delete"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding() - 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "请先添加一个哪吒监控的API实例~"
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    appSettingsState: AppSettingsState,
    deleteInstanceIndex: Int,
    onDismissRequest: () -> Unit,
    onAction: (LoginAction) -> Unit,
    onLoad: (AppSettingsAction) -> Unit
) {

    val scope = rememberCoroutineScope()
    val activeInstance = appSettingsState.appSettings.activeInstance
    val instancesCount = appSettingsState.appSettings.instances.count()

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null
            )
        },
        title = {
            Text("Confirm")
        },
        text = {
            Text("确定要删除吗？这个操作不可恢复！")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        if (instancesCount - 2 < activeInstance) {
                            onLoad(
                                AppSettingsAction.OnChangeActiveInstance(0)
                            )
                        }
                        onAction(
                            LoginAction.OnDeleteClick(deleteInstanceIndex)
                        )
                        onDismissRequest()
                    }
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
fun CredentialDialog(
    index: Int,
    instance: Instance,
    onDismissRequest: () -> Unit,
    onAction: (LoginAction) -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    state: LoginState
) {

    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf(instance.name) }
    var apiUrl by rememberSaveable { mutableStateOf(instance.apiUrl) }
    var apiToken by rememberSaveable { mutableStateOf(instance.apiToken) }

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = dialogText)
                Spacer(Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .alpha(0.6f),
                    text = "INSTANCE NAME",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = name,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Nezha instance name",
                            modifier = Modifier.alpha(0.4f)
                        )
                    },
                    shape = ShapeDefaults.ExtraLarge,
                    onValueChange = {
                        name = it
                        onAction(
                            LoginAction.OnCredentialsChange
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Text(
                    modifier = Modifier
                        .alpha(0.6f),
                    text = "API BACKEND",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = apiUrl,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "https://your.nezha.api.com/",
                            modifier = Modifier.alpha(0.4f)
                        )
                    },
                    shape = ShapeDefaults.ExtraLarge,
                    onValueChange = {
                        apiUrl = it
                        onAction(
                            LoginAction.OnCredentialsChange
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Text(
                    modifier = Modifier
                        .alpha(0.6f),
                    text = "TOKEN",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = apiToken,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "YOUR_NEZHA_API_TOKEN",
                            modifier = Modifier.alpha(0.4f)
                        )
                    },
                    shape = ShapeDefaults.ExtraLarge,
                    onValueChange = {
                        apiToken = it
                        onAction(
                            LoginAction.OnCredentialsChange
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TestButton(
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        onClick = {
                            onAction(
                                LoginAction.OnTestClick(apiUrl, apiToken)
                            )
                        },
                        state = state
                    )
                    Spacer(modifier = Modifier.weight(0.2f))
                    SaveButton(
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                if (dialogTitle == "Create") {
                                    onAction(
                                        LoginAction.OnSaveClicked(name, apiUrl, apiToken)
                                    )
                                }
                                if (dialogTitle == "Edit") {
                                    onAction(
                                        LoginAction.OnEditSaveClicked(index, name, apiUrl, apiToken)
                                    )
                                }
                                onAction(
                                    LoginAction.OnCredentialsChange
                                )
                                onDismissRequest()
                            }

                        },
                        state = state
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
private fun TestButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    state: LoginState
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!state.servers.isEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Check"
                )
            } else if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = LocalContentColor.current
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.NetworkCheck,
                    contentDescription = "Login"
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "Test"
            )
        }
    }
}

@Composable
private fun SaveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    state: LoginState,
) {

    val scope = rememberCoroutineScope()

    Button(
        enabled = !state.servers.isEmpty(),
        onClick = onClick,
        modifier = modifier
    ) {
        Row {
            Icon(
                imageVector = Icons.Rounded.Save,
                contentDescription = "Save"
            )
            Spacer(Modifier.width(4.dp))
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "Save"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MDPingsTheme {
        LoginScreen(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            state = LoginState(),
            onAction = {},
            onNavigateToServer = {},
            appSettingsState = mockAppSettingsState1,
            onLoad = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview2() {
    MDPingsTheme {
        LoginScreen(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            state = LoginState(),
            onAction = {},
            onNavigateToServer = {},
            appSettingsState = mockAppSettingsState2,
            onLoad = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginDialogPreview() {
    MDPingsTheme {
        CredentialDialog(
            index = 0,
            onDismissRequest = {},
            dialogTitle = "Login",
            dialogText = "请输入一个哪吒监控的API实例、取一个容易记住的名字，并点击Test测试服务器连接状况。",
            icon = Icons.AutoMirrored.Rounded.Login,
            state = LoginState(),
            onAction = {},
            instance = Instance(
                name = "",
                apiUrl = "",
                apiToken = ""
            ),
        )
    }
}

private val mockAppSettingsState1 = AppSettingsState(
    appSettings = AppSettings(
        activeInstance = 0,
        instances = persistentListOf(
            Instance(
                name = "test1",
                apiUrl = "https://test1.example.com/",
                apiToken = "AABBCCDDEEFFGGAABBCCDDEEFFGGAABBCCDDEEFFGG"
            ),
            Instance(
                name = "test2",
                apiUrl = "https://test2.example.com/",
                apiToken = "AABBCCDDEEFFGGAABBCCDDEEFFGGAABBCCDDEEFFGG"
            )
            ,
            Instance(
                name = "test3",
                apiUrl = "https://test3.example.com/",
                apiToken = "AABBCCDDEEFFGGAABBCCDDEEFFGGAABBCCDDEEFFGG"
            )
        ),
        refreshInterval = 5000
    )
)

private val mockAppSettingsState2 = AppSettingsState(
    appSettings = AppSettings(
        activeInstance = 0,
        instances = persistentListOf(),
        refreshInterval = 5000
    )
)