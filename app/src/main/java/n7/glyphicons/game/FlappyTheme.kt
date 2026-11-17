package n7.glyphicons.game

import android.content.Context
import kotlin.math.PI
import kotlin.math.sin
import n7.glyph.ui.support.Supporter

/**
 * How the obstacles and the box are drawn. [CLASSIC] is everybody's; the others are the supporters' extra. The game,
 * the gap and the collisions are the same in every theme: only the picture changes.
 */
internal enum class FlappyTheme(val supporter: Boolean) {
    /** Two-cell columns, a square box. */
    CLASSIC(false),

    /** Shaded pipes with a bright cap at the gap, a square box. */
    PIPES(true),

    /** Brick walls with dark mortar lines, a hollow box. */
    BRICKS(true),

    /** A pulsing beam with a faint glow beside it, a diamond box. */
    LASER(true),
    ;

    /**
     * Brightness of the obstacle cell in its column [dx] (-1 and 2 are the caps' overhang, 0 and 1 the obstacle itself)
     * on row [y], for a gap from [gapTop] to [gapBottom]; 0 for nothing. [timeMs] moves the laser's pulse.
     */
    fun obstacle(dx: Int, y: Int, gapTop: Int, gapBottom: Int, timeMs: Long): Int {
        val inGap = y in gapTop..gapBottom
        if (inGap) return 0
        val cap = y == gapTop - 1 || y == gapTop - 2 || y == gapBottom + 1 || y == gapBottom + 2
        return when (this) {
            CLASSIC -> if (dx in 0..1) GameConfig.OBSTACLE_BRIGHTNESS else 0
            PIPES -> when {
                cap -> 1600
                dx == 0 -> 800
                dx == 1 -> 320
                else -> 0
            }
            BRICKS -> when {
                dx !in 0..1 -> 0
                y % 3 == 2 -> 0
                // The bricks of every other course are shifted: the joint is in the other column.
                (y / 3) % 2 == dx && y % 3 == 0 -> 0
                else -> 900
            }
            LASER -> when (dx) {
                0 -> (1600 + 1200 * sin(2 * PI * timeMs / 600.0 + y * 0.5)).toInt().coerceIn(400, 4095)
                1 -> 180
                else -> 0
            }
        }
    }

    /** Whether cell ([x], [y]) of a [size] x [size] box is lit. */
    fun box(x: Int, y: Int, size: Int): Boolean {
        val last = size - 1
        return when (this) {
            CLASSIC, PIPES -> true
            BRICKS -> size < 4 || x == 0 || y == 0 || x == last || y == last
            LASER -> size < 3 || !((x == 0 || x == last) && (y == 0 || y == last))
        }
    }

    companion object {
        private const val PREFS = "flappy"
        private const val KEY_THEME = "theme"

        /** The theme to draw: the one picked in the app, or [CLASSIC] if it is a supporters' theme and the purchase is gone. */
        fun current(context: Context): FlappyTheme {
            val stored = stored(context)
            return if (stored.supporter && !Supporter.owned(context)) CLASSIC else stored
        }

        fun stored(context: Context): FlappyTheme {
            val name = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_THEME, null)
            return entries.firstOrNull { it.name == name } ?: CLASSIC
        }

        fun save(context: Context, theme: FlappyTheme) {
            context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_THEME, theme.name).apply()
        }
    }
}
