package ua.anastasiia.finesapp.entity

data class Fine(
    val id: String,
    val car: Car,
    val trafficTicket: TrafficTicket
) {

    data class Car(
        val plate: String,
        val make: String,
        val model: String,
        val color: String
    )

    data class TrafficTicket(
        val id: String? = null,
        val locationLat: Double,
        val locationLon: Double,
        val dateTime: String,
        val photoUrl: String,
        val valid: Boolean,
        val violations: List<Violation>
    ) {

        data class Violation(
            val description: String,
            val price: Double
        )
    }
}