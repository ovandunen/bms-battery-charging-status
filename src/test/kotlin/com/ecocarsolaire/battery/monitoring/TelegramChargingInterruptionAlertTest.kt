package com.ecocarsolaire.battery.monitoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TelegramChargingInterruptionAlertTest {

    @Test
    fun telegramSendRequest_whenTokenOrChatIsBlank_doesNotBuildARequest() {
        assertNull(telegramSendRequest(" ", "99", CHARGING_INTERRUPTED_ALERT))
        assertNull(telegramSendRequest("123:ABC", "", CHARGING_INTERRUPTED_ALERT))
    }

    @Test
    fun telegramSendRequest_postsTheAlertToTheBotSendMessageEndpoint() {
        val request = telegramSendRequest("123:ABC", "99", CHARGING_INTERRUPTED_ALERT)!!

        assertEquals("POST", request.method)
        assertEquals("https://api.telegram.org/bot123:ABC/sendMessage", request.url.toString())
        assertTrue(request.body != null)
    }
}
