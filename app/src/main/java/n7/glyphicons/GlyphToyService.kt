package n7.glyphicons

import android.app.Service
import android.content.Intent
import android.os.IBinder

class GlyphToyService : Service() {

    private val glyphController by lazy { GlyphController(this) }

    override fun onBind(intent: Intent?): IBinder? {
        glyphController.init()
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }
}
