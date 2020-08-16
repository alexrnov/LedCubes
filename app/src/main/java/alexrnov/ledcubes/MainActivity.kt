package alexrnov.ledcubes

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
  private var surfaceView: SurfaceView? = null

  /*
   * Checking the OpenGL version on the device at runtime. The manifest declares
   * support for OpenGL 2, which by default means support for OpenGL 2 and OpenGL 1.
   * Since, in fact, this application supports OpenGL 2 and OpenGL 3, the supported
   * version is checked at runtime. If OpenGL 3 or higher is supported, the third
   * version is used, if OpenGL 2 is supported, then the second version is used.
   * If only OpenGL version 1 is supported, then SceneRenderer will not start.
   */
  private val supportOpenGLES: Int
    get() {
      val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      val info = am.deviceConfigurationInfo ?: return 1
      val version = java.lang.Double.parseDouble(info.glEsVersion)
      return when {
        version >= 3.0 -> 3 // or info.reqGlEsVersion >= 0x30000
        version >= 2.0 -> 2
        else -> 1
      }
    }

  private var timer: Timer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (supportOpenGLES != 1) {
      surfaceView = findViewById(R.id.oglView)
      surfaceView?.init(supportOpenGLES, applicationContext)
    }
  }

  override fun onResume() {
    super.onResume()
    var i = 0

    timer = Timer(true)
    timer?.schedule(object : TimerTask() {
      override fun run() {
        if (surfaceView?.sceneRenderer?.isLoad()!!) {
        val color = when (Random().nextInt(8)) {
          0 -> BasicColor.cyan()
          1 -> BasicColor.red()
          2 -> BasicColor.blue()
          3 -> BasicColor.green()
          4 -> BasicColor.blue()
          5 -> BasicColor.yellow()
          6 -> BasicColor.white()
          else -> BasicColor.magenta()
        }
        surfaceView?.sceneRenderer?.setColor(i, color)
        i += 1
        if (i == 512) this.cancel()
      }
      }
    }, 0, 30)

    surfaceView?.onResume()
  }

  override fun onPause() {
    super.onPause()
    timer?.cancel()
    surfaceView?.onPause()
  }
}