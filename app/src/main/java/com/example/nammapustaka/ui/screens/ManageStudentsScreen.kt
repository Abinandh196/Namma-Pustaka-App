package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.ui.viewmodel.MainViewModel

@Composable
fun ManageStudentsScreen(viewModel: MainViewModel) {
    val students by viewModel.allStudents.collectAsState()
    
    // We should ideally fetch all students from the DB, but using leaderboard as a proxy for now
    var newStudentName by remember { mutableStateOf("") }
    var newStudentGrade by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Manage Students",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add New Student (Manual)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newStudentName,
                    onValueChange = { newStudentName = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newStudentGrade,
                    onValueChange = { newStudentGrade = it },
                    label = { Text("Class / Grade") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (newStudentName.isNotBlank() && newStudentGrade.isNotBlank()) {
                            viewModel.addStudentManually(newStudentName, newStudentGrade)
                            newStudentName = ""
                            newStudentGrade = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Student")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Registered Students", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(students) { student ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = student.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Grade: ${student.grade} | ID: ${student.id}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(text = "${student.totalPagesRead} pages", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
