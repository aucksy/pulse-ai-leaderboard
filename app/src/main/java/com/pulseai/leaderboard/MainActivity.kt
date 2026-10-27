package com.pulseai.leaderboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulseai.leaderboard.ui.screens.CompareScreen
import com.pulseai.leaderboard.ui.screens.FrontierScreen
import com.pulseai.leaderboard.ui.screens.LeaderboardScreen
import com.pulseai.leaderboard.ui.screens.ModelDetailSheet
import com.pulseai.leaderboard.ui.theme.AccentIndigo
import com.pulseai.leaderboard.ui.theme.BgObsidian
import com.pulseai.leaderboard.ui.theme.PulseAITheme
import com.pulseai.leaderboard.ui.theme.SurfaceCard
import com.pulseai.leaderboard.ui.theme.SurfaceCardBorder
import com.pulseai.leaderboard.ui.theme.TextMuted
import com.pulseai.leaderboard.ui.theme.TextPrimary
import com.pulseai.leaderboard.ui.viewmodel.LeaderboardViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PulseAITheme {
                val app = application as PulseAiApplication
                val viewModel: LeaderboardViewModel = viewModel(
                    factory = LeaderboardViewModel.Factory(app.repository)
                )

                PulseAiApp(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector) {
    LEADERBOARD("Leaderboard", Icons.Default.Leaderboard),
    COMPARE("Compare", Icons.AutoMirrored.Filled.CompareArrows),
    FRONTIER("Frontier", Icons.AutoMirrored.Filled.ShowChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseAiApp(viewModel: LeaderboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgObsidian,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder)
            ) {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(text = tab.title, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentIndigo,
                            selectedTextColor = AccentIndigo,
                            indicatorColor = AccentIndigo.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> LeaderboardScreen(
                    viewModel = viewModel,
                    onModelClick = { model ->
                        viewModel.selectModelForDetail(model)
                    }
                )
                1 -> CompareScreen(viewModel = viewModel)
                2 -> FrontierScreen(
                    viewModel = viewModel,
                    onModelClick = { model ->
                        viewModel.selectModelForDetail(model)
                    }
                )
            }

            // Model Deep-Dive Sheet
            if (uiState.selectedModelForDetail != null) {
                ModelDetailSheet(
                    model = uiState.selectedModelForDetail!!,
                    uiState = uiState,
                    sheetState = sheetState,
                    onDismiss = { viewModel.selectModelForDetail(null) },
                    onVolumeChange = { tokens -> viewModel.setMonthlyVolumeTokens(tokens) }
                )
            }
        }
    }
}
