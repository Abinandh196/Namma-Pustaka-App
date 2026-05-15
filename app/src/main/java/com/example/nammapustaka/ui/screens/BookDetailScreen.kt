package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.data.entity.Book
import com.example.nammapustaka.ui.viewmodel.MainViewModel

@Composable
fun BookDetailScreen(bookId: String, viewModel: MainViewModel, onBack: () -> Unit) {
    val bookFlow = remember(bookId) { viewModel.getBookById(bookId) }
    val book by bookFlow.collectAsState(initial = null)
    
    var aiSummary by remember { mutableStateOf("Generating Kannada summary...") }
    
    LaunchedEffect(book) {
        book?.let {
            if (it.summaryKannada.isNotBlank()) {
                aiSummary = it.summaryKannada
            } else {
                aiSummary = viewModel.generateSummaryFor(it.title, it.author)
            }
        }
    }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = book!!.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Author: ${book!!.author}", style = MaterialTheme.typography.titleMedium)
        Text(text = "Category: ${book!!.category}", style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "✨ Smart Summary (Kannada)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (aiSummary.startsWith("Generating") || aiSummary.startsWith("Error")) {
                    Text(text = aiSummary, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(text = aiSummary, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Availability", style = MaterialTheme.typography.titleLarge)
        Text(text = "${book!!.availableCopies} / ${book!!.totalCopies} Available", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { /* Implement Issue */ }, modifier = Modifier.weight(1f)) {
                Text("Issue (Scan QR)")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { 
                    viewModel.borrowBook(bookId)
                    onBack()
                }, 
                modifier = Modifier.weight(1f), 
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Borrow Manually")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Reviews", style = MaterialTheme.typography.titleLarge)
        Text(text = "No reviews yet. Be the first to review!", style = MaterialTheme.typography.bodyMedium)
    }
}
