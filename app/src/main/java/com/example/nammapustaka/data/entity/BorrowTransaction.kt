package com.example.nammapustaka.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class BorrowTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val bookId: String,
    val issueDateMillis: Long,
    val dueDateMillis: Long,
    val returnDateMillis: Long? = null,
    val status: String // ISSUED, RETURNED, OVERDUE
)
