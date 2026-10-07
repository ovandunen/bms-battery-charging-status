# bms-battery-charging-status

Plain Kotlin/JVM library. It decides when a charging pack has dropped to about 0 V, and it posts that alert to Telegram. No Android dependency.

**Coordinates:** `com.fleet.shared:bms-battery-charging-status:1.0.0-SNAPSHOT`  
**Package:** `com.ecocarsolaire.battery.monitoring`

## Consume from the BMS app (composite build)

```kotlin
includeBuild("../bms-battery-charging-status")
```

```kotlin
implementation("com.fleet.shared:bms-battery-charging-status:1.0.0-SNAPSHOT")
```

The caller passes decoded pack voltage and current. This library does not read CAN and does not start a foreground service.

`telegram_bot_token` and `telegram_chat_id` stay in the caller's configuration. A blank token or chat id sends nothing.
