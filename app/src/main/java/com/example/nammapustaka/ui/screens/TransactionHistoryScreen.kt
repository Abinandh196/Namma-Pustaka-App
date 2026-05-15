package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allBooks by viewModel.books.collectAsState()
    val currentTime = System.currentTimeMillis()
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {
            Text("No transactions yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { txn ->
                    val studentName = allStudents.find { it.id == txn.studentId }?.name ?: "Unknown Student"
                    val bookTitle = allBooks.find { it.id == txn.bookId }?.title ?: "Unknown Book"

                    val isOverdue = txn.status == "ISSUED" && txn.dueDateMillis < currentTime
                    val cardColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (isOverdue) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = bookTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Borrowed by: $studentName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor
                            )
                            Text(
                                text = "Issued: ${dateFormatter.format(Date(txn.issueDateMillis))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                            Text(
                                text = "Due: ${dateFormatter.format(Date(txn.dueDateMillis))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOverdue) Color.Red else textColor,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Status: ${if (isOverdue) "OVERDUE" else txn.status}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
