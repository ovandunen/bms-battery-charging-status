package com.ecocarsolaire.battery.monitoring

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class TelegramChargingInterruptionAlert(
    private val client: OkHttpClient,
    private val token: String,
    private val chatId: String,
    private val onFailure: (Throwable) -> Unit = {},
) : ChargingInterruptionAlertPort {
    override suspend fun alertChargingInterrupted() {
        val request = telegramSendRequest(token, chatId, CHARGING_INTERRUPTED_ALERT) ?: return
        withContext(Dispatchers.IO) {
            runCatching { client.newCall(request).execute().close() }
                .onFailure(onFailure)
        }
    }
}

internal const val CHARGING_INTERRUPTED_ALERT =
    "🚨 CRITICAL FAULT: EcoCar charging interrupted. HV Battery voltage dropped to 0V. Please check vehicle immediately."

internal fun telegramSendRequest(token: String, chatId: String, text: String): Request? {
    if (token.isBlank() || chatId.isBlank()) return null
    val body = FormBody.Builder().add("chat_id", chatId).add("text", text).build()
    return Request.Builder()
        .url("https://api.telegram.org/bot$token/sendMessage")
        .post(body)
        .build()
}
