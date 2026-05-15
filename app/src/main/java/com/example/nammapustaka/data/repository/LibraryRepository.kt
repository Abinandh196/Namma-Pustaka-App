package com.example.nammapustaka.data.repository

import com.example.nammapustaka.data.dao.BookDao
import com.example.nammapustaka.data.dao.LibraryDao
import com.example.nammapustaka.data.dao.StudentDao
import com.example.nammapustaka.data.entity.Book
import com.example.nammapustaka.data.entity.BorrowTransaction
import com.example.nammapustaka.data.entity.Review
import com.example.nammapustaka.data.entity.Student

class LibraryRepository(
    private val bookDao: BookDao,
    private val studentDao: StudentDao,
    private val libraryDao: LibraryDao
) {
    // Books
    fun getAllBooks() = bookDao.getAllBooks()
    fun getBooksByCategory(category: String) = bookDao.getBooksByCategory(category)
    fun searchBooks(query: String) = bookDao.searchBooks(query)
    fun getBookById(id: String) = bookDao.getBookById(id)
    suspend fun insertBook(book: Book) = bookDao.insertBook(book)
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    // Students
    fun getAllStudents() = studentDao.getAllStudents()
    fun getLeaderboard() = studentDao.getLeaderboard()
    fun getStudentById(id: Int) = studentDao.getStudentById(id)
    suspend fun getStudentByName(name: String) = studentDao.getStudentByName(name)
    suspend fun insertStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)

    // Library Transactions
    fun getAllTransactions() = libraryDao.getAllTransactions()
    fun getTransactionsForStudent(studentId: Int) = libraryDao.getTransactionsForStudent(studentId)
    fun getTransactionsForBook(bookId: String) = libraryDao.getTransactionsForBook(bookId)
    fun getOverdueTransactions(currentDateMillis: Long) = libraryDao.getOverdueTransactions(currentDateMillis)
    suspend fun issueBook(transaction: BorrowTransaction) = libraryDao.insertTransaction(transaction)
    suspend fun returnBook(transaction: BorrowTransaction) = libraryDao.updateTransaction(transaction)

    // Reviews
    fun getReviewsForBook(bookId: String) = libraryDao.getReviewsForBook(bookId)
    suspend fun insertReview(review: Review) = libraryDao.insertReview(review)
}
