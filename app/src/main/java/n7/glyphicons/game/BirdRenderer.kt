package n7.glyphicons.game

import android.content.Context
import com.nothing.ketchum.GlyphMatrixFrame

internal class BirdRenderer {

    private val birdX = 8
    private val brightness = 255

    fun render(context: Context, gameState: GameState): GlyphMatrixFrame {
        val matrix = IntArray(625) { 0 }

        val birdYInt = gameState.birdY.toInt()

        matrix[birdYInt * 25 + birdX] = brightness
        matrix[(birdYInt - 1) * 25 + birdX] = brightness
        matrix[(birdYInt + 1) * 25 + birdX] = brightness

        return GlyphMatrixFrame.Builder()
            .addMid(matrix)
            .build(context)
    }
}
