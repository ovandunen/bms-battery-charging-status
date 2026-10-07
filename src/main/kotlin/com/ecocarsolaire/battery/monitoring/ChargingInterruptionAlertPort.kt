package com.ecocarsolaire.battery.monitoring

interface ChargingInterruptionAlertPort {
    suspend fun alertChargingInterrupted()
}
