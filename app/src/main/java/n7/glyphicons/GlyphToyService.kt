package n7.glyphicons

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nothing.ketchum.GlyphMatrixManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.glyphicons.R

class GlyphToyService : Service() {

    private lateinit var glyphMatrixManager: GlyphMatrixManager
    private var animationPlayer: GlyphAnimationPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        glyphMatrixManager = GlyphMatrixManager.getInstance(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playPuckAnimation()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun playPuckAnimation() {
        val adapter = LottieToGlyphAdapter(this)
        animationPlayer = GlyphAnimationPlayer(glyphMatrixManager, adapter)

        scope.launch {
            val animation = adapter.loadAnimation(R.raw.puck)
            animation?.let {
                animationPlayer?.playAnimation(it, loop = true, fps = 30)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animationPlayer?.stop()
        glyphMatrixManager.unInit()
    }
}
