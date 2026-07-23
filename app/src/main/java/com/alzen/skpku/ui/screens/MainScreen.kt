package com.alzen.skpku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alzen.skpku.MainViewModel
import com.alzen.skpku.Skp
import com.alzen.skpku.SkpRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddSkp: () -> Unit,
    onSkpClick: (Skp) -> Unit,
    onLogout: () -> Unit
) {
    val skpList by viewModel.allSkpList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val studentName by viewModel.studentName.collectAsState("")
    val userId by viewModel.userId.collectAsState("")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            // Trigger a refresh when the user ID is available
            viewModel.refreshData(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SKPKU", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSkp,
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah SKP")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            DashboardHeader(studentName, skpList)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Repository SKP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(skpList) { index, skp ->
                    if (index == 0) {
                        Column {
                            Surface(
                                color = Color(0xFF3F51B5),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    text = "Record SKP Terbaru",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            SkpItem(skp, onSkpClick)
                        }
                    } else {
                        SkpItem(skp, onSkpClick)
                    }
                }

                if (skpList.isEmpty() && !isLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Belum ada data SKP", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(name: String, list: List<Skp>) {
    val totalPoin = list.sumOf { it.poinSkp }
    val target = 200
    val sisa = (target - totalPoin).coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Halo, $name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total SKP", fontSize = 14.sp, color = Color.Gray)
                    Text("$totalPoin Poin", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Target: $target", fontSize = 14.sp, color = Color.Gray)
                    Text("Sisa: $sisa", fontSize = 14.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SkpItem(skp: Skp, onClick: (Skp) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(skp) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(skp.namaKegiatan ?: "-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(skp.kategoriBidang ?: "-", fontSize = 14.sp, color = Color.Gray)
                Text(skp.tanggalInput ?: "-", fontSize = 12.sp, color = Color.LightGray)
            }
            Text(
                "${skp.poinSkp} Poin",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
                fontSize = 16.sp
            )
        }
    }
}
