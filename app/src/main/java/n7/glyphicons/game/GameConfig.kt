package n7.glyphicons.game

internal object GameConfig {
    const val SMOOTHING_FACTOR = 0.3f
    const val DEAD_ZONE_THRESHOLD = 0.01f

    var OBSTACLE_GAP_SIZE = 10
    var OBSTACLE_SPAWN_INTERVAL = 2f
    var OBSTACLE_SPEED = 7f

    var BIRD_SIZE = 4
    const val BIRD_BRIGHTNESS = 1024 // 0 - 2024
    const val OBSTACLE_BRIGHTNESS = 524 // 0 - 2024
    const val SCORE_BRIGHTNESS = 40 // 0 - 255
}
