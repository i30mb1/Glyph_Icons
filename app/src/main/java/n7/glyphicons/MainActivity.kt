package n7.glyphicons

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nothing.ketchum.GlyphMatrixManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.glyphicons.ui.theme.GlyphIconsTheme

class MainActivity : ComponentActivity() {
    private var glyphMatrixManager: GlyphMatrixManager? = null
    private var animationPlayer: GlyphAnimationPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initGlyphMatrix()

        setContent {
            GlyphIconsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun initGlyphMatrix() {
        glyphMatrixManager = GlyphMatrixManager.getInstance(this)
        glyphMatrixManager?.init(object : GlyphMatrixManager.Callback {
            override fun onServiceConnected(componentName: ComponentName?) {
                glyphMatrixManager?.register(packageName)
                playPuckAnimation()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {}
        })
    }

    private fun playPuckAnimation() {
        glyphMatrixManager?.let { manager ->
            val adapter = LottieToGlyphAdapter(this)
            animationPlayer = GlyphAnimationPlayer(manager, adapter)

            scope.launch {
                val animation = adapter.loadAnimation(R.raw.puck)
                animation?.let {
                    animationPlayer?.playAnimation(it, loop = true, fps = 30)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animationPlayer?.stop()
        glyphMatrixManager?.unInit()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GlyphIconsTheme {
        Greeting("Android")
    }
}
