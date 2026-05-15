package com.example.nammapustaka.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val grade: String,
    val totalPagesRead: Int = 0 
)
