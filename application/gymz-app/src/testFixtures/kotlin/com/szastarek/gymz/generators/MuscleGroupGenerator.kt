package com.szastarek.gymz.generators

import com.szastarek.gymz.domain.model.muscle.group.MuscleGroup
import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list

@OptIn(DelicateKotest::class)
object MuscleGroupGenerator {
    fun primary(range: IntRange = 1..3): Arb<List<MuscleGroup>> =
        Arb.list(Arb.enum<MuscleGroup>(), range).distinct()

    fun secondary(primary: List<MuscleGroup>, range: IntRange = 1..3): Arb<List<MuscleGroup>> =
        Arb.list(Arb.enum<MuscleGroup>().filter { it !in primary }, range).distinct()
}
