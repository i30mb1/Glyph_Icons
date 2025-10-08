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
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject
import n7.glyphicons.ui.theme.GlyphIconsTheme

class MainActivity : ComponentActivity() {
    private var glyphMatrixManager: GlyphMatrixManager? = null

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
                showDot()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {}
        })
    }

    private fun showDot() {
        try {
            val frame = GlyphController.createDotFrame(this)
            glyphMatrixManager?.setMatrixFrame(frame)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
