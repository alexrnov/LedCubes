package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static alexrnov.ledcubes.BasicColor.transparent;

public class SceneRenderer implements GLSurfaceView.Renderer {

  private int versionGL; // support version of OpenGL ES

  // coefficients for camera rotation
  private float kx = 0f;
  private float ky = 0f;

  /* coordinates of camera */
  private float xCamera = 0.0f;
  private float yCamera = 0.0f;
  private float zCamera = 2.6f;

  /* coordinates of camera for thread-safe distance calculations */
  private float xCamera2 = 0.0f;
  private float yCamera2 = 0.0f;
  private float zCamera2 = 2.6f;

  private final int NUMBER_CUBES = 512;
  private Cube[] cubes = new Cube[NUMBER_CUBES];

  private float[] viewMatrix = new float[16];
  private float[] projectionMatrix = new float[16];

  //private long pastTime = System.currentTimeMillis();
  //private long spentTime = System.currentTimeMillis();

  private boolean initCubes = false; // this flag check if all objects was init
  private float[] defaultColor = transparent(BasicColor.gray(), 0.5f);

  private final int[] VBO = new int[1];

  private volatile boolean changeView = false; // this flag check if camera was moved

  private List<Cube> transparentObjects = new ArrayList<>();

  // sort cubes by distance to camera for correct alpha blending
  private Comparator<Cube> comparatorByZ = (objectA, objectB) -> {
    Double cameraDistance1 = Math.sqrt(Math.pow(xCamera2 - objectA.getX(), 2.0)
            + Math.pow(yCamera2 - objectA.getY(), 2.0)
            + Math.pow(zCamera2 - objectA.getZ(), 2.0));
    Double cameraDistance2 = Math.sqrt(Math.pow(xCamera2 - objectB.getX(), 2.0)
            + Math.pow(yCamera2 - objectB.getY(), 2.0)
            + Math.pow(zCamera2 - objectB.getZ(), 2.0));
    return cameraDistance2.compareTo(cameraDistance1);
  };

  public SceneRenderer(int versionGL) {
    this.versionGL = versionGL;
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    float size = 0.024f; // size of cube
    float[] vertices = new float[] {
            -size, size, size, -size, -size, size, size, -size, size, size, -size, size,
            size, size, size, -size, size, size, -size, size, -size, -size, -size, -size,
            size, -size, -size, size, -size, -size, size, size, -size, -size, size, -size,
            -size, size, -size, -size, -size, -size, -size, -size, size, -size, -size, size,
            -size, size, size, -size, size, -size, size, size, -size, size, -size, -size,
            size, -size, size, size, -size, size, size, size, size, size, size, -size,
            -size, size, -size, -size, size, size, size, size, size, size, size, size,
            size, size, -size, -size, size, -size, -size, -size, -size, -size, -size, size,
            size, -size, size, size, -size, size, size, -size, -size, -size, -size, -size };

    FloatBuffer bufferVertices = ByteBuffer.allocateDirect(vertices.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    bufferVertices.put(vertices).position(0);

    String vShader, fShader;
    if (versionGL == 2) { // version is OpenGL ES 2.0
      vShader =
              "#version 100                                            \n" +
                      "uniform mat4 mvp_matrix;                       \n" +
                      "attribute vec4 a_position;                        \n" +
                      "void main()                                               \n" +
                      "{                                                              \n" +
                      "gl_Position = mvp_matrix * a_position;  \n" +
                      "}                                                              \n";

      fShader =
              "#version 100                                           \n" +
                      "precision lowp float;                                 \n" +
                      "uniform vec4 v_color;                              \n" +
                      "void main()                                              \n" +
                      "{                                                              \n" +
                      "gl_FragColor = v_color;                         \n" +
                      "}                                                              \n";

    } else { // version OpenGL ES 3.0 or higher
      vShader =
              "#version 300 es                                      \n" +
                      "uniform mat4 mvp_matrix;                      \n" +
                      "in vec4 a_position;                                   \n" +
                      "void main()                                               \n" +
                      "{                                                              \n" +
                      "gl_Position = mvp_matrix * a_position;  \n" +
                      "}                                                              \n";

      fShader =
              "#version 300 es                                      \n" +
                      "precision lowp float;                                \n" +
                      "uniform vec4 v_color;                              \n" +
                      "out vec4 fragColor;                                  \n" +
                      "void main()                                               \n" +
                      "{                                                              \n" +
                      "fragColor = v_color;                               \n" +
                      "}                                                               \n";
    }

    LinkedProgram linkedProgram = new LinkedProgram(vShader, fShader);
    int programObject = linkedProgram.get();

    Matrix.setLookAtM(viewMatrix, 0, xCamera, yCamera, zCamera,
            0.0f, 0.0f, 0f, 0f, 1.0f, 0.0f);

    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    // implementation prioritizes performance
    GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_FASTEST);

    VBO[0] = 0;
    GLES20.glGenBuffers(1, VBO, 0);
    bufferVertices.position(0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);
    // 12 is the float_size (4) * component of vertex (3), 36 is the number of vertex
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 12 * 36,
            bufferVertices, GLES20.GL_STATIC_DRAW);

    int i = 0; // id of cube
    for (int kz = -4; kz < 4; kz++) {
      float z = - kz * 0.10f - 0.04f; // subtract the 0.04 value to center the scene
      for (int ky = -4; ky < 4; ky++) {
        float y = ky * 0.10f - 0.04f;
        for (int kx = 4; kx > -4; kx--) { // start position of lowest right angle
          float x = kx * 0.10f - 0.04f;
          cubes[i] = new Cube(programObject);
          cubes[i].setPosition(x, y, z);
          cubes[i].setColor(defaultColor);
          i++;
        }
      }
    }

    transparentObjects.addAll(Arrays.asList(cubes).subList(0, NUMBER_CUBES));

    initCubes = true; // init finish
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

    /* set default view for scene */
    for (Cube cube: transparentObjects) cube.defineView(viewMatrix, projectionMatrix);

    // sort by length to camera for correct transparency
    Collections.sort(transparentObjects, comparatorByZ);
  }

  // called when the frame is redrawn
  @Override
  public void onDrawFrame(GL10 gl) {
    //spentTime = System.currentTimeMillis() - pastTime;
    //pastTime = System.currentTimeMillis();
    //Log.v("P", "spentTime (fps) = " + spentTime);
    // set color buffer
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST); // enable depth test

    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);

    /* when used blending */
    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    if (changeView) { // if the camera was moved then perform matrix calculation
      // apply immutable matrix to avoid flicker artifact
      final float[] immutableViewMatrix = Arrays.copyOf(viewMatrix, 16);
      for (Cube cube: transparentObjects) cube.defineView(immutableViewMatrix, projectionMatrix);
      changeView = false;

      // copy coordinates of camera that no happen thread error
      xCamera2 = this.xCamera;
      yCamera2 = this.yCamera;
      zCamera2 = this.zCamera;
      // sort by length to camera for correct transparency
      Collections.sort(transparentObjects, comparatorByZ);
    }

    for (int i = 0; i < transparentObjects.size(); i++) {
      transparentObjects.get(i).draw();
    }

    GLES20.glDisable(GLES20.GL_BLEND);

    /* when blending is not used */
    /*
    for (int i = 0; i < NUMBER_CUBES; i++) {
      // invoke every frame to avoid flickering when rotating
      //cubes[i].defineView(immutableViewMatrix, projectionMatrix);
      cubes[i].draw();
    }
    */
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
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

    changeView = true; // in next frame perform calculation matrix
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
  public synchronized void setColor(int i, float[] color) {
    cubes[i].setColor(color);
  }
}
