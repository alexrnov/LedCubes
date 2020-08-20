package alexrnov.ledcubes

import alexrnov.ledcubes.BasicColor.transparent
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
  private var surfaceView: SurfaceView? = null

  /*
   * Colors with shades for different faces. Lighting is not computed
   * in the shader for performance reasons.
   */
  private val cyan = transparent(BasicColor.cyan(), 0.5f)
  private val red = transparent(BasicColor.red(), 0.5f)
  private val blue = transparent(BasicColor.blue(), 0.5f)
  private val green = transparent(BasicColor.green(), 0.5f)
  private val yellow = transparent(BasicColor.yellow(), 0.5f)
  private val white = transparent(BasicColor.white(), 0.5f)
  private val magenta = transparent(BasicColor.magenta(), 0.5f)
  private val gray = transparent(BasicColor.gray(), 0.24f)

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
          var k = Random().nextInt(20)
          k = if (k == 0) {
            1
          } else {
            9
          }

          /* pass all array that not flickers */
          for (i in 0 until 512) {
            if (i % k == 0) {
              surfaceView?.sceneRenderer?.setColor(i, cyan) // change color the current cube
            } else {
              surfaceView?.sceneRenderer?.setColor(i, gray) // turn off leds
            }
          }

          /*
          val color:FloatArray = when (Random().nextInt(7)) {
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
          if (i == 512) i = 0
          */
          surfaceView?.requestRender() // refresh frame
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