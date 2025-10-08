package n7.glyphicons

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.graphics.BitmapCompat
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject

class GlyphToyService : Service() {

    private lateinit var glyphMatrixManager: GlyphMatrixManager

    override fun onCreate() {
        super.onCreate()
        glyphMatrixManager = GlyphMatrixManager.getInstance(applicationContext)
        glyphMatrixManager.init(object : GlyphMatrixManager.Callback {
            override fun onServiceConnected(componentName: ComponentName?) {
                glyphMatrixManager.register(packageName)
                showCenterPixel()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {}
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showCenterPixel() {
        val matrix = IntArray(25 * 25) { 0 }
        matrix[12 * 25 + 12] = 100

        val imageSource = BitmapFactory.decodeResource(resources, R.drawable.ff)
        val text = GlyphMatrixObject.Builder()
            .setImageSource(imageSource)
//            .setScale(7)
//            .setPosition(0,9)
            .build()

        val frame = GlyphMatrixFrame.Builder()
            .addMid(text)
            .build(applicationContext)

        glyphMatrixManager.setMatrixFrame(frame)
    }

    override fun onDestroy() {
        super.onDestroy()
        glyphMatrixManager.unInit()
    }
}
