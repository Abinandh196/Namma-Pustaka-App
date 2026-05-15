package com.example.nammapustaka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammapustaka.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AddBookScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var bookId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Story") }
    var summaryEn by remember { mutableStateOf("") }
    var summaryKn by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (isScanning) {
        QrScannerScreen(onQrScanned = { scannedId ->
            bookId = scannedId
            isScanning = false
        })
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Add Book to Library",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = bookId,
                onValueChange = { bookId = it },
                label = { Text("Barcode / ISBN") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { isScanning = true }) {
                Text("Scan")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = summaryEn,
            onValueChange = { summaryEn = it },
            label = { Text("English Summary (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = summaryKn,
            onValueChange = { summaryKn = it },
            label = { Text("Kannada Summary (Auto-generated if empty)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isGenerating = true
                    var finalKn = summaryKn
                    if (summaryKn.isBlank() && title.isNotBlank()) {
                        try {
                            finalKn = viewModel.generateSummaryFor(title, author)
                        } catch (e: Exception) {
                            finalKn = "Summary could not be generated."
                        }
                    }
                    viewModel.addBookManually(bookId, title, author, category, summaryEn, finalKn)
                    isGenerating = false
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = bookId.isNotBlank() && title.isNotBlank() && !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Book")
            }
        }
    }
}
