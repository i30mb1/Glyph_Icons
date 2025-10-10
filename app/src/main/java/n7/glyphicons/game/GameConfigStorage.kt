package n7.glyphicons.game

import android.content.Context
import android.content.SharedPreferences

internal class GameConfigStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_config", Context.MODE_PRIVATE)

    fun loadConfig() {
        GameConfig.OBSTACLE_GAP_SIZE = prefs.getInt("gap_size", 10)
        GameConfig.OBSTACLE_SPAWN_INTERVAL = prefs.getFloat("spawn_interval", 2f)
        GameConfig.OBSTACLE_SPEED = prefs.getFloat("speed", 7f)
        GameConfig.BIRD_SIZE = prefs.getInt("bird_size", 4)
    }

    fun saveConfig() {
        prefs.edit().apply {
            putInt("gap_size", GameConfig.OBSTACLE_GAP_SIZE)
            putFloat("spawn_interval", GameConfig.OBSTACLE_SPAWN_INTERVAL)
            putFloat("speed", GameConfig.OBSTACLE_SPEED)
            putInt("bird_size", GameConfig.BIRD_SIZE)
            apply()
        }
    }

    fun getBestScore(): Int {
        return prefs.getInt("best_score", 0)
    }

    fun saveBestScore(score: Int) {
        val currentBest = getBestScore()
        if (score > currentBest) {
            prefs.edit().putInt("best_score", score).apply()
        }
    }
}
