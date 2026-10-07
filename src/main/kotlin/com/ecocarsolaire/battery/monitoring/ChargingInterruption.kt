package com.ecocarsolaire.battery.monitoring

import java.time.Duration
import java.time.Instant

/**
 * Alerts when a pack that was charging (voltage above 200 V and current above 0 A)
 * then stays under 5 V with no charging current for 2 seconds.
 * At most one alert every 5 minutes while the fault continues.
 *
 * Voltage and current are the decoded pack values. CAN1 `181028F4` is already scaled
 * to volts and amperes before it reaches this class.
 */
class ChargingInterruption {
    private var wasCharging = false
    private var faultSince: Instant? = null
    private var lastAlertAt: Instant? = null

    fun chargingInterrupted(voltageV: Double, currentA: Double, at: Instant): Boolean {
        if (voltageV > CHARGING_VOLTAGE_V && currentA > 0.0) {
            wasCharging = true
            faultSince = null
            return false
        }
        val fault = wasCharging && voltageV < FAULT_VOLTAGE_V && kotlin.math.abs(currentA) < CURRENT_AT_REST_A
        if (!fault) {
            faultSince = null
            return false
        }
        val since = faultSince ?: at.also { faultSince = it }
        if (Duration.between(since, at) < HOLD) return false
        val previous = lastAlertAt
        if (previous != null && Duration.between(previous, at) < COOLDOWN) return false
        lastAlertAt = at
        return true
    }

    private companion object {
        const val CHARGING_VOLTAGE_V = 200.0
        const val FAULT_VOLTAGE_V = 5.0
        const val CURRENT_AT_REST_A = 0.05
        val HOLD: Duration = Duration.ofSeconds(2)
        val COOLDOWN: Duration = Duration.ofMinutes(5)
    }
}
