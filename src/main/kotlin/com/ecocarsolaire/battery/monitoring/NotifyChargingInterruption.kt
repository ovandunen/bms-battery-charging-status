package com.ecocarsolaire.battery.monitoring

import java.time.Clock
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotifyChargingInterruption(
    private val alerts: ChargingInterruptionAlertPort,
    private val clock: Clock,
    private val scope: CoroutineScope,
    private val interruption: ChargingInterruption = ChargingInterruption(),
    private val onAlert: () -> Unit = {},
    private val onFailure: (Throwable) -> Unit = {},
) {
    fun batteryMeasured(voltageV: Double, currentA: Double) {
        if (!interruption.chargingInterrupted(voltageV, currentA, Instant.now(clock))) return
        onAlert()
        scope.launch {
            runCatching { alerts.alertChargingInterrupted() }.onFailure(onFailure)
        }
    }
}
