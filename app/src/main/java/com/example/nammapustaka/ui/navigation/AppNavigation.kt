package com.example.nammapustaka.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.nammapustaka.ui.screens.BookDetailScreen
import com.example.nammapustaka.ui.screens.CatalogScreen
import com.example.nammapustaka.ui.screens.LeaderboardScreen
import com.example.nammapustaka.ui.screens.QrScannerScreen
import com.example.nammapustaka.ui.screens.LoginScreen
import com.example.nammapustaka.ui.viewmodel.MainViewModel
import com.example.nammapustaka.ui.screens.SettingsScreen
import com.example.nammapustaka.ui.screens.TransactionHistoryScreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Catalog")
    object Scanner : Screen("scanner", "Scanner")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
    object Settings : Screen("settings", "Settings")
    object History : Screen("history", "History")
    object BookDetail : Screen("book_detail/{bookId}", "Book Detail") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(viewModel = viewModel)
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
            if (currentRoute != Screen.Scanner.route && !(currentRoute?.startsWith("book_detail") ?: false)) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Catalog") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
                    )
                    val currentUser by viewModel.currentUser.collectAsState()
                    val isTeacher = currentUser?.grade?.equals("Teacher", ignoreCase = true) == true || currentUser?.grade?.equals("Admin", ignoreCase = true) == true

                    if (isTeacher) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = null) },
                            label = { Text("Scanner") },
                            selected = currentRoute == Screen.Scanner.route,
                            onClick = { navController.navigate(Screen.Scanner.route) }
                        )
                    }
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text("Leaderboard") },
                        selected = currentRoute == Screen.Leaderboard.route,
                        onClick = { navController.navigate(Screen.Leaderboard.route) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        label = { Text("History") },
                        selected = currentRoute == Screen.History.route,
                        onClick = { navController.navigate(Screen.History.route) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("Settings") },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = { navController.navigate(Screen.Settings.route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                CatalogScreen(
                    viewModel = viewModel, 
                    onBookClick = { bookId -> navController.navigate(Screen.BookDetail.createRoute(bookId)) },
                    onAddBookClick = { navController.navigate("add_book") }
                )
            }
            composable(Screen.Scanner.route) {
                QrScannerScreen(onQrScanned = { qrValue ->
                    navController.navigate("issue_book/$qrValue")
                })
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel, 
                    onLogout = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                    onNavigateToManageStudents = { navController.navigate("manage_students") },
                    onNavigateToAddBook = { navController.navigate("add_book") }
                )
            }
            composable(Screen.History.route) {
                TransactionHistoryScreen(viewModel = viewModel)
            }
            composable("manage_students") {
                com.example.nammapustaka.ui.screens.ManageStudentsScreen(viewModel = viewModel)
            }
            composable("add_book") {
                com.example.nammapustaka.ui.screens.AddBookScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("issue_book/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                com.example.nammapustaka.ui.screens.IssueBookScreen(bookId = bookId, viewModel = viewModel, onBack = { navController.navigate(Screen.Home.route) { popUpTo(0) } })
            }
            composable(Screen.BookDetail.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                BookDetailScreen(bookId = bookId, viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
    }
}
