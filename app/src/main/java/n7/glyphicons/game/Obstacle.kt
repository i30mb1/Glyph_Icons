package n7.glyphicons.game

internal data class Obstacle(
    var x: Float,
    val gapY: Int,
    val gapSize: Int,
    var passed: Boolean = false
)
