package com.example.nammapustaka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammapustaka.ai.GeminiHelper
import com.example.nammapustaka.data.entity.Book
import com.example.nammapustaka.data.entity.Student
import com.example.nammapustaka.data.repository.LibraryRepository
import com.example.nammapustaka.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

import com.example.nammapustaka.data.UserManager

class MainViewModel(
    private val repository: LibraryRepository,
    private val userManager: UserManager
) : ViewModel() {
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<Student>>(emptyList())
    val leaderboard: StateFlow<List<Student>> = _leaderboard.asStateFlow()

    private val _allStudents = MutableStateFlow<List<Student>>(emptyList())
    val allStudents: StateFlow<List<Student>> = _allStudents.asStateFlow()

    private val _transactions = MutableStateFlow<List<com.example.nammapustaka.data.entity.BorrowTransaction>>(emptyList())
    val transactions: StateFlow<List<com.example.nammapustaka.data.entity.BorrowTransaction>> = _transactions.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(userManager.getLoggedInUserId() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<Student?>(null)
    val currentUser: StateFlow<Student?> = _currentUser.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isDarkMode = MutableStateFlow(userManager.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val geminiHelper = GeminiHelper(Constants.GEMINI_API_KEY)

    init {
        loadBooks()
        loadLeaderboard()
        loadAllStudents()
        loadTransactions()
        val savedUserId = userManager.getLoggedInUserId()
        if (savedUserId != null) {
            viewModelScope.launch {
                repository.getStudentById(savedUserId).collectLatest { _currentUser.value = it }
            }
        }
    }

    private fun loadAllStudents() {
        viewModelScope.launch {
            repository.getAllStudents().collectLatest { _allStudents.value = it }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions().collectLatest { _transactions.value = it }
        }
    }

    fun login(name: String, grade: String) {
        viewModelScope.launch {
            val existingStudent = repository.getStudentByName(name)
            val id = if (existingStudent != null) {
                existingStudent.id
            } else {
                val student = Student(name = name, grade = grade, totalPagesRead = 0)
                repository.insertStudent(student).toInt()
            }
            userManager.loginUser(id)
            _isLoggedIn.value = true
            repository.getStudentById(id).collectLatest { _currentUser.value = it }
        }
    }

    fun addStudentManually(name: String, grade: String) {
        viewModelScope.launch {
            val student = Student(name = name, grade = grade, totalPagesRead = 0)
            repository.insertStudent(student)
            // It will automatically update the leaderboard
        }
    }

    fun addBookManually(id: String, title: String, author: String, category: String, summaryEn: String, summaryKn: String) {
        viewModelScope.launch {
            val book = Book(
                id = id,
                title = title,
                author = author,
                category = category,
                summaryEnglish = summaryEn,
                summaryKannada = summaryKn,
                pages = 100,
                totalCopies = 1,
                availableCopies = 1
            )
            repository.insertBook(book)
        }
    }

    fun issueBookManually(bookId: String, studentId: Int, issueDate: Long, dueDate: Long) {
        viewModelScope.launch {
            val book = repository.getBookById(bookId).firstOrNull()
            if (book != null && book.availableCopies > 0) {
                val transaction = com.example.nammapustaka.data.entity.BorrowTransaction(
                    studentId = studentId,
                    bookId = bookId,
                    issueDateMillis = issueDate,
                    dueDateMillis = dueDate,
                    status = "ISSUED"
                )
                repository.issueBook(transaction)
                val updatedBook = book.copy(availableCopies = book.availableCopies - 1)
                repository.updateBook(updatedBook)
                
                val student = repository.getStudentById(studentId).firstOrNull()
                if (student != null) {
                    val updatedUser = student.copy(totalPagesRead = student.totalPagesRead + book.pages)
                    repository.updateStudent(updatedUser)
                }
            }
        }
    }

    fun logout() {
        userManager.logout()
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    fun setDarkMode(isDark: Boolean) {
        userManager.setDarkMode(isDark)
        _isDarkMode.value = isDark
    }

    fun borrowBook(bookId: String) {
        viewModelScope.launch {
            val book = repository.getBookById(bookId).firstOrNull()
            val user = _currentUser.value
            if (book != null && user != null && book.availableCopies > 0) {
                val issueDate = System.currentTimeMillis()
                val dueDate = issueDate + java.util.concurrent.TimeUnit.DAYS.toMillis(7) // 7 days default
                val transaction = com.example.nammapustaka.data.entity.BorrowTransaction(
                    studentId = user.id,
                    bookId = bookId,
                    issueDateMillis = issueDate,
                    dueDateMillis = dueDate,
                    status = "ISSUED"
                )
                repository.issueBook(transaction)

                val updatedBook = book.copy(availableCopies = book.availableCopies - 1)
                repository.updateBook(updatedBook)
                val updatedUser = user.copy(totalPagesRead = user.totalPagesRead + book.pages)
                repository.updateStudent(updatedUser)
                _currentUser.value = updatedUser
            }
        }
    }

    private var booksJob: kotlinx.coroutines.Job? = null

    private fun loadBooks() {
        booksJob?.cancel()
        booksJob = viewModelScope.launch {
            repository.getAllBooks().collectLatest { _books.value = it }
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            repository.getLeaderboard().collectLatest { students ->
                _leaderboard.value = students.filter { it.grade.equals("Student", ignoreCase = true) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        booksJob?.cancel()
        booksJob = viewModelScope.launch {
            if (query.isBlank()) {
                val currentCategory = _selectedCategory.value
                if (currentCategory == "All") {
                    repository.getAllBooks().collectLatest { _books.value = it }
                } else {
                    repository.getBooksByCategory(currentCategory).collectLatest { _books.value = it }
                }
            } else {
                repository.searchBooks(query).collectLatest { _books.value = it }
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        booksJob?.cancel()
        booksJob = viewModelScope.launch {
            if (category == "All") {
                repository.getAllBooks().collectLatest { _books.value = it }
            } else {
                repository.getBooksByCategory(category).collectLatest { _books.value = it }
            }
        }
    }

    fun getBookById(id: String) = repository.getBookById(id)

    suspend fun generateSummaryFor(title: String, author: String): String {
        return geminiHelper.getKannadaSummary(title, author)
    }
}
