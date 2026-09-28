package com.example.globalindustriesattendance.data.repository

import com.example.globalindustriesattendance.data.model.AttendanceRecord
import com.example.globalindustriesattendance.data.model.DprRecord
import com.example.globalindustriesattendance.data.model.SiteInfo
import com.example.globalindustriesattendance.data.model.Worker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val DATABASE_URL = "https://g12343-default-rtdb.asia-southeast1.firebasedatabase.app/"

class FirebaseRepository(
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
) {
    init {
        try {
            db.setPersistenceEnabled(true)
        } catch (_: Exception) {
            // Already enabled
        }
    }

    private fun getSiteRef(siteId: String) = db.getReference("sites").child(siteId)

    /**
     * Real-time stream of all construction sites from node "global/sites"
     */
    fun getSitesFlow(): Flow<List<SiteInfo>> = callbackFlow {
        val sitesRef = db.getReference("global/sites")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SiteInfo>()
                for (child in snapshot.children) {
                    val site = child.getValue(SiteInfo::class.java)
                    if (site != null) list.add(site)
                }
                if (list.isEmpty()) {
                    list.add(SiteInfo("jaynagar", "Jaynagar Railway Station"))
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        sitesRef.addValueEventListener(listener)
        awaitClose { sitesRef.removeEventListener(listener) }
    }

    suspend fun addNewSite(siteInfo: SiteInfo) {
        val sitesRef = db.getReference("global/sites")
        sitesRef.child(siteInfo.id).setValue(siteInfo).await()
    }

    /**
     * Real-time stream of workers for siteId from "sites/{siteId}/workers"
     */
    fun getWorkersFlow(siteId: String): Flow<List<Worker>> = callbackFlow {
        val workersRef = getSiteRef(siteId).child("workers")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workers = mutableListOf<Worker>()
                for (child in snapshot.children) {
                    val worker = child.getValue(Worker::class.java)
                    if (worker != null) {
                        val finalWorker = if (worker.id.isEmpty()) worker.copy(id = child.key ?: "") else worker
                        workers.add(finalWorker)
                    }
                }
                trySend(workers)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        workersRef.addValueEventListener(listener)
        awaitClose { workersRef.removeEventListener(listener) }
    }

    /**
     * Real-time stream of attendance records for siteId from "sites/{siteId}/attendance"
     */
    fun getAttendanceFlow(siteId: String, yearMonth: String): Flow<Map<String, Map<Int, AttendanceRecord>>> = callbackFlow {
        val attendanceRef = getSiteRef(siteId).child("attendance")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultMap = mutableMapOf<String, MutableMap<Int, AttendanceRecord>>()

                for (workerSnapshot in snapshot.children) {
                    val workerId = workerSnapshot.key ?: continue
                    val dayMap = mutableMapOf<Int, AttendanceRecord>()

                    for (daySnapshot in workerSnapshot.children) {
                        val dayInt = daySnapshot.key?.toIntOrNull() ?: continue
                        val duty = daySnapshot.child("duty").getValue(Double::class.java)
                            ?: daySnapshot.child("duty").getValue(Long::class.java)?.toDouble() ?: 0.0
                        val ot = daySnapshot.child("ot").getValue(Double::class.java)
                            ?: daySnapshot.child("ot").getValue(Long::class.java)?.toDouble() ?: 0.0
                        val adv = daySnapshot.child("adv").getValue(Double::class.java)
                            ?: daySnapshot.child("adv").getValue(Long::class.java)?.toDouble()
                            ?: daySnapshot.child("dailyAdvance").getValue(Double::class.java) ?: 0.0

                        dayMap[dayInt] = AttendanceRecord(duty = duty, ot = ot, adv = adv)
                    }
                    resultMap[workerId] = dayMap
                }
                trySend(resultMap)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        attendanceRef.addValueEventListener(listener)
        awaitClose { attendanceRef.removeEventListener(listener) }
    }

    /**
     * Real-time stream of base advances from "sites/{siteId}/advances"
     */
    fun getAdvancesFlow(siteId: String): Flow<Map<String, Double>> = callbackFlow {
        val advancesRef = getSiteRef(siteId).child("advances")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, Double>()
                for (child in snapshot.children) {
                    val workerId = child.key ?: continue
                    val amount = child.getValue(Double::class.java)
                        ?: child.getValue(Long::class.java)?.toDouble() ?: 0.0
                    map[workerId] = amount
                }
                trySend(map)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        advancesRef.addValueEventListener(listener)
        awaitClose { advancesRef.removeEventListener(listener) }
    }

    /**
     * Real-time stream for DPR report from "sites/{siteId}/dpr/{dateKey}"
     */
    fun getDprFlow(siteId: String, dateKey: String): Flow<DprRecord> = callbackFlow {
        val dprRef = getSiteRef(siteId).child("dpr").child(dateKey)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dpr = snapshot.getValue(DprRecord::class.java) ?: DprRecord()
                trySend(dpr)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        dprRef.addValueEventListener(listener)
        awaitClose { dprRef.removeEventListener(listener) }
    }

    /**
     * Real-time stream for Daily Notes from "sites/{siteId}/notes/{dateKey}"
     */
    fun getNotesFlow(siteId: String, dateKey: String): Flow<String> = callbackFlow {
        val notesRef = getSiteRef(siteId).child("notes").child(dateKey)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = snapshot.getValue(String::class.java) ?: ""
                trySend(notes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        notesRef.addValueEventListener(listener)
        awaitClose { notesRef.removeEventListener(listener) }
    }

    // --- WRITES ---

    suspend fun saveWorker(siteId: String, worker: Worker) {
        val workersRef = getSiteRef(siteId).child("workers")
        val key = worker.id.ifEmpty { workersRef.push().key ?: System.currentTimeMillis().toString() }
        val updatedWorker = worker.copy(id = key)
        workersRef.child(key).setValue(updatedWorker).await()
        if (worker.baseAdvance > 0) {
            saveAdvance(siteId, key, worker.baseAdvance)
        }
    }

    suspend fun deleteWorker(siteId: String, workerId: String) {
        getSiteRef(siteId).child("workers").child(workerId).removeValue().await()
        getSiteRef(siteId).child("advances").child(workerId).removeValue().await()
    }

    suspend fun saveAttendanceRecord(siteId: String, yearMonth: String, workerId: String, day: Int, duty: Double, ot: Double, adv: Double) {
        val recordRef = getSiteRef(siteId).child("attendance").child(workerId).child(day.toString())
        val map = mapOf("duty" to duty, "ot" to ot, "adv" to adv)
        recordRef.setValue(map).await()
    }

    suspend fun saveAdvance(siteId: String, workerId: String, amount: Double) {
        getSiteRef(siteId).child("advances").child(workerId).setValue(amount).await()
    }

    suspend fun saveDpr(siteId: String, dateKey: String, dprRecord: DprRecord) {
        getSiteRef(siteId).child("dpr").child(dateKey).setValue(dprRecord).await()
    }

    suspend fun saveNotes(siteId: String, dateKey: String, notesText: String) {
        getSiteRef(siteId).child("notes").child(dateKey).setValue(notesText).await()
    }

    suspend fun bulkMarkAttendance(siteId: String, yearMonth: String, day: Int, workers: List<Worker>, duty: Double) {
        val attRef = getSiteRef(siteId).child("attendance")
        val updates = mutableMapOf<String, Any>()
        for (w in workers) {
            updates["${w.id}/$day/duty"] = duty
        }
        attRef.updateChildren(updates).await()
    }
}
