package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AiChatMessage
import com.example.ui.components.NoorScreenHeader
import com.example.ui.viewmodel.AiStreamState

private val suggestedQuestions = listOf(
    "What should I recite before sleeping?",
    "What is the Sunnah method of eating?",
    "Meaning of Ayat-ul-Kursi?",
    "What is the reward of Ishraq prayer?"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAssistantScreen(
    messages: List<AiChatMessage>,
    streamState: AiStreamState,
    onBack: () -> Unit,
    onSendQuestion: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val busy = streamState is AiStreamState.Loading || streamState is AiStreamState.Streaming

    Column(modifier = Modifier.fillMaxSize()) {
        NoorScreenHeader(
            title = "Islamic AI Assistant",
            subtitle = "Grounded in verified Quran, Sunnah & Hadith",
            onBack = onBack
        )

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Educational guidance only—not a formal Fatwa. Consult qualified scholars for personal legal rulings.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(10.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestedQuestions.forEach { q ->
                        FilterChip(
                            selected = false,
                            onClick = { if (!busy) onSendQuestion(q) },
                            label = { Text(q) }
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val container = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                val contentColor = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                Card(
                    colors = CardDefaults.cardColors(containerColor = container),
                    border = if (msg.isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (msg.isUser) "You" else "Noor-e-Zindagi Assistant",
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.78f)
                        )
                        Text(msg.text, style = MaterialTheme.typography.bodyMedium, color = contentColor)
                        if (msg.citedSources.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "Verified Context Used:\n" + msg.citedSources.take(2).joinToString("\n"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (streamState is AiStreamState.Loading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Searching verified Quran & Sunnah sources...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (streamState is AiStreamState.Streaming) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = streamState.partialText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Ask an Islamic question...") },
                    modifier = Modifier.weight(1f).testTag("ai_question_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val q = input
                        input = ""
                        onSendQuestion(q)
                    },
                    enabled = input.isNotBlank() && !busy,
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("ai_send_btn")
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send question")
                }
            }
        }
    }
}
