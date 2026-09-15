package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prep_lists")
data class PrepListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val itemsJson: String = "[]",
    val completedTasksJson: String = "[]",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
