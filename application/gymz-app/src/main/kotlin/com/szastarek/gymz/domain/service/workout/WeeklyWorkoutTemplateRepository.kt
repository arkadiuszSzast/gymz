package com.szastarek.gymz.domain.service.workout

import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplate
import com.szastarek.gymz.domain.model.workout.WeeklyWorkoutTemplateId
import com.szastarek.gymz.shared.SaveResult
import com.szastarek.gymz.shared.page.Page
import com.szastarek.gymz.shared.page.PageQueryParameters

interface WeeklyWorkoutTemplateRepository {
    suspend fun findAll(pageQueryParameters: PageQueryParameters): Page<WeeklyWorkoutTemplate>
    suspend fun findById(id: WeeklyWorkoutTemplateId): WeeklyWorkoutTemplate?
    suspend fun save(workoutTemplate: WeeklyWorkoutTemplate): SaveResult
}
