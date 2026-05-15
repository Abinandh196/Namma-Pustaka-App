package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    onNavigateToManageStudents: () -> Unit,
    onNavigateToAddBook: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = currentUser?.name ?: "Unknown User",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grade: ${currentUser?.grade ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.setDarkMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // My Borrow History
        Text(
            text = "My Borrow History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        val transactions by viewModel.transactions.collectAsState()
        val allBooks by viewModel.books.collectAsState()
        val myTransactions = transactions.filter { it.studentId == currentUser?.id }
        val currentTime = System.currentTimeMillis()
        val dateFormatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())

        if (myTransactions.isEmpty()) {
            Text("You haven't borrowed any books yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(myTransactions) { txn ->
                    val bookTitle = allBooks.find { it.id == txn.bookId }?.title ?: "Unknown Book"
                    val isOverdue = txn.status == "ISSUED" && txn.dueDateMillis < currentTime
                    val cardColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = bookTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverdue) androidx.compose.ui.graphics.Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Issued: ${dateFormatter.format(java.util.Date(txn.issueDateMillis))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                            Text(
                                text = "Return by: ${dateFormatter.format(java.util.Date(txn.dueDateMillis))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOverdue) androidx.compose.ui.graphics.Color.Red else textColor,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isOverdue) {
                                Text(
                                    text = "OVERDUE",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (currentUser?.grade?.equals("Teacher", ignoreCase = true) == true || currentUser?.grade?.equals("Admin", ignoreCase = true) == true) {
            Button(
                onClick = onNavigateToAddBook,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            ) {
                Text("Add Book Manually")
            }
            Button(
                onClick = onNavigateToManageStudents,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Manage Students")
            }
        }

        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Log Out")
        }
    }
}
