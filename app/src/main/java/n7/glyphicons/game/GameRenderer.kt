package n7.glyphicons.game

import android.content.Context
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixObject

internal class GameRenderer {

    private val squareSize = GameConfig.BIRD_SIZE
    private val matrix = IntArray(625)

    fun render(context: Context, gameState: GameState, obstacles: List<Obstacle>): GlyphMatrixFrame {
        matrix.fill(0)

        renderObstacles(obstacles)
        renderBird(gameState.birdY, gameState.birdSize)

        val scoreObject = GlyphMatrixObject.Builder()
            .setText(gameState.score.toString())
            .setScale(1)
            .setPosition(10, 19)
            .setBrightness(GameConfig.SCORE_BRIGHTNESS)
            .build()

        return GlyphMatrixFrame.Builder()
            .addMid(matrix)
            .addTop(scoreObject)
            .build(context)
    }

    private fun renderBird(birdY: Float, birdSize: Int) {
        val centerY = birdY.toInt()
        val centerX = 12
        val halfSize = birdSize / 2

        for (y in 0 until birdSize) {
            for (x in 0 until birdSize) {
                val targetY = centerY - halfSize + y
                val targetX = centerX - halfSize + x

                if (targetY in 0 until 25 && targetX in 0 until 25) {
                    val matrixIndex = targetY * 25 + targetX
                    matrix[matrixIndex] = GameConfig.BIRD_BRIGHTNESS
                }
            }
        }
    }

    private fun renderObstacles(obstacles: List<Obstacle>) {
        obstacles.forEach { obstacle ->
            val gapTop = obstacle.gapY - obstacle.gapSize / 2
            val gapBottom = obstacle.gapY + obstacle.gapSize / 2
            val obstacleX = obstacle.x.toInt()

            for (x in obstacleX until (obstacleX + 2).coerceAtMost(25)) {
                for (y in 0 until 25) {
                    if (y < gapTop || y > gapBottom) {
                        if (x in 0 until 25 && y in 0 until 25) {
                            val matrixIndex = y * 25 + x
                            matrix[matrixIndex] = GameConfig.OBSTACLE_BRIGHTNESS
                        }
                    }
                }
            }
        }
    }
}
