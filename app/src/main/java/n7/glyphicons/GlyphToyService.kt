package n7.glyphicons

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Messenger
import android.util.Log
import com.nothing.ketchum.GlyphToy

class GlyphToyService : Service() {

    private val glyphController by lazy { GlyphController(this) }
    private val serviceHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            GlyphToy.MSG_GLYPH_TOY -> {
                val event = it.data.getString(GlyphToy.MSG_GLYPH_TOY_DATA)
                when (event) {
                    GlyphToy.EVENT_CHANGE -> glyphController.onLongPress()
                    GlyphToy.EVENT_AOD -> Log.d("N7", "EVENT_AOD")
                }
                true
            }

            else -> false
        }
    }
    private val serviceMessenger = Messenger(serviceHandler)

    override fun onBind(intent: Intent?): IBinder {
        Log.d("N7", "onBind")
        glyphController.init()
        return serviceMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("N7", "onUnbind")
        glyphController.clear()
        return false
    }
}
