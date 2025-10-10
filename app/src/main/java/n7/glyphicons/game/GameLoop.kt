package n7.glyphicons.game

import android.os.Handler
import android.os.Looper
import android.util.Log

internal class GameLoop(
    private val onUpdate: (deltaTime: Float) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val frameDelayMs = 50L
    private var frameCount = 0L
    private val deltaTime = frameDelayMs / 1000f

    private val updateRunnable = object : Runnable {
        override fun run() {
            frameCount++
            onUpdate(deltaTime)
            handler.postDelayed(this, frameDelayMs)
        }
    }

    fun start() {
        Log.d("N7", "GameLoop started")
        handler.post(updateRunnable)
    }

    fun stop() {
        Log.d("N7", "GameLoop stopped at frame #$frameCount")
        handler.removeCallbacks(updateRunnable)
    }
}
