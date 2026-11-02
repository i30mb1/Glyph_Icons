package n7.glyphicons

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import n7.glyph.ui.components.DotHeadline
import n7.glyph.ui.components.GlyphMatrix
import n7.glyph.ui.components.N7Button
import n7.glyph.ui.components.N7Card
import n7.glyph.ui.components.N7Divider
import n7.glyph.ui.components.N7Stepper
import n7.glyph.ui.components.ScoreDisplay
import n7.glyph.ui.components.SectionLabel
import n7.glyph.ui.components.SignatureLabel
import n7.glyph.ui.family.MoreFromN7
import n7.glyph.ui.matrix.Cell
import n7.glyph.ui.theme.N7Colors
import n7.glyph.ui.theme.N7GlyphTheme
import n7.glyph.ui.theme.N7Type
import n7.glyphicons.game.GameConfig
import n7.glyphicons.game.GameConfigStorage
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var configStorage: GameConfigStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        configStorage = GameConfigStorage(this)
        configStorage.loadConfig()

        setContent {
            N7GlyphTheme {
                SetupScreen(
                    onActivateToy = {
                        val intent = Intent().apply {
                            component = ComponentName(
                                "com.nothing.thirdparty",
                                "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity",
                            )
                        }
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            // Older system versions have no Glyph Toys manager
                        }
                    },
                    onConfigChanged = { configStorage.saveConfig() },
                )
            }
        }
    }
}

@Composable
private fun SetupScreen(
    onActivateToy: () -> Unit,
    onConfigChanged: () -> Unit,
) {
    val context = LocalContext.current
    val configStorage = remember { GameConfigStorage(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var gapSize by remember { mutableIntStateOf(GameConfig.OBSTACLE_GAP_SIZE) }
    var spawnInterval by remember { mutableFloatStateOf(GameConfig.OBSTACLE_SPAWN_INTERVAL) }
    var speed by remember { mutableFloatStateOf(GameConfig.OBSTACLE_SPEED) }
    var birdSize by remember { mutableIntStateOf(GameConfig.BIRD_SIZE) }
    var bestScore by remember { mutableIntStateOf(configStorage.getBestScore()) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            bestScore = configStorage.getBestScore()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = N7Colors.Ground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DotHeadline(lines = listOf("FLAPPY", "BOX"), fontSize = 56.sp)
                SectionLabel(stringResource(R.string.main_hint))
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GlyphMatrix(
                    cells = previewFrame(gapSize, birdSize),
                    modifier = Modifier.size(224.dp),
                    contentDescription = stringResource(R.string.main_preview_description),
                )
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                ScoreDisplay(value = bestScore, label = stringResource(R.string.best_score))
                Text(
                    text = stringResource(R.string.hint_text),
                    style = N7Type.BodyS,
                    color = N7Colors.InkMuted,
                    textDecoration = if (bestScore > 100) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f),
                )
            }

            N7Button(
                text = stringResource(R.string.activate_toy),
                onClick = onActivateToy,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(stringResource(R.string.settings_title))
                N7Card(Modifier.fillMaxWidth()) {
                    N7Stepper(
                        label = stringResource(R.string.setting_gap),
                        value = gapSize.toString(),
                        canDecrease = gapSize > 4,
                        canIncrease = gapSize < 15,
                        onDecrease = { gapSize--; GameConfig.OBSTACLE_GAP_SIZE = gapSize; onConfigChanged() },
                        onIncrease = { gapSize++; GameConfig.OBSTACLE_GAP_SIZE = gapSize; onConfigChanged() },
                    )
                    N7Divider()
                    N7Stepper(
                        label = stringResource(R.string.setting_spawn),
                        value = "%.1f".format(Locale.US, spawnInterval),
                        canDecrease = spawnInterval > 1f,
                        canIncrease = spawnInterval < 5f,
                        onDecrease = { spawnInterval -= 0.5f; GameConfig.OBSTACLE_SPAWN_INTERVAL = spawnInterval; onConfigChanged() },
                        onIncrease = { spawnInterval += 0.5f; GameConfig.OBSTACLE_SPAWN_INTERVAL = spawnInterval; onConfigChanged() },
                    )
                    N7Divider()
                    N7Stepper(
                        label = stringResource(R.string.setting_speed),
                        value = "%.0f".format(Locale.US, speed),
                        canDecrease = speed > 3f,
                        canIncrease = speed < 15f,
                        onDecrease = { speed -= 1f; GameConfig.OBSTACLE_SPEED = speed; onConfigChanged() },
                        onIncrease = { speed += 1f; GameConfig.OBSTACLE_SPEED = speed; onConfigChanged() },
                    )
                    N7Divider()
                    N7Stepper(
                        label = stringResource(R.string.setting_bird),
                        value = birdSize.toString(),
                        canDecrease = birdSize > 2,
                        canIncrease = birdSize < 8,
                        onDecrease = { birdSize--; GameConfig.BIRD_SIZE = birdSize; onConfigChanged() },
                        onIncrease = { birdSize++; GameConfig.BIRD_SIZE = birdSize; onConfigChanged() },
                    )
                }
            }

            MoreFromN7(Modifier.fillMaxWidth())

            SignatureLabel(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

/** The bird in the middle and one pipe on the right, with the configured gap, as the matrix shows it. */
private fun previewFrame(gapSize: Int, birdSize: Int): List<Cell> = buildList {
    val half = birdSize / 2
    for (y in 0 until birdSize) for (x in 0 until birdSize) add(Cell(12 - half + x, 12 - half + y, 1f))
    val gapTop = 12 - gapSize / 2
    val gapBottom = 12 + gapSize / 2
    for (y in 0 until 25) {
        if (y < gapTop || y > gapBottom) {
            add(Cell(19, y, 0.5f))
            add(Cell(20, y, 0.5f))
        }
    }
}
