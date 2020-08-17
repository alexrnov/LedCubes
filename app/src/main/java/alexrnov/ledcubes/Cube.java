package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * A class for creating a separate cube that can change color. No indices or
 * normals are used for the cube. It also does not use shader lighting
 * computation and buffer objects. This is due to the goal of improving
 * performance, since there can be many cubes. Additional data and objects
 * can increase memory consumption and object initialization time.
 */
public class Cube {
  private final int programObject;

  private final int mvpMatrixLink; // link of uniform for mvpMatrix
  private final int colorLink; // link of uniform for color
  private final int positionLink; // link of vertex attribute

  private float[] mvpMatrix = new float[16];
  private float[] modelMatrix = new float[16];

  private float[][] color; // color of cube

  private final int[] VBO = new int[1];

  /**
   * Create a cube of a specific size
   * @param size - size of cube
   * @param versionGL - support version of OpenGL ES
   */
  public Cube(float size, int versionGL) {

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
    programObject = linkedProgram.get();

    mvpMatrixLink = GLES20.glGetUniformLocation(this.programObject, "mvp_matrix");
    colorLink = GLES20.glGetUniformLocation(this.programObject, "v_color");
    positionLink = GLES20.glGetAttribLocation(programObject, "a_position");

    /* create vertex buffer object */
    VBO[0] = 0;
    GLES20.glGenBuffers(1, VBO, 0);
    bufferVertices.position(0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);
    // 12 is the float_size (4) * component of vertex (3), 36 is the number of vertex
    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 12 * 36,
            bufferVertices, GLES20.GL_STATIC_DRAW);
  }

  /**
   * Define view of cube.
   * @param viewMatrix - matrix of view (change when changes when the cube rotates)
   * @param projectionMatrix - projection matrix (change when the screen rotates)
   */
  public void defineView(float[] viewMatrix, float[] projectionMatrix) {
    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
  }

  /**
   * Draw object. Methods such as: setPosition(), setColor(), setView()
   * must be called first.
   */
  public void draw() {
    GLES20.glUseProgram(this.programObject);
    GLES20.glUniformMatrix4fv(mvpMatrixLink, 1, false, mvpMatrix, 0);
    GLES20.glEnableVertexAttribArray(positionLink);// allow cube vertices attribute

    // in case without VBO
    //GLES20.glVertexAttribPointer(positionLink, 3, GLES20.GL_FLOAT, false, 0, bufferVertices);

    // in case with VBO
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VBO[0]);
    GLES20.glVertexAttribPointer(positionLink, 3, GLES20.GL_FLOAT,
            false, 12, 0);
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    /* different pseudo-shades only for the front faces */
    // front face
    GLES20.glUniform4fv(colorLink, 1, color[0], 0); // pass color of face to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6); // draw current face
    // front-side face
    GLES20.glUniform4fv(colorLink, 1, color[3], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 18, 6);
    // top face
    GLES20.glUniform4fv(colorLink, 1, color[4], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 24, 6);

    // one color for back faces
    GLES20.glUniform4fv(colorLink, 1, color[2], 0);
    // back face and back-side face
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 12);
    // down face
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 30, 6);

    /* different pseudo-shades for the all faces */
    /*
    // front face
    GLES20.glUniform4fv(colorLink, 1, color[0], 0); // pass color of face to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6); // draw current face

    // back face
    GLES20.glUniform4fv(colorLink, 1, color[1], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 6);
    // back-side face
    GLES20.glUniform4fv(colorLink, 1, color[2], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 12, 6);
    // front-side face
    GLES20.glUniform4fv(colorLink, 1, color[3], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 18, 6);
    // top face
    GLES20.glUniform4fv(colorLink, 1, color[4], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 24, 6);
    // down face
    GLES20.glUniform4fv(colorLink, 1, color[5], 0);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 30, 6);
    */
    GLES20.glDisableVertexAttribArray(positionLink); // disable cube vertices attribute
  }

  /**
   * Set color for cube.
   * @param color - current color
   */
  public void setColor(float[][] color) {
    this.color = color;
  }

  /**
   * Define start position for cube.
   * @param x - coordinate x
   * @param y - coordinate y
   * @param z - coordinate z
   */
  public void setPosition(float x, float y, float z) {
    Matrix.setIdentityM(modelMatrix, 0); // reset matrix to one
    Matrix.translateM(modelMatrix, 0, x, y, z); // move cube
  }
}
