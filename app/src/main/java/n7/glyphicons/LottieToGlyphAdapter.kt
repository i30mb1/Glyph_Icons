package n7.glyphicons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LottieToGlyphAdapter(private val context: Context) {

    companion object {
        private const val GLYPH_SIZE = 33
        private const val RENDER_SIZE = 512
        private const val BRIGHTNESS_THRESHOLD = 128
    }

    suspend fun loadAnimation(rawResId: Int): LottieAnimation? = withContext(Dispatchers.IO) {
        try {
            val result = LottieCompositionFactory.fromRawResSync(context, rawResId)
            result.value?.let { composition ->
                LottieAnimation(composition)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convertFrameToGlyph(lottieDrawable: LottieDrawable, progress: Float): GlyphMatrixFrame {
        lottieDrawable.progress = progress

        val bitmap = Bitmap.createBitmap(RENDER_SIZE, RENDER_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        lottieDrawable.setBounds(0, 0, RENDER_SIZE, RENDER_SIZE)
        lottieDrawable.draw(canvas)

        val glyphData = downscaleAndThreshold(bitmap)
        bitmap.recycle()

        val flatArray = glyphData.flatMap { it.toList() }.toIntArray()

        val glyphObject = GlyphMatrixObject.Builder()
            .setPosition(0, 0)
            .setRawArray(flatArray)
            .build()

        return GlyphMatrixFrame.Builder()
            .addMid(glyphObject)
            .build(context)
    }

    private fun downscaleAndThreshold(bitmap: Bitmap): Array<IntArray> {
        val glyphData = Array(GLYPH_SIZE) { IntArray(GLYPH_SIZE) }
        val scaleX = bitmap.width.toFloat() / GLYPH_SIZE
        val scaleY = bitmap.height.toFloat() / GLYPH_SIZE

        for (y in 0 until GLYPH_SIZE) {
            for (x in 0 until GLYPH_SIZE) {
                val srcX = (x * scaleX).toInt().coerceIn(0, bitmap.width - 1)
                val srcY = (y * scaleY).toInt().coerceIn(0, bitmap.height - 1)
                val pixel = bitmap.getPixel(srcX, srcY)

                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                glyphData[y][x] = if (brightness > BRIGHTNESS_THRESHOLD) 1 else 0
            }
        }

        return glyphData
    }

    data class LottieAnimation(
        val composition: LottieComposition
    ) {
        val duration: Long
            get() = composition.duration.toLong()

        val frameCount: Int
            get() = (composition.endFrame - composition.startFrame).toInt()
    }
}
