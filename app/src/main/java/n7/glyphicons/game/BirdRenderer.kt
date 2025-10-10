package n7.glyphicons.game

import android.content.Context
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixObject

internal class BirdRenderer {

    private val squareSize = 7
    private val brightness = 255
    private val matrix = IntArray(625)

    fun render(context: Context, gameState: GameState): GlyphMatrixFrame {
        matrix.fill(0)

        val centerY = gameState.birdY.toInt()
        val centerX = 12
        val halfSize = squareSize / 2

        for (y in 0 until squareSize) {
            for (x in 0 until squareSize) {
                val targetY = centerY - halfSize + y
                val targetX = centerX - halfSize + x

                if (targetY in 0 until 25 && targetX in 0 until 25) {
                    val matrixIndex = targetY * 25 + targetX
                    matrix[matrixIndex] = brightness
                }
            }
        }

        return GlyphMatrixFrame.Builder()
            .addMid(matrix)
            .build(context)
    }
}
