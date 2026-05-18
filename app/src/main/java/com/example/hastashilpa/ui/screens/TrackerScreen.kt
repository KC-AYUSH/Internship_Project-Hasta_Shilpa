package com.example.hastashilpa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hastashilpa.data.model.MaterialLog
import com.example.hastashilpa.ui.theme.*
import com.example.hastashilpa.viewmodel.TrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    innerPadding: PaddingValues,
    trackerViewModel: TrackerViewModel = viewModel()
) {
    val logs by trackerViewModel.logs.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Material Tracker", fontWeight = FontWeight.Bold,
                            fontSize = 20.sp, color = DarkBrown)
                        Text("Log bamboo & cane usage per batch",
                            fontSize = 12.sp, color = BambooGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = BambooGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add batch") },
                text = { Text("Add Batch") },
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
            )
        },
        containerColor = CreamWhite
    ) { scaffoldPadding ->

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("No batches logged yet", fontSize = 16.sp,
                        color = DarkBrown.copy(alpha = 0.5f))
                    Text("Tap 'Add Batch' to log your first batch",
                        fontSize = 13.sp, color = BambooGreen)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = scaffoldPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Summary card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSage),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SummaryItem("Batches", logs.size.toString())
                            SummaryItem("Total Poles",
                                logs.sumOf { it.bambooPoles }.toString())
                            SummaryItem("Total Hours",
                                String.format("%.1f", logs.sumOf { it.hoursWorked.toDouble() }))
                        }
                    }
                }
                items(logs, key = { it.docId }) { log ->
                    LogCard(log) { trackerViewModel.deleteLog(log.docId) }
                }
            }
        }
    }

    if (showDialog) {
        AddLogDialog(
            onDismiss = { showDialog = false },
            onConfirm = { batch, poles, hours, notes ->
                trackerViewModel.addLog(batch, poles, hours, notes)
                showDialog = false
            }
        )
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = BambooGreen)
        Text(label, fontSize = 11.sp, color = DarkBrown.copy(alpha = 0.6f))
    }
}

@Composable
fun LogCard(log: MaterialLog, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(log.batchName, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = DarkBrown)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatChip("${log.bambooPoles} poles")
                    StatChip("${log.hoursWorked}h")
                }
                if (log.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(log.notes, fontSize = 12.sp,
                        color = DarkBrown.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(log.dateAdded, fontSize = 11.sp,
                    color = DarkBrown.copy(alpha = 0.4f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete",
                    tint = WarmBrown)
            }
        }
    }
}

@Composable
fun StatChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = LightSage
    ) {
        Text(text, fontSize = 11.sp, color = BambooGreen,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Float, String) -> Unit
) {
    var batchName by remember { mutableStateOf("") }
    var poles by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CreamWhite,
        title = { Text("Add Batch Log", fontWeight = FontWeight.Bold, color = DarkBrown) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = batchName, onValueChange = { batchName = it; error = "" },
                    label = { Text("Batch name e.g. Laptop Stand #1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = error.isNotEmpty() && batchName.isBlank()
                )
                OutlinedTextField(
                    value = poles, onValueChange = { poles = it; error = "" },
                    label = { Text("Bamboo poles used") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = hours, onValueChange = { hours = it; error = "" },
                    label = { Text("Hours worked") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (batchName.isBlank()) {
                        error = "Batch name is required."
                    } else {
                        val p = poles.toIntOrNull() ?: 0
                        val h = hours.toFloatOrNull() ?: 0f
                        onConfirm(batchName, p, h, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen)
            ) { Text("Save Batch") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = WarmBrown) }
        }
    )
}