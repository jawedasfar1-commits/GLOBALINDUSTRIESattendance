package com.example.globalindustriesattendance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun DashboardHeader(
    selectedDay: Int,
    totalWorkers: Int,
    todayPresentCount: Int,
    totalDutyMonth: Double,
    totalOtMonth: Double,
    totalNetBalance: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Grid Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                label = "WORKERS",
                value = "$totalWorkers",
                borderColor = Color(0xFF2D68C4),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "TODAY (DAY $selectedDay)",
                value = "$todayPresentCount / $totalWorkers",
                borderColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        // Grid Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                label = "DUTY DAYS",
                value = String.format(Locale.US, "%.1f", totalDutyMonth),
                borderColor = Color(0xFFFFB703),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "OVERTIME",
                value = String.format(Locale.US, "%.1f hrs", totalOtMonth),
                borderColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }

        // Grid Row 3 (Full Width Balance Card)
        StatCard(
            label = "NET PAYABLE BALANCE",
            value = String.format(Locale.US, "₹%,.2f", totalNetBalance),
            borderColor = Color(0xFF2D68C4),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row {
            // Left Accent Border Line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(68.dp)
                    .background(borderColor)
            )
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF102A43)
                )
            }
        }
    }
}
