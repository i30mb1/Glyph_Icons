package n7.glyphicons

import android.content.Context
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixObject

object GlyphController {

    fun createDotFrame(context: Context): GlyphMatrixFrame {
        val glyphObject = GlyphMatrixObject.Builder()
            .setPosition(32, 32)
            .setRawArray(intArrayOf(1))
            .build()

        return GlyphMatrixFrame.Builder()
            .addMid(glyphObject)
            .build(context)
    }
}
