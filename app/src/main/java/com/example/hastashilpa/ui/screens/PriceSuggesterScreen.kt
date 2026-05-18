package com.example.hastashilpa.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hastashilpa.ui.theme.*
import kotlin.math.roundToInt

data class PriceResult(
    val materialCost: Double,
    val labourCost: Double,
    val totalCost: Double,
    val profit: Double,
    val suggestedPrice: Double,
    val margin: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceSuggesterScreen(innerPadding: PaddingValues) {
    var materialCost by remember { mutableStateOf("") }
    var labourHours by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("50") }
    var profitMargin by remember { mutableStateOf("30") }
    var result by remember { mutableStateOf<PriceResult?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Price Suggester", fontWeight = FontWeight.Bold,
                            fontSize = 20.sp, color = DarkBrown)
                        Text("Calculate your fair selling price",
                            fontSize = 12.sp, color = BambooGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = scaffoldPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Input card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Input Details", fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = DarkBrown)

                    OutlinedTextField(
                        value = materialCost,
                        onValueChange = { materialCost = it; errorMessage = "" },
                        label = { Text("Material cost (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = errorMessage.isNotEmpty() && materialCost.isBlank(),
                        supportingText = { Text("Cost of bamboo/cane + other materials") }
                    )
                    OutlinedTextField(
                        value = labourHours,
                        onValueChange = { labourHours = it; errorMessage = "" },
                        label = { Text("Labour hours") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = errorMessage.isNotEmpty() && labourHours.isBlank(),
                        supportingText = { Text("Total hours spent making the product") }
                    )
                    OutlinedTextField(
                        value = hourlyRate,
                        onValueChange = { hourlyRate = it },
                        label = { Text("Your hourly rate (₹/hr)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        supportingText = { Text("How much your time is worth per hour") }
                    )
                    OutlinedTextField(
                        value = profitMargin,
                        onValueChange = { profitMargin = it },
                        label = { Text("Profit margin (%)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        supportingText = { Text("Recommended: 25–40% for handmade crafts") }
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp)
                    }
                }
            }

            Button(
                onClick = {
                    val mc = materialCost.toDoubleOrNull()
                    val lh = labourHours.toDoubleOrNull()
                    if (mc == null || lh == null) {
                        errorMessage = "Please enter valid numbers for material cost and labour hours."
                        result = null
                    } else {
                        val hr = hourlyRate.toDoubleOrNull() ?: 50.0
                        val pm = profitMargin.toDoubleOrNull() ?: 30.0
                        val labourCost = lh * hr
                        val totalCost = mc + labourCost
                        val profit = totalCost * (pm / 100)
                        val suggestedPrice = totalCost + profit
                        result = PriceResult(mc, labourCost, totalCost, profit, suggestedPrice, pm)
                        errorMessage = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Calculate Suggested Price", fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp))
            }

            // Animated result card
            AnimatedVisibility(
                visible = result != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                result?.let { r ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSage),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Price Breakdown", fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = DarkBrown)
                            Divider(color = BambooGreen.copy(alpha = 0.3f))
                            PriceRow("Material cost", "₹${r.materialCost.roundToInt()}")
                            PriceRow("Labour cost", "₹${r.labourCost.roundToInt()}")
                            PriceRow("Total cost", "₹${r.totalCost.roundToInt()}")
                            PriceRow("Profit (${r.margin.roundToInt()}%)", "₹${r.profit.roundToInt()}")
                            Divider(color = BambooGreen.copy(alpha = 0.3f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Suggested Price",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp, color = DarkBrown)
                                Text("₹${r.suggestedPrice.roundToInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp, color = BambooGreen)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BambooGreen.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "This price covers your materials, labour, and includes a fair profit for your skill.",
                                    fontSize = 12.sp, color = DarkBrown.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = DarkBrown.copy(alpha = 0.7f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkBrown)
    }
}