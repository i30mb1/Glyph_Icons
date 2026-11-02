package n7.glyphicons

import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import n7.glyphicons.game.GameConfig
import n7.glyphicons.game.GameLoop
import n7.glyphicons.game.GameRenderer
import n7.glyphicons.game.GameState
import n7.glyphicons.game.GyroSensorController
import n7.glyphicons.game.ObstacleManager
import kotlin.math.abs

class GlyphController(
    private val context: Context,
) : GlyphMatrixManager.Callback {

    private val glyphManager = GlyphMatrixManager.getInstance(context)
    private val configStorage = n7.glyphicons.game.GameConfigStorage(context)
    private val gameState = GameState()
    private val gameRenderer = GameRenderer()
    private val obstacleManager = ObstacleManager(configStorage.getBestScore())

    private lateinit var gyroController: GyroSensorController
    private lateinit var gameLoop: GameLoop
    private lateinit var scope: CoroutineScope

    fun init() {
        glyphManager.init(this)

        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        gyroController = GyroSensorController(context)

        gyroController.sensorFlow
            .map { sensor -> -sensor.x }
            .scan(0f) { prevSmoothed, current ->
                prevSmoothed * (1 - GameConfig.SMOOTHING_FACTOR) + current * GameConfig.SMOOTHING_FACTOR
            }
            .filter { abs(it) >= GameConfig.DEAD_ZONE_THRESHOLD }
            .onEach { delta ->
                Log.d("N7", "Gyro delta: $delta, birdY before: ${gameState.birdY}")
                gameState.updateBirdPosition(delta)
                Log.d("N7", "birdY after: ${gameState.birdY}")
            }
            .launchIn(scope)

        gameLoop = GameLoop { deltaTime ->
            updateGame(deltaTime)
        }
    }

    fun clear() {
        gameLoop.stop()
        scope.cancel()
        glyphManager.turnOff()
        glyphManager.closeAppMatrix()
        glyphManager.unInit()
    }

    private fun updateGame(deltaTime: Float) {
        if (!gameState.isGameOver) {
            obstacleManager.update(deltaTime, gameState.currentSpeedMultiplier)

            gameState.score = obstacleManager.getScore()

            if (obstacleManager.checkCollision(gameState.birdY, gameState.birdSize)) {
                gameState.isGameOver = true
                configStorage.saveBestScore(gameState.score)
                Log.d("N7", "Game Over! Score: ${obstacleManager.getScore()}, Best: ${configStorage.getBestScore()}")
            }
        }

        val frame = gameRenderer.render(context, gameState, obstacleManager.getObstacles())
        glyphManager.setMatrixFrame(frame)
    }

    override fun onServiceConnected(p0: ComponentName?) {
        // SDK 2.0 supports several devices; Flappy Box needs the 25x25 matrix of Phone (3).
        glyphManager.register(Glyph.DEVICE_23112)
        glyphManager.setGlyphMatrixTimeout(false)
        gameLoop.start()
        Log.d("N7", "onServiceConnected $p0")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.d("N7", "onServiceDisconnected $p0")
    }

    fun onLongPress() {
        if (gameState.isGameOver) {
            gameState.reset()
            obstacleManager.reset()
            Log.d("N7", "Game Reset")
        } else {
            gameState.increaseBoost()
            Log.d("N7", "Boost increased to level ${gameState.boostLevel}, multiplier: ${gameState.currentSpeedMultiplier}")
        }
    }
}
