package com.example.nammapustaka.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: String, // ISBN or custom ID
    val title: String,
    val author: String,
    val category: String, // Story, Science, History
    val summaryEnglish: String = "",
    val summaryKannada: String,
    val pages: Int = 100, // Important for leaderboard tracking points
    val totalCopies: Int = 1,
    val availableCopies: Int = 1
)
