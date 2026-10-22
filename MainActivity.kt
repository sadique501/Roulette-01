package com.example.rouletteinsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.rouletteinsight.analysis.SpinAnalysis
import com.example.rouletteinsight.data.AppDatabase
import com.example.rouletteinsight.data.SpinEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.get(this).spinDao()
        val sessionId = UUID.randomUUID().toString()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                RouletteHome(
                    dao = dao,
                    sessionId = sessionId,
                    onAdd = { n -> lifecycleScope.launch { dao.insert(SpinEntity(number = n, sessionId = sessionId)) } },
                    onDelete = { id -> lifecycleScope.launch { dao.deleteById(id) } },
                    onClear = { lifecycleScope.launch { dao.deleteAll() } }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RouletteHome(
    dao: com.example.rouletteinsight.data.SpinDao,
    sessionId: String,
    onAdd: (Int) -> Unit,
    onDelete: (Long) -> Unit,
    onClear: () -> Unit
) {
    val allNewest by dao.observeNewestFirst().collectAsStateWithLifecycle(initialValue = emptyList())
    val allOldest = remember(allNewest) { allNewest.reversed() }
    var input by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) }
    val recent = allOldest.takeLast(10).reversed()
    val stats = remember(allOldest) { SpinAnalysis.frequency(allOldest.map { it.number }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roulette Insight") },
                actions = {
                    IconButton(onClick = onClear) { Icon(Icons.Default.Delete, contentDescription = "Clear history") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Track results. Understand the statistics.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Track") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Analysis") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("AI Chat") })
            }
            Spacer(Modifier.height(12.dp))
            when (tab) {
                0 -> {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Add roulette result (0–36)", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = input,
                                    onValueChange = { input = it.filter(Char::isDigit).take(2) },
                                    label = { Text("Number") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = {
                                    val n = input.toIntOrNull()
                                    if (n != null && n in 0..36) { onAdd(n); input = "" }
                                }, enabled = (input.toIntOrNull() ?: -1) in 0..36) { Text("Save") }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text("Results are saved locally in arrival order.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Recent sequence · newest first", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        recent.forEach { spin ->
                            AssistChip(onClick = { onDelete(spin.id) }, label = { Text("${spin.number} ×") })
                        }
                        if (recent.isEmpty()) Text("No results yet.")
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Full history", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(allNewest, key = { it.id }) { spin ->
                            ListItem(
                                headlineContent = { Text("Number ${spin.number}") },
                                supportingContent = {
                                    Text(SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault()).format(Date(spin.timestamp)))
                                },
                                trailingContent = {
                                    IconButton(onClick = { onDelete(spin.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete result")
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
                1 -> {
                    Text("Results stored: ${allOldest.size}", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Top 5 by historical frequency", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    val top = SpinAnalysis.estimate(allOldest.map { it.number })
                    if (top.isEmpty()) Text("Add results to see statistics.")
                    top.forEach { stat ->
                        ListItem(
                            headlineContent = { Text("Number ${stat.number}") },
                            supportingContent = { Text("Seen ${stat.count} time(s) · ${"%.1f".format(stat.percent)}% of recorded results") }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Important: past frequency is descriptive, not a reliable prediction. In fair roulette, each spin is independent.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {
                    Text("AI Assistant", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Planned module: Hinglish/Hindi/English chat, context from saved statistics, and optional voice input/output.")
                    Spacer(Modifier.height(8.dp))
                    Text("To activate real AI chat, connect a secure backend/API. Do not put a private API key directly inside the Android app.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { }) { Text("AI connection not configured yet") }
                }
            }
        }
    }
}
