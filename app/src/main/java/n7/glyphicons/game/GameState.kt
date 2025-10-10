package n7.glyphicons.game

internal class GameState {
    var birdY: Float = 12f

    private val minY = 2f
    private val maxY = 22f

    fun updateBirdPosition(deltaY: Float) {
        birdY = (birdY + deltaY).coerceIn(minY, maxY)
    }
}
