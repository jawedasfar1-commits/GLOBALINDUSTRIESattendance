package com.example.globalindustriesattendance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalindustriesattendance.data.model.AttendanceRecord
import com.example.globalindustriesattendance.data.model.AttendanceUiState
import com.example.globalindustriesattendance.data.model.DprRecord
import com.example.globalindustriesattendance.data.model.SiteInfo
import com.example.globalindustriesattendance.data.model.Worker
import com.example.globalindustriesattendance.data.repository.FirebaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AttendanceViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val yearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)

    private val _currentSite = MutableStateFlow(SiteInfo("jaynagar", "Jaynagar Railway Station"))
    private val _selectedYearMonth = MutableStateFlow(yearMonthFormat.format(Date()))
    private val _selectedDay = MutableStateFlow(calendar.get(Calendar.DAY_OF_MONTH))
    private val _isEditUnlocked = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _workers = _currentSite.flatMapLatest { site ->
        repository.getWorkersFlow(site.id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _attendanceMap = combine(_currentSite, _selectedYearMonth) { site, ym ->
        Pair(site.id, ym)
    }.flatMapLatest { (siteId, ym) ->
        repository.getAttendanceFlow(siteId, ym)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _advancesMap = _currentSite.flatMapLatest { site ->
        repository.getAdvancesFlow(site.id)
    }

    private val _dateKey = combine(_selectedYearMonth, _selectedDay) { ym, day ->
        String.format(Locale.US, "%s-%02d", ym, day)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _dprRecord = combine(_currentSite, _dateKey) { site, dateKey ->
        Pair(site.id, dateKey)
    }.flatMapLatest { (siteId, dateKey) ->
        repository.getDprFlow(siteId, dateKey)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _notesText = combine(_currentSite, _dateKey) { site, dateKey ->
        Pair(site.id, dateKey)
    }.flatMapLatest { (siteId, dateKey) ->
        repository.getNotesFlow(siteId, dateKey)
    }

    val uiState: StateFlow<AttendanceUiState> = combine(
        combine(_isEditUnlocked, _currentSite, repository.getSitesFlow(), _selectedYearMonth) { isUnlocked, site, sites, ym ->
            Tuple4(isUnlocked, site, sites, ym)
        },
        combine(_selectedDay, _workers, _attendanceMap, _advancesMap) { day, workers, attMap, advMap ->
            Tuple4(day, workers, attMap, advMap)
        },
        combine(_dprRecord, _notesText) { dpr, notes ->
            Pair(dpr, notes)
        }
    ) { (isUnlocked, site, sites, ym), (day, workers, attMap, advMap), (dpr, notes) ->
        AttendanceUiState(
            isEditUnlocked = isUnlocked,
            currentSite = site,
            availableSites = sites,
            selectedYearMonth = ym,
            selectedDay = day,
            workers = workers,
            attendanceMap = attMap,
            advancesMap = advMap,
            dprRecord = dpr,
            notesText = notes,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AttendanceUiState()
    )

    private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    // Admin PIN Unlock
    fun unlockWithPin(pin: String): Boolean {
        return if (pin == "1820") {
            _isEditUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun lockEditMode() {
        _isEditUnlocked.value = false
    }

    // Navigation & Site Switches
    fun switchSite(site: SiteInfo) {
        _currentSite.value = site
    }

    fun addNewSite(siteName: String) {
        val newId = siteName.lowercase(Locale.US).replace(Regex("[^a-z0-9]"), "_") + "_" + System.currentTimeMillis()
        val siteInfo = SiteInfo(newId, siteName.trim())
        viewModelScope.launch {
            repository.addNewSite(siteInfo)
            _currentSite.value = siteInfo
        }
    }

    fun setSelectedYearMonth(yearMonth: String) {
        _selectedYearMonth.value = yearMonth
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    // --- WRITES ---

    fun saveWorker(worker: Worker, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveWorker(_currentSite.value.id, worker)
            onComplete()
        }
    }

    fun deleteWorker(workerId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteWorker(_currentSite.value.id, workerId)
            onComplete()
        }
    }

    fun updateAttendance(workerId: String, day: Int, duty: Double, ot: Double, dailyAdvance: Double) {
        viewModelScope.launch {
            val siteId = _currentSite.value.id
            val ym = _selectedYearMonth.value
            repository.saveAttendanceRecord(siteId, ym, workerId, day, duty, ot, dailyAdvance)
        }
    }

    fun bulkMarkDaily(duty: Double) {
        viewModelScope.launch {
            val state = uiState.value
            repository.bulkMarkAttendance(
                siteId = state.currentSite.id,
                yearMonth = state.selectedYearMonth,
                day = state.selectedDay,
                workers = state.workers,
                duty = duty
            )
        }
    }

    fun updateAdvance(workerId: String, amount: Double) {
        viewModelScope.launch {
            repository.saveAdvance(_currentSite.value.id, workerId, amount)
        }
    }

    fun saveDpr(dprRecord: DprRecord) {
        viewModelScope.launch {
            val dateKey = String.format(Locale.US, "%s-%02d", _selectedYearMonth.value, _selectedDay.value)
            repository.saveDpr(_currentSite.value.id, dateKey, dprRecord)
        }
    }

    fun saveNotes(notesText: String) {
        viewModelScope.launch {
            val dateKey = String.format(Locale.US, "%s-%02d", _selectedYearMonth.value, _selectedDay.value)
            repository.saveNotes(_currentSite.value.id, dateKey, notesText)
        }
    }
}
