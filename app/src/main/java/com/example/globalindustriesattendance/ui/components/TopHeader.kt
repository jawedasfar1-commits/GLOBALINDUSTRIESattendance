package com.example.globalindustriesattendance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalindustriesattendance.data.model.SiteInfo

@Composable
fun TopHeader(
    currentSite: SiteInfo,
    availableSites: List<SiteInfo>,
    isEditUnlocked: Boolean,
    onSiteSelected: (SiteInfo) -> Unit,
    onAddNewSiteRequested: () -> Unit,
    onToggleLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSiteMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // --- 1. Letterhead Container (White Background with Brown Accent Line) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Globe Logo
            GlobeLogo(modifier = Modifier.size(50.dp))

            Spacer(modifier = Modifier.width(14.dp))

            // Title Container ("Global Industries")
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Global",
                    color = Color(0xFF4CAF50),
                    fontSize = 36.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Industries",
                    color = Color(0xFF6DA35E),
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        // 3.5px Brown Accent Border Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp)
                .background(Color(0xFF8B5A2B))
        )

        // --- 2. App Control Bar (Dark Navy Gradient Header Bar) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1B2B))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Site Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "SITE:",
                    color = Color(0xFFFFD700), // Gold
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.95f))
                            .clickable { expandedSiteMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentSite.name,
                            color = Color(0xFF102A43),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Site",
                            tint = Color(0xFF102A43),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedSiteMenu,
                        onDismissRequest = { expandedSiteMenu = false }
                    ) {
                        availableSites.forEach { site ->
                            DropdownMenuItem(
                                text = { Text(site.name, fontSize = 13.sp) },
                                onClick = {
                                    onSiteSelected(site)
                                    expandedSiteMenu = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("+ Add New Site...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D68C4)) },
                            onClick = {
                                expandedSiteMenu = false
                                onAddNewSiteRequested()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Online Badge & Lock Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Online Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Online",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Auth Status Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isEditUnlocked) Color(0xFF10B981)
                            else Color.White.copy(alpha = 0.2f)
                        )
                        .clickable { onToggleLock() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isEditUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = "Lock Mode",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEditUnlocked) "Edit Mode" else "View Only",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GlobeLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(50.dp)) {
            val brownGrad = Color(0xFFA67B5B)
            // Outer Circle
            drawCircle(
                color = brownGrad,
                radius = size.width / 2,
                style = Stroke(width = 3.dp.toPx())
            )
            // Horizontal Latitude Lines
            drawLine(
                color = brownGrad,
                start = Offset(x = 4.dp.toPx(), y = size.height / 2),
                end = Offset(x = size.width - 4.dp.toPx(), y = size.height / 2),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = brownGrad,
                start = Offset(x = 8.dp.toPx(), y = size.height * 0.25f),
                end = Offset(x = size.width - 8.dp.toPx(), y = size.height * 0.25f),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = brownGrad,
                start = Offset(x = 8.dp.toPx(), y = size.height * 0.75f),
                end = Offset(x = size.width - 8.dp.toPx(), y = size.height * 0.75f),
                strokeWidth = 2.dp.toPx()
            )
        }
        // Center Banner "GI"
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF70441D)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GI",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
