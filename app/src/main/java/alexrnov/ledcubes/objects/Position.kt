package alexrnov.ledcubes.objects

import android.opengl.Matrix
import java.util.*

class Position(val x: Float, val y: Float, val z: Float) {

  private val mvpMatrix = FloatArray(16)
  private val viewMatrix = FloatArray(16)
  private var widthScreen = 0
  private var heightScreen = 0
  private var aspect = 0.toFloat()

  private val rotationMatrix = FloatArray(16)
  private val projectionMatrix = FloatArray(16)

  /**
   * Определяются настройки проекционной матрицы. Вычисляется
   * аспект и создается экземпляр для получения экранных координат
   * объекта (в пикселах). Данный метод должен вызываться в классе
   * рендеринга OpenGL в методе onSurfaceChanged()
   */
  fun defineSettingsOfView(widthScreen: Int, heightScreen: Int) {
    defineCommonSettings(widthScreen, heightScreen)
    setPerspectiveProjection()
  }

  /* Установить проекцию с перспективой. Параметр near установлен в
   * значении 0.2f вместо 0.1f, для того, чтобы не было искажений по
   * углам экрана. Эти искажения связаны с сильной перспективой. Когда
   * же параметр установлен в значении 0.2f, объект приближается в два
   * раза, и перспектива также уменьшается в два раза*/
  private fun setPerspectiveProjection() {
    val k: Float = 1f / 30 //коэффициент подобран эмпирически
    if (widthScreen < heightScreen) {
      Matrix.frustumM(projectionMatrix, 0, -1f * k, 1f * k,
              (1/-aspect) * k, (1/aspect) * k, 0.2f, 40f)
    } else {
      Matrix.frustumM(projectionMatrix, 0, -aspect * k,
              aspect * k, -1f * k, 1f * k, 0.2f, 40f)
    }
  }

  fun run2(viewMatrix2: FloatArray) { //Создать вращение и перемещение куба
    //сбросить матрицу на единичную
    Matrix.setIdentityM(rotationMatrix, 0)
    //переместить куб вверх/вниз и влево/вправо
    Matrix.translateM(rotationMatrix, 0, x, y, z)
    //комбинировать видовую и модельные матрицы
    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix2, 0, rotationMatrix, 0)
    //комбинировать модельно-видовую матрицу и проектирующую матрицу
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
  }

  /** Определяет настройки, общие для всех объектов сцены */
  private fun defineCommonSettings(widthScreen: Int, heightScreen: Int) {
    this.widthScreen = widthScreen
    this.heightScreen = heightScreen
    //установить позицию камеры(матрица вида)
    Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f)
    aspect = widthScreen.toFloat() / heightScreen.toFloat()
  }

  /**
   * @return итоговая модельно-видо-проекционная-матрица, характеризующая
   * изменение положения трехмерного объекта в пространстве
   */
  fun getMVP(): FloatArray {
    return mvpMatrix
  }
}