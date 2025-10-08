package n7.glyphicons

import android.os.Handler
import android.os.Looper
import com.airbnb.lottie.LottieDrawable
import com.nothing.ketchum.GlyphMatrixManager

class GlyphAnimationPlayer(
    private val glyphMatrixManager: GlyphMatrixManager,
    private val adapter: LottieToGlyphAdapter
) {

    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    private var currentAnimation: AnimationState? = null

    fun playAnimation(
        animation: LottieToGlyphAdapter.LottieAnimation,
        loop: Boolean = true,
        fps: Int = 30
    ) {
        stop()

        val lottieDrawable = LottieDrawable().apply {
            composition = animation.composition
        }

        val frameDelay = 1000L / fps
        val totalFrames = animation.frameCount

        currentAnimation = AnimationState(
            lottieDrawable = lottieDrawable,
            totalFrames = totalFrames,
            loop = loop,
            frameDelay = frameDelay
        )

        isPlaying = true
        playNextFrame()
    }

    private fun playNextFrame() {
        val state = currentAnimation ?: return
        if (!isPlaying) return

        try {
            val progress = state.currentFrame.toFloat() / state.totalFrames
            val frame = adapter.convertFrameToGlyph(state.lottieDrawable, progress)
            glyphMatrixManager.setMatrixFrame(frame)

            state.currentFrame++

            if (state.currentFrame >= state.totalFrames) {
                if (state.loop) {
                    state.currentFrame = 0
                } else {
                    stop()
                    return
                }
            }

            handler.postDelayed({ playNextFrame() }, state.frameDelay)
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    fun stop() {
        isPlaying = false
        handler.removeCallbacksAndMessages(null)
        currentAnimation = null
    }

    fun isPlaying(): Boolean = isPlaying

    private data class AnimationState(
        val lottieDrawable: LottieDrawable,
        val totalFrames: Int,
        val loop: Boolean,
        val frameDelay: Long,
        var currentFrame: Int = 0
    )
}
