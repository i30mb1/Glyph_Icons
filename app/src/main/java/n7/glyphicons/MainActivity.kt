package n7.glyphicons

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    SetupScreen(
                        modifier = Modifier.padding(innerPadding),
                        onInstallWidget = {
                            val intent = Intent().apply {
                                component = ComponentName(
                                    "com.nothing.thirdparty",
                                    "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"
                                )
                            }
                            startActivity(intent)
                        }
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
fun SetupScreen(
    modifier: Modifier = Modifier,
    onInstallWidget: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onInstallWidget) {
            Text("Установить виджет")
        }
    }
}
