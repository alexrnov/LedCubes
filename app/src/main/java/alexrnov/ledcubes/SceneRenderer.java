package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import alexrnov.ledcubes.objects.Position;
import alexrnov.ledcubes.objects.Cube;

public class SceneRenderer implements GLSurfaceView.Renderer {

  private double versionGL;

  private final float[] blue = BasicColor.blue();
  private final float[] red = BasicColor.red();
  private final float[] yellow = BasicColor.yellow();
  private final float[] cyan = BasicColor.cyan();
  private final float[] green = BasicColor.green();

  private final int NUMBER_CUBES = 512;
  private Cube[] cubes = new Cube[NUMBER_CUBES];

  private Random r = new Random();
  public SceneRenderer(double versionGL) {
    this.versionGL = versionGL;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
    // implementation prioritizes performance
    GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);

    int i = 0;

    for (int kz = 0; kz < 8; kz++) {
      float z = kz * 0.20f;
      for (int ky = -4; ky < 4; ky++) {
        float y = ky * 0.10f;
        for (int kx = -2; kx < 6; kx++) {
          float x = kx * 0.10f;

          cubes[i] = new Cube(0.007f);

          int color = r.nextInt(5);

          cubes[i].setBehavior(new Position(x, y, z));

          if (color == 0) {
            cubes[i].setColor(blue);
          } else if (color == 1) {
            cubes[i].setColor(red);
          } else if (color == 2) {
            cubes[i].setColor(yellow);
          } else if (color == 3) {
            cubes[i].setColor(cyan);
          } else {
            cubes[i].setColor(green);
          }






          i++;
        }
      }
    }


    /*
    for (int i = 0; i < NUMBER_CUBES; i++) {
      cubes[i] = new Cube(0.005f);


      Random r = new Random();
      float xCube = r.nextFloat() * 0.4f;//смещение по оси абцисс
      float yCube = r.nextFloat() * 0.4f;//смещение по оси ординат
      float zCube = r.nextFloat() * 10f;

      cubes[i].setBehavior(new Position(xCube, yCube, zCube));
      if (i % 2 == 0) {
        cubes[i].setColor(blue);
      } else {
        cubes[i].setColor(red);
      }
    }
    */

  }

  // screen orientation change handler, also called when returning to the app.
  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    GLES20.glViewport(0, 0, width, height); // set screen size

    for (int i = 0; i < NUMBER_CUBES; i++) {
      cubes[i].defineSettingsForBehavior(width, height);
    }
  }

  // called when the frame is redrawn
  @Override
  public void onDrawFrame(GL10 gl) {
    // set color buffer
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    //GLES20.glEnable(GLES20.GL_CULL_FACE); // allow discard
    //GLES20.glCullFace(GLES20.GL_BACK); // discard the back face of primitives
    GLES20.glEnable(GLES20.GL_DEPTH_TEST); // enable depth test

    for (int i = 0; i < NUMBER_CUBES; i++) {
      cubes[i].move();
      cubes[i].draw();
    }
  }
}
