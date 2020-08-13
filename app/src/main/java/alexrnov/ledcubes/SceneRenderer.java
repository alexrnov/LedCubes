package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SceneRenderer implements GLSurfaceView.Renderer {

  private double versionGL;

  private float xoffset = 0.0f;
  private float zoffset = -3.0f;
  private final float[] blue = BasicColor.blue();
  private final float[] red = BasicColor.red();
  private final float[] yellow = BasicColor.yellow();
  private final float[] cyan = BasicColor.cyan();
  private final float[] green = BasicColor.green();
  private final float[] magenta = BasicColor.magenta();
  private final float[] gray = BasicColor.gray();

  private float angle = 0f;

  private final int NUMBER_CUBES = 512;
  private Cube[] cubes = new Cube[NUMBER_CUBES];

  private Random r = new Random();

  private float[] viewMatrix = new float[16];
  private float[] projectionMatrix = new float[16];

  public SceneRenderer(double versionGL) {
    this.versionGL = versionGL;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f,
            0f, 0f, 0f, 0f, 1.0f, 0.0f);

    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    // implementation prioritizes performance
    GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);

    int i = 0;

    for (int kz = -4; kz < 4; kz++) {
      float z = - kz * 0.10f;
      for (int ky = -4; ky < 4; ky++) {
        float y = ky * 0.10f;
        for (int kx = 4; kx > -4; kx--) { // start position of lowest right angle
          float x = kx * 0.10f;

          cubes[i] = new Cube(0.024f);

          int color = r.nextInt(7);

          cubes[i].setPosition(x, y, z);

          if (color == 0) {
            cubes[i].setColor(blue);
          } else if (color == 1) {
            cubes[i].setColor(red);
          } else if (color == 2) {
            cubes[i].setColor(yellow);
          } else if (color == 3) {
            cubes[i].setColor(cyan);
          } else if (color == 4) {
            cubes[i].setColor(magenta);
          } else if (color == 5) {
            cubes[i].setColor(gray);
          } else {
            cubes[i].setColor(green);
          }
          i++;
        }
      }
    }
  }

  // screen orientation change handler, also called when returning to the app.
  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    GLES20.glViewport(0, 0, width, height); // set screen size

    float aspect = (float) width / (float) height;

    float k = 1f / 30; // коэффициент подобран эмпирически
    if (width < height) {
      Matrix.frustumM(projectionMatrix, 0, -1f * k, 1f * k,
              (1/-aspect) * k, (1/aspect) * k, 0.1f, 40f);
    } else {
      Matrix.frustumM(projectionMatrix, 0, -aspect * k,
              aspect * k, -1f * k, 1f * k, 0.1f, 40f);
    }
  }

  // called when the frame is redrawn
  @Override
  public void onDrawFrame(GL10 gl) {

    xoffset = xoffset - 0.01f;
    Log.i("P", "xoffset = " + xoffset);

    angle = angle + 0.01f;
    if (angle >=360f) {
      angle = 0.0f;
    }

    float radius = 2.6f;

    float x = (float)( radius * Math.cos(angle));
    float y = (float)( radius * Math.sin(angle));

    Log.i("P", "x = " + x);
    Log.i("P", "y = " + y);

    Matrix.setLookAtM(viewMatrix, 0, x, 0, y, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

    //Matrix.translateM(viewMatrix, 0, -x, 0, 0);

    // set color buffer
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    //GLES20.glEnable(GLES20.GL_CULL_FACE); // allow discard
    //GLES20.glCullFace(GLES20.GL_BACK); // discard the back face of primitives
    GLES20.glEnable(GLES20.GL_DEPTH_TEST); // enable depth test

    for (int i = 0; i < NUMBER_CUBES; i++) {
      cubes[i].defineView(viewMatrix, projectionMatrix);
      cubes[i].draw();
    }
  }
}
