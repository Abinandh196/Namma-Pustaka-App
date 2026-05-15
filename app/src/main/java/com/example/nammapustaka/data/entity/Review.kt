package com.example.nammapustaka.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val studentId: Int,
    val rating: Int, 
    val reviewText: String
)
