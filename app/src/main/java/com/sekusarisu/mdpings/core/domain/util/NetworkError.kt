package com.sekusarisu.mdpings.core.domain.util

enum class NetworkError: Error {
    Forbidden,
    REQUEST_TIMEOUT,
    TOO_MANY_REQUEST,
    NO_INTERNET,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN
}