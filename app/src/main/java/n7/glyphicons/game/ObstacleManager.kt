package n7.glyphicons.game

import android.util.Log
import kotlin.random.Random

internal class ObstacleManager(
    private val bestScore: Int
) {
    private val obstacles = mutableListOf<Obstacle>()
    private var score = 0
    private var spawnTimer = 0f

    private val spawnInterval = GameConfig.OBSTACLE_SPAWN_INTERVAL
    private val obstacleSpeed = GameConfig.OBSTACLE_SPEED
    private val gapSize = GameConfig.OBSTACLE_GAP_SIZE
    private val obstacleWidth = 2

    fun update(deltaTime: Float, speedMultiplier: Float = 1.0f) {
        spawnTimer += deltaTime * speedMultiplier

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnObstacle()
        }

        obstacles.forEach { obstacle ->
            obstacle.x -= obstacleSpeed * speedMultiplier * deltaTime
        }

        obstacles.removeAll { it.x.toInt() + obstacleWidth < 0 }

        Log.d("N7", "Obstacles count: ${obstacles.size}, timer: $spawnTimer, multiplier: $speedMultiplier")
    }

    fun checkCollision(birdY: Float, birdSize: Int): Boolean {
        val birdX = 12
        val halfBirdSize = birdSize / 2

        obstacles.forEach { obstacle ->
            val birdLeft = birdX - halfBirdSize
            val birdRight = birdX + halfBirdSize
            val birdTop = birdY.toInt() - halfBirdSize
            val birdBottom = birdY.toInt() + halfBirdSize

            val obstacleLeft = obstacle.x.toInt()
            val obstacleRight = obstacle.x.toInt() + obstacleWidth

            if (birdRight >= obstacleLeft && birdLeft <= obstacleRight) {
                val gapTop = obstacle.gapY - obstacle.gapSize / 2
                val gapBottom = obstacle.gapY + obstacle.gapSize / 2

                if (birdTop < gapTop || birdBottom > gapBottom) {
                    Log.e("N7", "COLLISION! Bird: Y=$birdY, Top=$birdTop, Bottom=$birdBottom, Size=$birdSize | Gap: Y=${obstacle.gapY}, Top=$gapTop, Bottom=$gapBottom, Size=${obstacle.gapSize}")
                    return true
                }
            }

            if (!obstacle.passed && birdLeft > obstacleRight) {
                obstacle.passed = true
                score++
                Log.d("N7", "Score increased to $score")
            }
        }

        if (birdY < 0 || birdY > 25) {
            Log.e("N7", "COLLISION! Bird out of bounds: Y=$birdY")
            return true
        }

        return false
    }

    fun getObstacles(): List<Obstacle> = obstacles

    fun getScore(): Int = score

    fun reset() {
        obstacles.clear()
        score = 0
        spawnTimer = 0f
    }

    private fun spawnObstacle() {
        val currentGapSize = calculateCurrentGapSize()
        val centerY = 12
        val halfGap = currentGapSize / 2
        val birdSize = GameConfig.BIRD_SIZE
        val halfBirdSize = birdSize / 2

        val minAllowedGapY = halfGap + 1
        val maxAllowedGapY = 25 - halfGap - 1

        val gapY = if (bestScore > 100) {
            Random.nextInt(minAllowedGapY, maxAllowedGapY + 1)
        } else {
            val birdTop = centerY - halfBirdSize
            val birdBottom = centerY + halfBirdSize

            val minGapY = (birdBottom - halfGap).coerceAtLeast(minAllowedGapY)
            val maxGapY = (birdTop + halfGap).coerceAtMost(maxAllowedGapY)

            if (minGapY <= maxGapY) {
                Random.nextInt(minGapY, maxGapY + 1)
            } else {
                centerY
            }
        }

        val obstacle = Obstacle(x = 25f, gapY = gapY, gapSize = currentGapSize)
        obstacles.add(obstacle)

        val gapTopEdge = gapY - halfGap
        val gapBottomEdge = gapY + halfGap
        Log.d("N7", "Spawned: gapY=$gapY, gapSize=$currentGapSize, gapRange=[$gapTopEdge, $gapBottomEdge], birdRange=[${centerY - halfBirdSize}, ${centerY + halfBirdSize}], score=$score")
    }

    private fun calculateCurrentGapSize(): Int {
        val reductions = score / 20
        val minGapSize = GameConfig.BIRD_SIZE + 2
        val reducedGapSize = gapSize - reductions
        return reducedGapSize.coerceAtLeast(minGapSize)
    }
}
