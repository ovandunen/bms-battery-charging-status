package com.ecocarsolaire.battery.monitoring

import java.time.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargingInterruptionTest {

    private val start = Instant.parse("2026-10-07T10:00:00Z")
    private val interruption = ChargingInterruption()

    @Test
    fun chargingInterrupted_whenVoltageDropsBeforeCharging_doesNotAlert() {
        assertFalse(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(2)))
    }

    @Test
    fun chargingInterrupted_whenFaultIsShorterThanTwoSeconds_doesNotAlert() {
        interruption.chargingInterrupted(400.0, 20.0, start)
        interruption.chargingInterrupted(0.0, 0.0, start)
        assertFalse(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(1)))
    }

    @Test
    fun chargingInterrupted_whenChargingThenVoltageAndCurrentStayAtZeroForTwoSeconds_alerts() {
        interruption.chargingInterrupted(400.0, 20.0, start)
        interruption.chargingInterrupted(0.0, 0.0, start)
        assertTrue(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(2)))
    }

    @Test
    fun chargingInterrupted_whenVoltageRecoversBeforeTwoSeconds_doesNotAlert() {
        interruption.chargingInterrupted(400.0, 20.0, start)
        interruption.chargingInterrupted(1.0, 0.0, start.plusSeconds(1))
        interruption.chargingInterrupted(400.0, 20.0, start.plusMillis(1500))
        interruption.chargingInterrupted(1.0, 0.0, start.plusMillis(1600))
        assertFalse(interruption.chargingInterrupted(1.0, 0.0, start.plusMillis(3500)))
    }

    @Test
    fun chargingInterrupted_whenFaultContinues_sendsAtMostOneAlertPerFiveMinutes() {
        interruption.chargingInterrupted(400.0, 20.0, start)
        interruption.chargingInterrupted(0.0, 0.0, start)
        assertTrue(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(2)))
        assertFalse(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(3)))
        assertFalse(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(5 * 60)))
        assertTrue(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(5 * 60 + 2)))
    }

    @Test
    fun chargingInterrupted_whenVoltageIsZeroButCurrentStillPositive_doesNotAlert() {
        interruption.chargingInterrupted(400.0, 20.0, start)
        assertFalse(interruption.chargingInterrupted(0.0, 5.0, start.plusSeconds(3)))
    }

    @Test
    fun chargingInterrupted_atTheVoltageBoundaries_usesTheStatedThresholds() {
        interruption.chargingInterrupted(200.0, 20.0, start)
        interruption.chargingInterrupted(0.0, 0.0, start)
        assertFalse(interruption.chargingInterrupted(0.0, 0.0, start.plusSeconds(2)))

        val charging = ChargingInterruption()
        charging.chargingInterrupted(200.1, 20.0, start)
        charging.chargingInterrupted(5.0, 0.0, start)
        assertFalse(charging.chargingInterrupted(5.0, 0.0, start.plusSeconds(2)))
        charging.chargingInterrupted(4.9, 0.0, start.plusSeconds(2))
        assertTrue(charging.chargingInterrupted(4.9, 0.0, start.plusSeconds(4)))
    }
}
