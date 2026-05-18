package com.example.hastashilpa.ui.screens

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hastashilpa.ui.theme.*
import com.example.hastashilpa.viewmodel.BlueprintViewModel
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File
import java.io.FileOutputStream

data class Blueprint(
    val name: String,
    val width: String,
    val height: String,
    val depth: String,
    val material: String,
    val poles: String,
    val difficulty: String,
    val imageUrl: String = ""
)

val sampleBlueprints = listOf(
    Blueprint("Bamboo Pendant Light", "25 cm", "40 cm", "25 cm", "Bamboo strips 5mm", "N/A", "Intermediate", "https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=500"),
    Blueprint("Modern Cane Side Table", "45 cm", "50 cm", "45 cm", "Cane mesh & wood", "2 main poles", "Advanced", "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=500"),
    Blueprint("Bamboo Laptop Stand 2.0", "35 cm", "10 cm", "28 cm", "Carbonized bamboo", "4 short poles", "Beginner", "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=500"),
    Blueprint("Cane Weave Accent Chair", "70 cm", "85 cm", "65 cm", "Rattan & Linen", "6 structural poles", "Advanced", "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500"),
    Blueprint("Minimalist Bamboo Shelf", "60 cm", "15 cm", "20 cm", "Solid bamboo slat", "2 support poles", "Beginner", "https://images.unsplash.com/photo-1594026112284-02bb6f3352fe?w=500"),
    Blueprint("Cane Planter Trio", "30 cm", "25 cm", "30 cm", "Cane strips 8mm", "N/A", "Intermediate", "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=500"),
    Blueprint("Bamboo Desk Organizer", "35 cm", "12 cm", "15 cm", "Polished bamboo", "N/A", "Beginner", "https://images.unsplash.com/photo-1516533075015-a3838414c3cb?w=500"),
    Blueprint("Woven Cane Partition", "150 cm", "180 cm", "3 cm", "Natural cane mesh", "12 long poles", "Advanced", "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500"),
    Blueprint("Bamboo Mirror Frame", "60 cm", "60 cm", "5 cm", "Bamboo spokes", "30 small spokes", "Intermediate", "https://images.unsplash.com/photo-1618220179428-22790b461013?w=500"),
    Blueprint("Cane Headboard", "160 cm", "120 cm", "5 cm", "Viennese cane mesh", "4 heavy poles", "Advanced", "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500"),
    Blueprint("Bamboo Bluetooth Speaker Case", "20 cm", "12 cm", "10 cm", "Aged Bamboo", "1 hollow pole", "Intermediate", "https://images.unsplash.com/photo-1608155613951-344414ced42f?w=500"),
    Blueprint("Cane Hanging Egg Chair", "100 cm", "130 cm", "80 cm", "Reinforced Cane", "2 main hoops", "Expert", "https://images.unsplash.com/photo-1595514535311-66520b789182?w=500"),
    Blueprint("Bamboo Wine Rack", "30 cm", "25 cm", "20 cm", "Laminated Bamboo", "6 cut segments", "Intermediate", "https://images.unsplash.com/photo-1594498653385-d5172b532c00?w=500"),
    Blueprint("Cane Sun Lounger", "60 cm", "30 cm", "180 cm", "Cane Mesh", "4 long rails", "Advanced", "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500"),
    Blueprint("Bamboo Utensil Set", "30 cm", "5 cm", "1 cm", "Organic Bamboo", "N/A", "Beginner", "https://images.unsplash.com/photo-1591871937573-748906562b8b?w=500")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintScreen(
    innerPadding: PaddingValues,
    initialBlueprintName: String? = null,
    viewModel: BlueprintViewModel = viewModel()
) {
    val firestoreBlueprints by viewModel.blueprints.collectAsState()
    val blueprints = if (firestoreBlueprints.isNotEmpty()) firestoreBlueprints else sampleBlueprints
    
    fun getEffectiveBlueprint(name: String?, list: List<Blueprint>): Blueprint {
        if (name == null) return list[0]
        return list.find { it.name == name } ?: Blueprint(
            name = name,
            width = "Custom",
            height = "Custom",
            depth = "Custom",
            material = "Bamboo / Cane",
            poles = "Per Design",
            difficulty = "Expert",
            imageUrl = ""
        )
    }

    var selected by remember(initialBlueprintName, blueprints) { 
        mutableStateOf(getEffectiveBlueprint(initialBlueprintName, blueprints))
    }

    val zoomState = rememberZoomState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(selected.name, fontWeight = FontWeight.Bold,
                            fontSize = 20.sp, color = DarkBrown)
                        Text("Technical Dimensions",
                            fontSize = 12.sp, color = BambooGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        Toast.makeText(context, "Blueprint data saved!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
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
                .padding(scaffoldPadding)
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = selected.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Measurement Prototype (Without Picture)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .zoomable(zoomState)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D2137)),
                contentAlignment = Alignment.Center
            ) {
                TechnicalOverlay(selected)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Specs card (Restore specs)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Specifications", fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = DarkBrown)
                    Spacer(modifier = Modifier.height(12.dp))
                    SpecRow("Width", selected.width)
                    SpecRow("Height", selected.height)
                    SpecRow("Depth", selected.depth)
                    SpecRow("Material", selected.material)
                    SpecRow("Bamboo Poles", selected.poles)
                    SpecRow("Difficulty", selected.difficulty)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TechnicalOverlay(bp: Blueprint) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val p = 60f
        
        // Horizontal measurement line (Width)
        drawLine(
            color = Color(0xFFFFD54F),
            start = androidx.compose.ui.geometry.Offset(p, h/2),
            end = androidx.compose.ui.geometry.Offset(w - p, h/2),
            strokeWidth = 3f
        )
        
        // Vertical measurement line (Height)
        drawLine(
            color = Color(0xFF4FC3F7),
            start = androidx.compose.ui.geometry.Offset(w/2, p),
            end = androidx.compose.ui.geometry.Offset(w/2, h - p),
            strokeWidth = 3f
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Width Label
        Text(
            text = "WIDTH: ${bp.width}",
            color = Color(0xFFFFD54F),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 40.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        
        // Height Label
        Text(
            text = "HEIGHT: ${bp.height}",
            color = Color(0xFF4FC3F7),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 40.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )

        // Depth Label (Bottom)
        Text(
            text = "DEPTH: ${bp.depth}",
            color = Color(0xFF81C784),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = DarkBrown.copy(alpha = 0.6f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkBrown)
    }
    HorizontalDivider(color = LightSage, thickness = 0.5.dp)
}
