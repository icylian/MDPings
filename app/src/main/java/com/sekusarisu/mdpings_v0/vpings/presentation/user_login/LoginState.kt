package com.sekusarisu.mdpings_v0.vpings.presentation.user_login

import androidx.compose.runtime.Immutable
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi

@Immutable
data class LoginState(
    val isLoading: Boolean = false,
    val servers: List<ServerUi> = emptyList()
)