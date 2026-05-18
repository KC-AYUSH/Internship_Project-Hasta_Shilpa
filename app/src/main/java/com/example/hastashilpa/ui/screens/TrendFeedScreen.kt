package com.example.hastashilpa.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hastashilpa.data.model.DesignItem
import com.example.hastashilpa.data.model.getSampleDesignItems
import com.example.hastashilpa.ui.theme.BambooGreen
import com.example.hastashilpa.ui.theme.CreamWhite
import com.example.hastashilpa.ui.theme.DarkBrown
import com.example.hastashilpa.ui.theme.LightSage
import com.example.hastashilpa.viewmodel.TrendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendFeedScreen(
    innerPadding: PaddingValues,
    onItemClick: (DesignItem) -> Unit,
    viewModel: TrendViewModel = viewModel()
) {
    val firestoreTrends by viewModel.trends.collectAsState()
    val items = if (firestoreTrends.isNotEmpty()) firestoreTrends else getSampleDesignItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Design Trends", fontWeight = FontWeight.Bold,
                            fontSize = 20.sp, color = DarkBrown)
                        Text("Real-time bamboo & cane ideas", fontSize = 12.sp,
                            color = BambooGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { scaffoldPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp, end = 12.dp,
                top = scaffoldPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item -> 
                DesignCard(
                    item = item,
                    onClick = { onItemClick(item) }
                ) 
            }
        }
    }
}

@Composable
fun DesignCard(item: DesignItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LightSage
                ) {
                    Text(
                        text = item.category,
                        fontSize = 10.sp,
                        color = BambooGreen,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.name, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, color = DarkBrown)
                Text(item.description, fontSize = 11.sp,
                    color = DarkBrown.copy(alpha = 0.6f),
                    lineHeight = 15.sp,
                    maxLines = 2)
            }
        }
    }
}
