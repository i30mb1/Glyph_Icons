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
        renderBird(gameState.birdY, gameState.birdSize, gameState.currentSpeedMultiplier)

        val scoreText = gameState.score.toString()
        val scoreX = when {
            scoreText.length >= 3 -> 4
            scoreText.length >= 2 -> 7
            else -> 10
        }

        val scoreObject = GlyphMatrixObject.Builder()
            .setText(scoreText)
            .setScale(1)
            .setPosition(scoreX, 19)
            .setBrightness(GameConfig.SCORE_BRIGHTNESS)
            .build()

        println(matrix.joinToString(","))
        return GlyphMatrixFrame.Builder()
            .addMid(matrix)
            .addTop(scoreObject)
            .build(context)
    }

    private fun renderBird(birdY: Float, birdSize: Int, speedMultiplier: Float) {
        val centerY = birdY.toInt()
        val centerX = 12
        val halfSize = birdSize / 2

        val brightness = calculateBirdBrightness(speedMultiplier)

        for (y in 0 until birdSize) {
            for (x in 0 until birdSize) {
                val targetY = centerY - halfSize + y
                val targetX = centerX - halfSize + x

                if (targetY in 0 until 25 && targetX in 0 until 25) {
                    val matrixIndex = targetY * 25 + targetX
                    matrix[matrixIndex] = brightness
                }
            }
        }
    }

    private fun calculateBirdBrightness(speedMultiplier: Float): Int {
        return when {
            speedMultiplier >= 5.0f -> 2048
            speedMultiplier >= 4.0f -> 1550
            speedMultiplier >= 3.0f -> 1050
            speedMultiplier >= 2.0f -> 550
            else -> 50
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
