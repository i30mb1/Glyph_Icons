package n7.glyphicons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.glyph.ui.components.DotHeadline
import n7.glyph.ui.components.N7Card
import n7.glyph.ui.components.N7Divider
import n7.glyph.ui.components.SignatureLabel
import n7.glyph.ui.theme.N7Colors
import n7.glyph.ui.theme.N7GlyphTheme
import n7.glyph.ui.theme.N7Type
import n7.glyphicons.game.GameConfigStorage

class ToyIntroductionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        GameConfigStorage(this).loadConfig()

        setContent {
            N7GlyphTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = N7Colors.Ground) {
                    IntroductionScreen()
                }
            }
        }
    }
}

@Composable
private fun IntroductionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        DotHeadline(lines = listOf("FLAPPY", "BOX"), fontSize = 48.sp)
        N7Card(Modifier.fillMaxWidth()) {
            Rule("01", stringResource(R.string.intro_control))
            N7Divider()
            Rule("02", stringResource(R.string.intro_obstacles))
            N7Divider()
            Rule("03", stringResource(R.string.intro_boost))
            N7Divider()
            Rule("04", stringResource(R.string.intro_difficulty))
            N7Divider()
            Rule("05", stringResource(R.string.intro_hardcore), color = N7Colors.Ink)
        }
        SignatureLabel()
    }
}

@Composable
private fun Rule(number: String, text: String, color: Color = N7Colors.InkMuted) {
    Row(Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(number, style = N7Type.BodyS.copy(fontFamily = N7Type.Label.fontFamily), color = N7Colors.InkSubtle)
        Text(text, style = N7Type.BodyS, color = color)
    }
}
