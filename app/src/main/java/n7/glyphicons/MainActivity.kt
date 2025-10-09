package n7.glyphicons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nothing.ketchum.GlyphMatrixManager
import n7.glyphicons.ui.theme.GlyphIconsTheme

class MainActivity : ComponentActivity() {
    private var glyphMatrixManager: GlyphMatrixManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GlyphIconsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LottieAnimationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        glyphMatrixManager?.unInit()
    }
}

@Composable
fun LottieAnimationScreen(modifier: Modifier = Modifier) {
//    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim))

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        LottieAnimation(
//            composition = composition,
//            iterations = LottieConstants.IterateForever,
//            isPlaying = true,
//            restartOnPlay = true,
//            modifier = Modifier
//                .size(300.dp)
//        )
    }
}

@Preview(showBackground = true)
@Composable
fun LottieAnimationPreview() {
    GlyphIconsTheme {
        LottieAnimationScreen()
    }
}
