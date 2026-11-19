package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.remote.OfflineAstronomyHelper
import com.example.ui.components.NoorScreenHeader
import com.example.ui.viewmodel.NoorUiSnapshot
import java.text.DateFormatSymbols

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IslamicCalendarScreen(
    uiState: NoorUiSnapshot,
    onBack: () -> Unit,
    onLoadMonth: (Int, Int) -> Unit,
    onToggleFasting: () -> Unit
) {
    val monthTitle = "${DateFormatSymbols().months[(uiState.calendarMonth - 1).coerceIn(0, 11)]} ${uiState.calendarYear}"
    val firstCell = uiState.calendarCells.firstOrNull()

    fun shiftMonth(delta: Int) {
        var m = uiState.calendarMonth + delta
        var y = uiState.calendarYear
        if (m < 1) { m = 12; y -= 1 }
        if (m > 12) { m = 1; y += 1 }
        onLoadMonth(y, m)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Islamic Hijri Calendar",
            subtitle = firstCell?.let { "${it.hijriMonthEn} ${it.hijriYear} AH" } ?: "Hijri-Gregorian Grid",
            onBack = onBack
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Month Navigator + Optional Fasting Tracker
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { shiftMonth(-1) },
                                modifier = Modifier.minimumInteractiveComponentSize().testTag("cal_prev_month")
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Previous month", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(monthTitle, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                                if (firstCell != null) {
                                    Text("${firstCell.hijriMonthEn} ${firstCell.hijriYear} AH", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondaryContainer)
                                }
                            }
                            IconButton(
                                onClick = { shiftMonth(1) },
                                modifier = Modifier.minimumInteractiveComponentSize().testTag("cal_next_month")
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Next month", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }

                        Surface(
                            onClick = onToggleFasting,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth().testTag("fasting_toggle_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Today's Optional Fasting Tracker", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                                    Text("Mon/Thu, Ayyam al-Beed (13/14/15), or Ramadan (${uiState.habitStats.fastingDaysThisMonth} fasts this month)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondaryContainer)
                                }
                                Icon(
                                    imageVector = if (uiState.fastingCompletedToday) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = "Toggle fasting completed today",
                                    tint = MaterialTheme.colorScheme.secondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Moon-Sighting Disclaimer
            item {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hijri dates follow astronomical calculation and may vary by +/- 1 day depending on local crescent moon-sighting announcements.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Monthly Grid
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Daily Hijri / Gregorian Mapping (Gold = Ayyam al-Beed 13/14/15)", style = MaterialTheme.typography.labelLarge)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            maxItemsInEachRow = 7
                        ) {
                            uiState.calendarCells.forEach { cell ->
                                val bg = when {
                                    cell.isToday -> MaterialTheme.colorScheme.primary
                                    cell.occasion != null -> MaterialTheme.colorScheme.tertiaryContainer
                                    cell.isWhiteDay -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val fg = if (cell.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                Surface(
                                    color = bg,
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.width(44.dp)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = cell.gregorianDay.toString(),
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                color = fg,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "${cell.hijriDay}H",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = fg.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Islamic Occasions
            item {
                Text("Key Islamic Occasions & Sunnah Fasting Days", style = MaterialTheme.typography.titleLarge)
            }
            items(OfflineAstronomyHelper.islamicOccasions) { occ ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(occ.titleEn, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            if (occ.fastingRecommended) {
                                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                                    Text(
                                        "Sunnah Fast",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Text(occ.titleUr, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(occ.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
