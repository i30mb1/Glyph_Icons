package n7.glyphicons

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.nothing.ketchum.GlyphMatrixManager
import n7.glyphicons.ui.theme.GlyphIconsTheme

class MainActivity : ComponentActivity() {

    private lateinit var configStorage: n7.glyphicons.game.GameConfigStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configStorage = n7.glyphicons.game.GameConfigStorage(this)
        configStorage.loadConfig()

        setContent {
            GlyphIconsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { paddingValues ->
                    SetupScreen(
                        modifier = Modifier.fillMaxSize(),
                        onInstallWidget = {
                            val intent = Intent().apply {
                                component = ComponentName(
                                    "com.nothing.thirdparty",
                                    "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"
                                )
                            }
                            startActivity(intent)
                        },
                        onConfigChanged = {
                            configStorage.saveConfig()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SetupScreen(
    modifier: Modifier = Modifier,
    onInstallWidget: () -> Unit,
    onConfigChanged: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val configStorage = remember { n7.glyphicons.game.GameConfigStorage(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var gapSize by remember { mutableIntStateOf(n7.glyphicons.game.GameConfig.OBSTACLE_GAP_SIZE) }
    var spawnInterval by remember { mutableFloatStateOf(n7.glyphicons.game.GameConfig.OBSTACLE_SPAWN_INTERVAL) }
    var speed by remember { mutableFloatStateOf(n7.glyphicons.game.GameConfig.OBSTACLE_SPEED) }
    var birdSize by remember { mutableIntStateOf(n7.glyphicons.game.GameConfig.BIRD_SIZE) }
    var bestScore by remember { mutableIntStateOf(configStorage.getBestScore()) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            bestScore = configStorage.getBestScore()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.hint_text),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    textDecoration = if (bestScore > 100) TextDecoration.LineThrough else null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.best_score, bestScore),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        GamePreview(
            gapSize = gapSize,
            birdSize = birdSize
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onInstallWidget) {
            Text(stringResource(R.string.activate_toy))
        }

        Spacer(modifier = Modifier.height(32.dp))

        ParameterControl(
            label = "Gap Size",
            value = gapSize,
            onDecrease = {
                if (gapSize > 4) {
                    gapSize--
                    n7.glyphicons.game.GameConfig.OBSTACLE_GAP_SIZE = gapSize
                    onConfigChanged()
                }
            },
            onIncrease = {
                if (gapSize < 15) {
                    gapSize++
                    n7.glyphicons.game.GameConfig.OBSTACLE_GAP_SIZE = gapSize
                    onConfigChanged()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParameterControl(
            label = "Spawn Interval (s)",
            value = spawnInterval,
            onDecrease = {
                if (spawnInterval > 1f) {
                    spawnInterval -= 0.5f
                    n7.glyphicons.game.GameConfig.OBSTACLE_SPAWN_INTERVAL = spawnInterval
                    onConfigChanged()
                }
            },
            onIncrease = {
                if (spawnInterval < 5f) {
                    spawnInterval += 0.5f
                    n7.glyphicons.game.GameConfig.OBSTACLE_SPAWN_INTERVAL = spawnInterval
                    onConfigChanged()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParameterControl(
            label = "Speed",
            value = speed,
            onDecrease = {
                if (speed > 3f) {
                    speed -= 1f
                    n7.glyphicons.game.GameConfig.OBSTACLE_SPEED = speed
                    onConfigChanged()
                }
            },
            onIncrease = {
                if (speed < 15f) {
                    speed += 1f
                    n7.glyphicons.game.GameConfig.OBSTACLE_SPEED = speed
                    onConfigChanged()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParameterControl(
            label = "Bird Size",
            value = birdSize,
            onDecrease = {
                if (birdSize > 2) {
                    birdSize--
                    n7.glyphicons.game.GameConfig.BIRD_SIZE = birdSize
                    onConfigChanged()
                }
            },
            onIncrease = {
                if (birdSize < 8) {
                    birdSize++
                    n7.glyphicons.game.GameConfig.BIRD_SIZE = birdSize
                    onConfigChanged()
                }
            }
        )
    }
}

@Composable
fun GamePreview(
    gapSize: Int,
    birdSize: Int
) {
    Box(
        modifier = Modifier
            .size(250.dp)
            .background(Color.Black)
            .border(2.dp, Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / 25f

            val birdX = 12f
            val birdY = 12f
            val halfBirdSize = birdSize / 2f

            for (y in 0 until birdSize) {
                for (x in 0 until birdSize) {
                    val targetY = (birdY - halfBirdSize + y) * cellSize
                    val targetX = (birdX - halfBirdSize + x) * cellSize
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(targetX, targetY),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
            }

            val obstacleX = 20f
            val gapY = 12
            val gapTop = gapY - gapSize / 2
            val gapBottom = gapY + gapSize / 2

            for (y in 0 until 25) {
                if (y < gapTop || y > gapBottom) {
                    for (x in 0 until 2) {
                        val posX = (obstacleX + x) * cellSize
                        val posY = y * cellSize
                        drawRect(
                            color = Color.Gray,
                            topLeft = Offset(posX, posY),
                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterControl(
    label: String,
    value: Number,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(onClick = onDecrease) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = when (value) {
                    is Float -> String.format("%.1f", value)
                    else -> value.toString()
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.width(80.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = onIncrease) {
                Text("+")
            }
        }
    }
}
