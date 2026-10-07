package com.ecocarsolaire.battery.monitoring

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotifyChargingInterruptionTest {

    @Test
    fun batteryMeasured_whenFaultHeldTwoSeconds_sendsOneAlert() = runTest {
        val sent = CountingAlert()
        val clock = SettableClock(Instant.parse("2026-10-07T10:00:00Z"))
        val notify = NotifyChargingInterruption(
            alerts = sent,
            clock = clock,
            scope = this,
            interruption = ChargingInterruption(),
        )

        notify.batteryMeasured(400.0, 20.0)
        notify.batteryMeasured(0.0, 0.0)
        clock.now = Instant.parse("2026-10-07T10:00:02Z")
        notify.batteryMeasured(0.0, 0.0)
        advanceUntilIdle()

        assertEquals(1, sent.calls)
    }

    private class CountingAlert : ChargingInterruptionAlertPort {
        var calls = 0
        override suspend fun alertChargingInterrupted() {
            calls += 1
        }
    }

    private class SettableClock(var now: Instant) : java.time.Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId): java.time.Clock = this
        override fun instant(): Instant = now
    }
}
