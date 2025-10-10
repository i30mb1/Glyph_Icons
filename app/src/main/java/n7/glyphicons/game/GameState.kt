package n7.glyphicons.game

internal class GameState {
    var birdY: Float = 12f
    var isGameOver: Boolean = false
    var score: Int = 0
    var boostLevel: Int = 0
    val birdSize: Int
        get() = GameConfig.BIRD_SIZE
    val currentSpeedMultiplier: Float
        get() {
            val baseMultiplier = 1.0f + boostLevel * GameConfig.BOOST_INCREMENT
            val speedReductions = score / 50
            val reducedMultiplier = baseMultiplier - speedReductions * GameConfig.BOOST_INCREMENT
            return reducedMultiplier.coerceAtLeast(1.0f)
        }

    private val minY = 2f
    private val maxY = 22f

    fun updateBirdPosition(deltaY: Float) {
        if (!isGameOver) {
            birdY = (birdY + deltaY).coerceIn(minY, maxY)
        }
    }

    fun increaseBoost() {
        if (boostLevel < GameConfig.MAX_BOOST_LEVEL) {
            boostLevel++
        }
    }

    fun reset() {
        birdY = 12f
        isGameOver = false
        score = 0
        boostLevel = 0
    }
}
