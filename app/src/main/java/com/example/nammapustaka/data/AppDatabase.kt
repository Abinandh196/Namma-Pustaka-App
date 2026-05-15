package com.example.nammapustaka.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nammapustaka.data.entity.*
import com.example.nammapustaka.data.dao.*
import kotlinx.coroutines.launch

@Database(entities = [Book::class, Student::class, BorrowTransaction::class, Review::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun studentDao(): StudentDao
    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namma_pustaka_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                val bookDao = database.bookDao()
                                val studentDao = database.studentDao()

                                val books = listOf(
                                    Book(id = "1", title = "Malgudi Days", author = "R.K. Narayan", category = "Story", summaryEnglish = "A collection of short stories set in the fictional town of Malgudi.", summaryKannada = "ಮಾಲ್ಗುಡಿಯ ಕಾಲ್ಪನಿಕ ಪಟ್ಟಣದಲ್ಲಿ ನಡೆಯುವ ಸಣ್ಣ ಕಥೆಗಳ ಸಂಗ್ರಹ.", pages = 250, totalCopies = 3, availableCopies = 3),
                                    Book(id = "2", title = "A Brief History of Time", author = "Stephen Hawking", category = "Science", summaryEnglish = "A landmark volume in science writing.", summaryKannada = "ವಿಜ್ಞಾನ ಬರವಣಿಗೆಯಲ್ಲಿ ಒಂದು ಮೈಲಿಗಲ್ಲು.", pages = 256, totalCopies = 2, availableCopies = 2),
                                    Book(id = "3", title = "The Discovery of India", author = "Jawaharlal Nehru", category = "History", summaryEnglish = "A broad view of Indian history.", summaryKannada = "ಭಾರತೀಯ ಇತಿಹಾಸದ ವಿಸ್ತಾರವಾದ ನೋಟ.", pages = 640, totalCopies = 1, availableCopies = 1),
                                    Book(id = "4", title = "Panchatantra", author = "Vishnu Sharma", category = "Story", summaryEnglish = "Ancient Indian collection of interrelated animal fables.", summaryKannada = "ಪರಸ್ಪರ ಸಂಬಂಧ ಹೊಂದಿರುವ ಪ್ರಾಣಿಗಳ ನೀತಿಕಥೆಗಳ ಪ್ರಾಚೀನ ಭಾರತೀಯ ಸಂಗ್ರಹ.", pages = 150, totalCopies = 5, availableCopies = 5),
                                    Book(id = "5", title = "Cosmos", author = "Carl Sagan", category = "Science", summaryEnglish = "A popular science book.", summaryKannada = "ಜನಪ್ರಿಯ ವಿಜ್ಞಾನ ಪುಸ್ತಕ.", pages = 365, totalCopies = 2, availableCopies = 2)
                                )
                                books.forEach { bookDao.insertBook(it) }


                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
