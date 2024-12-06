package com.sekusarisu.mdpings_v0.vpings.data.app_settings

import android.util.Log
import androidx.datastore.core.Serializer
import com.sekusarisu.mdpings_v0.vpings.domain.AppSettings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            val bytes = input.readBytes()
            Log.d("InstanceManager", "Read raw data: ${bytes.decodeToString()}")
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = bytes.decodeToString()
            )
        } catch (e: SerializationException) {
            Log.e("InstanceManager", "Error reading data", e)
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: AppSettings,
        output: OutputStream
    ) {
        try {
            val jsonString = Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t
            )
            Log.d("InstanceManager", "Writing data: $jsonString")
            output.write(jsonString.encodeToByteArray())
        } catch (e: Exception) {
            Log.e("InstanceManager", "Error writing data", e)
        }
    }
}

