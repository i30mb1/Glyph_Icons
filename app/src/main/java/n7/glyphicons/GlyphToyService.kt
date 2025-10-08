package n7.glyphicons

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nothing.ketchum.GlyphMatrixManager

class GlyphToyService : Service() {

    private lateinit var glyphMatrixManager: GlyphMatrixManager

    override fun onCreate() {
        super.onCreate()
        glyphMatrixManager = GlyphMatrixManager.getInstance(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showDot()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showDot() {
        try {
            val frame = GlyphController.createDotFrame(this)
            glyphMatrixManager.setMatrixFrame(frame)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        glyphMatrixManager.unInit()
    }
}
