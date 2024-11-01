package com.example.mdpings.vpings.domain

import com.example.mdpings.core.domain.util.NetworkError
import com.example.mdpings.core.domain.util.Result

interface ServerDataSource {
    suspend fun getServers(apiUrl: String, token: String): Result<List<Server>, NetworkError>
}