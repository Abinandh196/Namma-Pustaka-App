package com.example.nammapustaka.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nammapustaka.data.entity.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY totalPagesRead DESC LIMIT 10")
    fun getLeaderboard(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Int): Flow<Student?>

    @Query("SELECT * FROM students WHERE name = :name LIMIT 1")
    suspend fun getStudentByName(name: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)
}
