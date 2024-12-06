package com.sekusarisu.mdpings_v0.vpings.presentation.user_login

import com.sekusarisu.mdpings_v0.core.domain.util.NetworkError

sealed interface LoginEvent {
    data class Error(val error: NetworkError): LoginEvent
}