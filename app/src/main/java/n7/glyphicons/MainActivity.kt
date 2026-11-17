package n7.glyphicons

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import n7.glyph.ui.support.SupporterSection
import n7.glyph.ui.support.rememberSupporter
import n7.glyphicons.game.FlappyTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
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
        // The screen is always black: white status and navigation bar icons, also when the phone is in light theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
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
    val supporter = rememberSupporter()
    val supporterOwned by supporter.owned.collectAsState()
    var theme by remember(supporterOwned) { mutableStateOf(FlappyTheme.current(context)) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            bestScore = configStorage.getBestScore()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = N7Colors.Ground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // The status bar stays outside the scroll, so scrolled cards never run under the clock.
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .verticalScroll(rememberScrollState())
                // The gesture bar is inside it: the content scrolls under it, and the end of the page clears it.
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // The name and, right under it, one line that says what the app is; then the controls hint as before.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DotHeadline(lines = listOf("FLAPPY", "BOX"), fontSize = 56.sp)
                Text(stringResource(R.string.tagline), style = N7Type.Body, color = N7Colors.InkMuted)
                SectionLabel(stringResource(R.string.main_hint))
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GlyphMatrix(
                    cells = previewFrame(gapSize, birdSize, theme),
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

            ThemeCard(theme, supporterOwned) {
                theme = it
                FlappyTheme.save(context, it)
            }

            SupporterSection(perks = listOf(stringResource(R.string.supporter_perk)), supporter = supporter)

            MoreFromN7(Modifier.fillMaxWidth())

            SignatureLabel(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

/** The bird in the middle and one pipe on the right, with the configured gap, in [theme], as the matrix shows it. */
private fun previewFrame(gapSize: Int, birdSize: Int, theme: FlappyTheme = FlappyTheme.CLASSIC): List<Cell> = buildList {
    val half = birdSize / 2
    for (y in 0 until birdSize) for (x in 0 until birdSize) {
        if (theme.box(x, y, birdSize)) add(Cell(12 - half + x, 12 - half + y, 1f))
    }
    val gapTop = 12 - gapSize / 2
    val gapBottom = 12 + gapSize / 2
    for (y in 0 until 25) for (dx in -1..2) {
        val level = theme.obstacle(dx, y, gapTop, gapBottom, 0L)
        if (level > 0) add(Cell(19 + dx, y, (level / 1600f).coerceIn(0.15f, 1f)))
    }
}

/** The look of the obstacles and the box: CLASSIC for everyone, three more for supporters, each with a preview. */
@Composable
private fun ThemeCard(theme: FlappyTheme, owned: Boolean, onPick: (FlappyTheme) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(stringResource(R.string.theme_title))
        N7Card(Modifier.fillMaxWidth()) {
            for (t in FlappyTheme.entries) {
                val unlocked = !t.supporter || owned
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(selected = t == theme, enabled = unlocked, role = Role.RadioButton) { onPick(t) }
                        .alpha(if (unlocked) 1f else 0.45f)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .border(2.dp, if (t == theme) N7Colors.Ink else N7Colors.ControlBorder, CircleShape)
                            .padding(5.dp),
                    ) {
                        if (t == theme) Box(Modifier.fillMaxSize().background(N7Colors.Ink, CircleShape))
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(stringResource(themeName(t)), style = N7Type.Title, color = N7Colors.Ink)
                        if (!unlocked) Text(stringResource(R.string.theme_locked), style = N7Type.BodyS, color = N7Colors.InkMuted)
                    }
                    GlyphMatrix(cells = previewFrame(8, 4, t), modifier = Modifier.size(56.dp), glow = false)
                }
            }
        }
    }
}

private fun themeName(theme: FlappyTheme): Int = when (theme) {
    FlappyTheme.CLASSIC -> R.string.theme_classic
    FlappyTheme.PIPES -> R.string.theme_pipes
    FlappyTheme.BRICKS -> R.string.theme_bricks
    FlappyTheme.LASER -> R.string.theme_laser
}
