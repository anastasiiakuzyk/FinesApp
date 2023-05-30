package ua.anastasiia.finesapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ua.anastasiia.finesapp.data.entity.CarInfo
import ua.anastasiia.finesapp.data.entity.Fine
import ua.anastasiia.finesapp.data.entity.FineViolationCrossRef
import ua.anastasiia.finesapp.data.entity.Violation

data class FineWithCarAndViolations(
    @Embedded
    val fine: Fine, @Relation(
        parentColumn = "fine_id",
        entityColumn = "id"
    )
    val carInfo: CarInfo,
    @Relation(
        parentColumn = "fine_id",
        entity = Violation::class,
        entityColumn = "violation_id",
        associateBy = Junction(
            value = FineViolationCrossRef::class,
            parentColumn = "fine_id",
            entityColumn = "violation_id"
        )
    )
    val violations: List<Violation>
)












