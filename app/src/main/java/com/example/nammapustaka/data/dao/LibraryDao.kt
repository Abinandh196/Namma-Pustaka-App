package com.example.nammapustaka.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nammapustaka.data.entity.BorrowTransaction
import com.example.nammapustaka.data.entity.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM transactions ORDER BY issueDateMillis DESC")
    fun getAllTransactions(): Flow<List<BorrowTransaction>>

    @Query("SELECT * FROM transactions WHERE studentId = :studentId")
    fun getTransactionsForStudent(studentId: Int): Flow<List<BorrowTransaction>>
    
    @Query("SELECT * FROM transactions WHERE bookId = :bookId")
    fun getTransactionsForBook(bookId: String): Flow<List<BorrowTransaction>>

    @Query("SELECT * FROM transactions WHERE status = 'ISSUED' AND dueDateMillis < :currentDateMillis")
    fun getOverdueTransactions(currentDateMillis: Long): Flow<List<BorrowTransaction>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: BorrowTransaction)
    
    @Update
    suspend fun updateTransaction(transaction: BorrowTransaction)

    @Query("SELECT * FROM reviews WHERE bookId = :bookId")
    fun getReviewsForBook(bookId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)
}
