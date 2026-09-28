package com.example.globalindustriesattendance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.globalindustriesattendance.ui.AttendanceViewModel
import com.example.globalindustriesattendance.ui.components.DashboardHeader
import com.example.globalindustriesattendance.ui.components.PinDialog
import com.example.globalindustriesattendance.ui.screens.DailyViewScreen
import com.example.globalindustriesattendance.ui.screens.DprScreen
import com.example.globalindustriesattendance.ui.screens.MonthlyViewScreen
import com.example.globalindustriesattendance.ui.screens.NotesScreen
import com.example.globalindustriesattendance.ui.screens.WorkersScreen

class MainActivity : ComponentActivity() {

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Daily : BottomNavScreen("daily", "Daily View", Icons.Default.Today)
    object DPR : BottomNavScreen("dpr", "DPR", Icons.AutoMirrored.Filled.Assignment)
    object Notes : BottomNavScreen("notes", "Notes", Icons.AutoMirrored.Filled.Notes)
    object Monthly : BottomNavScreen("monthly", "Monthly", Icons.Default.CalendarMonth)
    object Workers : BottomNavScreen("workers", "Workers", Icons.Default.People)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: AttendanceViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var showAddSiteDialog by remember { mutableStateOf(false) }
    var expandedSiteMenu by remember { mutableStateOf(false) }

    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavScreen.Daily,
        BottomNavScreen.DPR,
        BottomNavScreen.Notes,
        BottomNavScreen.Monthly,
        BottomNavScreen.Workers
    )

    // Site Stats Calculation
    val stats = remember(uiState.workers, uiState.attendanceMap, uiState.advancesMap, uiState.selectedDay) {
        val totalWorkers = uiState.workers.size
        val todayPresentCount = uiState.workers.count { worker ->
            val record = uiState.attendanceMap[worker.id]?.get(uiState.selectedDay)
            (record?.duty ?: 0.0) > 0.0
        }

        var totalDutyMonth = 0.0
        var totalOtMonth = 0.0
        var totalNetBalance = 0.0

        uiState.workers.forEach { worker ->
            val workerAttendance = uiState.attendanceMap[worker.id] ?: emptyMap()
            var wDuty = 0.0
            var wOt = 0.0
            var wDailyAdv = 0.0
            for (day in 1..31) {
                val rec = workerAttendance[day]
                if (rec != null) {
                    wDuty += rec.duty
                    wOt += rec.ot
                    wDailyAdv += rec.dailyAdvance
                }
            }
            totalDutyMonth += wDuty
            totalOtMonth += wOt

            val dutyEarned = wDuty * worker.dailyRate
            val otEarned = wOt * (worker.dailyRate / 8.0)
            val baseAdv = uiState.advancesMap[worker.id] ?: worker.baseAdvance
            val totAdv = baseAdv + wDailyAdv
            totalNetBalance += (dutyEarned + otEarned - totAdv)
        }

        SiteStats(
            totalWorkers = totalWorkers,
            todayPresentCount = todayPresentCount,
            totalDutyMonth = totalDutyMonth,
            totalOtMonth = totalOtMonth,
            totalNetBalance = totalNetBalance
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "GLOBAL INDUSTRIES",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // Site Selector Dropdown
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { expandedSiteMenu = true }
                            ) {
                                Text(
                                    text = "SITE: ${uiState.currentSite.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Site",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = expandedSiteMenu,
                                onDismissRequest = { expandedSiteMenu = false }
                            ) {
                                uiState.availableSites.forEach { site ->
                                    DropdownMenuItem(
                                        text = { Text(site.name) },
                                        onClick = {
                                            viewModel.switchSite(site)
                                            expandedSiteMenu = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("+ Add New Site...") },
                                    onClick = {
                                        expandedSiteMenu = false
                                        if (!uiState.isEditUnlocked) {
                                            showPinDialog = true
                                        } else {
                                            showAddSiteDialog = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (uiState.isEditUnlocked) {
                                viewModel.lockEditMode()
                                Toast.makeText(context, "Locked to View-Only Mode", Toast.LENGTH_SHORT).show()
                            } else {
                                showPinDialog = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.isEditUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Toggle Lock Mode",
                            tint = if (uiState.isEditUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
        ) {
            // Dashboard Summary Header
            DashboardHeader(
                selectedDay = uiState.selectedDay,
                totalWorkers = stats.totalWorkers,
                todayPresentCount = stats.todayPresentCount,
                totalDutyMonth = stats.totalDutyMonth,
                totalOtMonth = stats.totalOtMonth,
                totalNetBalance = stats.totalNetBalance
            )

            // Screen Navigation Host
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavScreen.Daily.route
                ) {
                    composable(BottomNavScreen.Daily.route) {
                        DailyViewScreen(
                            uiState = uiState,
                            onDaySelected = { day -> viewModel.setSelectedDay(day) },
                            onAttendanceUpdated = { workerId, day, duty, ot, dailyAdv ->
                                viewModel.updateAttendance(workerId, day, duty, ot, dailyAdv)
                            },
                            onBulkMark = { duty -> viewModel.bulkMarkDaily(duty) },
                            onRequestUnlock = { showPinDialog = true }
                        )
                    }

                    composable(BottomNavScreen.DPR.route) {
                        DprScreen(
                            uiState = uiState,
                            onSaveDpr = { dpr -> viewModel.saveDpr(dpr) },
                            onRequestUnlock = { showPinDialog = true }
                        )
                    }

                    composable(BottomNavScreen.Notes.route) {
                        NotesScreen(
                            uiState = uiState,
                            onSaveNotes = { notes -> viewModel.saveNotes(notes) },
                            onRequestUnlock = { showPinDialog = true }
                        )
                    }

                    composable(BottomNavScreen.Monthly.route) {
                        MonthlyViewScreen(
                            uiState = uiState,
                            onMonthSelected = { monthStr -> viewModel.setSelectedYearMonth(monthStr) },
                            onAdvanceUpdated = { workerId, amount ->
                                viewModel.updateAdvance(workerId, amount)
                            },
                            onRequestUnlock = { showPinDialog = true }
                        )
                    }

                    composable(BottomNavScreen.Workers.route) {
                        WorkersScreen(
                            uiState = uiState,
                            onSaveWorker = { worker -> viewModel.saveWorker(worker) },
                            onDeleteWorker = { workerId -> viewModel.deleteWorker(workerId) },
                            onRequestUnlock = { showPinDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Admin PIN Dialog
    if (showPinDialog) {
        PinDialog(
            onDismiss = { showPinDialog = false },
            onConfirmPin = { pin ->
                val success = viewModel.unlockWithPin(pin)
                if (success) {
                    showPinDialog = false
                    Toast.makeText(context, "Admin Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                }
                success
            }
        )
    }

    // Add New Site Dialog
    if (showAddSiteDialog) {
        var newSiteName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddSiteDialog = false },
            title = { Text("Add New Site / Station") },
            text = {
                OutlinedTextField(
                    value = newSiteName,
                    onValueChange = { newSiteName = it },
                    label = { Text("Site / Station Name") },
                    placeholder = { Text("E.g. Patna Junction") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSiteName.isNotBlank()) {
                            viewModel.addNewSite(newSiteName)
                            showAddSiteDialog = false
                        }
                    }
                ) {
                    Text("Add Site")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSiteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private data class SiteStats(
    val totalWorkers: Int,
    val todayPresentCount: Int,
    val totalDutyMonth: Double,
    val totalOtMonth: Double,
    val totalNetBalance: Double
)
