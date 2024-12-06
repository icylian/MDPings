package com.sekusarisu.mdpings_v0.core.data.networking

import com.sekusarisu.mdpings_v0.core.domain.util.NetworkError
import com.sekusarisu.mdpings_v0.core.domain.util.Result
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import java.lang.Exception
import kotlin.coroutines.coroutineContext

// 检查并返回发起连接前的错误
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        // Catching CancellationException: https://youtu.be/VWlwkqmTLHc?si=KGa_GMuFrJx-ghQI
        coroutineContext.ensureActive()
        return Result.Error(NetworkError.UNKNOWN)
    }

    return responseToResult(response)

}