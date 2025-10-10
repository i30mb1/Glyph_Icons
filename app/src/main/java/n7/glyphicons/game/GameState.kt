package n7.glyphicons.game

internal class GameState {
    var birdY: Float = 12f
    var isGameOver: Boolean = false
    var score: Int = 0
    var birdSize: Int = GameConfig.BIRD_SIZE

    private val minY = 2f
    private val maxY = 22f

    fun updateBirdPosition(deltaY: Float) {
        if (!isGameOver) {
            birdY = (birdY + deltaY).coerceIn(minY, maxY)
        }
    }

    fun increaseBirdSize() {
        birdSize++
    }

    fun decreaseBirdSize() {
        if (birdSize > 1) {
            birdSize--
        }
    }

    fun reset() {
        birdY = 12f
        isGameOver = false
        score = 0
        birdSize = GameConfig.BIRD_SIZE
    }
}
