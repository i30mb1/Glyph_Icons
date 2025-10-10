package n7.glyphicons.game

import android.util.Log
import kotlin.random.Random

internal class ObstacleManager {
    private val obstacles = mutableListOf<Obstacle>()
    private var score = 0
    private var spawnTimer = 0f

    private val spawnInterval = GameConfig.OBSTACLE_SPAWN_INTERVAL
    private val obstacleSpeed = GameConfig.OBSTACLE_SPEED
    private val gapSize = GameConfig.OBSTACLE_GAP_SIZE
    private val obstacleWidth = 2

    fun update(deltaTime: Float) {
        spawnTimer += deltaTime

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnObstacle()
        }

        obstacles.forEach { obstacle ->
            obstacle.x -= obstacleSpeed * deltaTime
        }

        obstacles.removeAll { it.x.toInt() + obstacleWidth < 0 }

        Log.d("N7", "Obstacles count: ${obstacles.size}, timer: $spawnTimer")
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
        val gapY = Random.nextInt(gapSize / 2 + 2, 25 - gapSize / 2 - 2)
        val obstacle = Obstacle(x = 25f, gapY = gapY, gapSize = gapSize)
        obstacles.add(obstacle)
        Log.d("N7", "Spawned obstacle at x=25, gapY=$gapY")
    }
}
