package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.ui.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueBookScreen(bookId: String, viewModel: MainViewModel, onBack: () -> Unit) {
    val allStudents by viewModel.allStudents.collectAsState()
    var selectedStudentId by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredStudents = allStudents.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.id.toString() == searchQuery
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Issue Book: $bookId",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Student Name or ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredStudents) { student ->
                val isSelected = student.id == selectedStudentId
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                    onClick = { selectedStudentId = student.id }
                ) {
                    Text(
                        text = "${student.name} (Grade: ${student.grade})",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedStudentId?.let { studentId ->
                    val issueDate = System.currentTimeMillis()
                    val dueDate = issueDate + TimeUnit.DAYS.toMillis(7) // 7 days borrow limit
                    viewModel.issueBookManually(bookId, studentId, issueDate, dueDate)
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedStudentId != null
        ) {
            Text("Confirm Issue")
        }
    }
}
