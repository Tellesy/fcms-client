# FCMS FX Houses Client (Kotlin/Java SDK)

A dedicated JVM SDK for FX houses (companies) using FCMS APIs. Uses a **separate token** from the bank client.

- Group: `io.github.tellesy`
- Artifact: `fcms-fx-houses-client`
- Version: `1.0.0`
- JVM: Java 21+

## Add Dependency

```kotlin
repositories { mavenCentral() }
dependencies { implementation("io.github.tellesy:fcms-fx-houses-client:1.0.0") }
```

## Quick Start (Kotlin)

```kotlin
import ly.neptune.nexus.fcms.fxhousesclient.core.FcmsFxConfig
import ly.neptune.nexus.fcms.fxhousesclient.requests.FcmsFxRequestsClients

suspend fun main() {
    val config = FcmsFxConfig(
        baseUrl = System.getenv("FCMS_BASE_URL"),
        tokenProvider = { System.getenv("FCMS_FX_TOKEN") }
    )

    val requests = FcmsFxRequestsClients.create(config)
    val queue = requests.listPendingRequests(page = 1)
    val first = queue.data.first()

    requests.approvePurchaseRequest(first.uuid!!, ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestActionRequest(ts = System.currentTimeMillis() / 1000))
    requests.processPurchaseRequest(first.uuid!!, ly.neptune.nexus.fcms.fxhousesclient.requests.model.PurchaseRequestProcessRequest(ts = System.currentTimeMillis() / 1000, type = "cash"))
}
```

## Build & Publish

- Local publish: `./gradlew -p fcms-fx-houses-client publishToMavenLocal`
- Maven Central: `./gradlew -p fcms-fx-houses-client publishAndReleaseToMavenCentral`

## Notes

- FX houses submit contracts using **`cash_price` + `bank_transfer_price` + `amount`**.
- FX houses **approve/decline/process** purchase requests. For bank transfers, the bank will also approve separately.
