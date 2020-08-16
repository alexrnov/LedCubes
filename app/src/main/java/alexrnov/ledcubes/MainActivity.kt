package alexrnov.ledcubes

import alexrnov.ledcubes.BasicColor.shades
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
  private var surfaceView: SurfaceView? = null

  /*
   * Colors with shades for different faces. Lighting is not computed
   * in the shader for performance reasons.
   */
  private val cyan: Array<FloatArray> = shades(BasicColor.cyan())
  private val red = shades(BasicColor.red())
  private val blue = shades(BasicColor.blue())
  private val green = shades(BasicColor.green())
  private val yellow = shades(BasicColor.yellow())
  private val white = shades(BasicColor.white())
  private val magenta = shades(BasicColor.magenta())

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
  private var i = 0 // id of current cube

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (supportOpenGLES != 1) {
      surfaceView = findViewById(R.id.oglView)
      surfaceView?.init(supportOpenGLES, applicationContext)
    }
  }

  /* Here you can control the cube */
  override fun onResume() {
    super.onResume()

    timer = Timer(true) // run thread as demon
    timer?.schedule(object : TimerTask() {
      override fun run() {
        // check initialization of all cubes
        if (surfaceView?.sceneRenderer?.isLoad!!) {
          val color:Array<FloatArray> = when (Random().nextInt(7)) {
            0 -> cyan
            1 -> red
            2 -> blue
            3 -> green
            4 -> white
            5 -> yellow
            else -> magenta
          }
          surfaceView?.sceneRenderer?.setColor(i, color) // change color the current cube
          i += 1
          if (i == 512) {
            i = 0
            this.cancel()
          }
        }
      }
    }, 0, 30) // change color every 30 ms

    surfaceView?.onResume()
  }

  override fun onPause() {
    super.onPause()
    timer?.cancel()
    surfaceView?.onPause()
  }
}