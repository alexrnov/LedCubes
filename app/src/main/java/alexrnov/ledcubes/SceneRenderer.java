package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static alexrnov.ledcubes.BasicColor.shades;

public class SceneRenderer implements GLSurfaceView.Renderer {

  private int versionGL;

  // coefficients for camera rotation
  private float kx = 0f;
  private float ky = 0f;

  // coordinates of camera
  private float xCamera = 0.0f;
  private float yCamera = 0.0f;
  private float zCamera = 2.6f;

  private final int NUMBER_CUBES = 512;
  private Cube[] cubes = new Cube[NUMBER_CUBES];

  private float[] viewMatrix = new float[16];
  private float[] projectionMatrix = new float[16];

  private long pastTime = System.currentTimeMillis();
  private long spentTime = System.currentTimeMillis();

  private boolean initCubes = false;
  private float[][] defaultColor = shades(BasicColor.gray());

  public SceneRenderer(int versionGL) {
    this.versionGL = versionGL;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    Matrix.setLookAtM(viewMatrix, 0, xCamera, yCamera, zCamera,
            0.0f, 0.0f, 0f, 0f, 1.0f, 0.0f);

    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    // implementation prioritizes performance
    GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);

    int i = 0; // id of cube
    for (int kz = -4; kz < 4; kz++) {
      float z = - kz * 0.10f - 0.04f; // subtract the 0.04 value to center the scene
      for (int ky = -4; ky < 4; ky++) {
        float y = ky * 0.10f - 0.04f;
        for (int kx = 4; kx > -4; kx--) { // start position of lowest right angle
          float x = kx * 0.10f - 0.04f;
          cubes[i] = new Cube(0.024f, versionGL);
          cubes[i].setPosition(x, y, z);
          cubes[i].setColor(defaultColor);
          i++;
        }
      }
    }
    initCubes = true;
    // set default camera angle
    this.setMotion(900.0f, -350.0f);
  }

  // screen orientation change handler, also called when returning to the app
  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    GLES20.glViewport(0, 0, width, height); // set screen size

    float aspect = (float) width / (float) height;
    float k = 1f / 30; // coefficient is selected empirically

    if (width < height) { // portrait orientation
      Matrix.frustumM(projectionMatrix, 0, -1f * k, 1f * k,
              (1/-aspect) * k, (1/aspect) * k, 0.1f, 40f);
    } else { // landscape orientation
      Matrix.frustumM(projectionMatrix, 0, -aspect * k,
              aspect * k, -1f * k, 1f * k, 0.1f, 40f);
    }
  }

  // called when the frame is redrawn
  @Override
  public void onDrawFrame(GL10 gl) {
    spentTime = System.currentTimeMillis() - pastTime;
    pastTime = System.currentTimeMillis();
    Log.v("P", "spentTime (fps) = " + spentTime);
    // set color buffer
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST); // enable depth test

    // apply immutable matrix to avoid flicker artifact
    final float[] immutableViewMatrix = Arrays.copyOf(viewMatrix, 16);
    for (int i = 0; i < NUMBER_CUBES; i++) {
      // invoke every frame to avoid flickering when rotating
      cubes[i].defineView(immutableViewMatrix, projectionMatrix);
      cubes[i].draw();
    }
  }

  /** Set camera in default place */
  public synchronized void defaultView() {
    xCamera = 0f;
    yCamera = 0f;
    zCamera = 2.6f;
    kx = 0f;
    ky = 0f;

    Matrix.setLookAtM(viewMatrix, 0, xCamera, yCamera, zCamera,
            0f, 0f, 0f, 0f, 1.0f, 0.0f);
    // set default camera angle
    this.setMotion(900.0f, -350.0f);
  }

  /**
   * Move camera based on offset by X and Y
   * @param xDistance - X-axis offset
   * @param yDistance - Y-axis offset
   */
  public synchronized void setMotion(float xDistance, float yDistance) {
    //Log.i("P", "xDistance = " + xDistance + ", yDistance =" + yDistance);
    kx = kx + xDistance * 0.001f;
    // limit rotation to z
    if ((!(ky < -0.5) || !(yDistance < 0.0)) && (!(ky > 0.5) || !(yDistance >= 0.0))) {
      ky = ky + yDistance * 0.001f;
    }

    final float radius = 2.6f; // radius of rotation of the camera around the object
    // define spherical coordinates for camera
    xCamera = (float) (radius * Math.cos(ky) * Math.sin(kx));
    yCamera = (float) (radius * Math.sin(ky));
    zCamera = (float) (radius * Math.cos(ky) * Math.cos(kx));

    // set position for camera
    Matrix.setLookAtM(viewMatrix, 0, xCamera, -yCamera, zCamera,
            0f, 0.0f, 0f, 0f, 1.0f, 0.0f);
  }

  /**
   * The method checks if all cubes are initialized.
   * @return <value>true</value> - if all cubes is init,
   * <value>else</value> - in another case
   */
  public synchronized boolean isLoad() {
    return initCubes;
  }

  /**
   * Set current color for cube.
   * @param i - id of cube
   * @param color - current color of cube
   */
  public synchronized void setColor(int i, float[][] color) {
    cubes[i].setColor(color);
  }
}
