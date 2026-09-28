package com.example.globalindustriesattendance.data.model

data class SiteInfo(
    val id: String = "jaynagar",
    val name: String = "Jaynagar Railway Station"
)

data class AttendanceUiState(
    val isEditUnlocked: Boolean = false,
    val currentSite: SiteInfo = SiteInfo(),
    val availableSites: List<SiteInfo> = listOf(SiteInfo()),
    val selectedYearMonth: String = "", // e.g. "2025-02"
    val selectedDay: Int = 1,          // 1..31
    val workers: List<Worker> = emptyList(),
    // Map<WorkerId, Map<Day (1..31), AttendanceRecord>>
    val attendanceMap: Map<String, Map<Int, AttendanceRecord>> = emptyMap(),
    // Map<WorkerId, BaseAdvanceAmount>
    val advancesMap: Map<String, Double> = emptyMap(),
    val dprRecord: DprRecord = DprRecord(),
    val notesText: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
