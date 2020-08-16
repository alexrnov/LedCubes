package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class Cube {
  private final int programObject;

  private final int mvpMatrixLink; // link of uniform for mvpMatrix
  private final int colorLink; // link of uniform for color
  private final int positionLink; // link of vertex attribute

  private float[] mvpMatrix = new float[16];
  private float[] modelMatrix = new float[16];

  private float[] color; // color of cube

  private FloatBuffer bufferVertices;

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

    bufferVertices = ByteBuffer.allocateDirect(vertices.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    bufferVertices.put(vertices).position(0);

    String vShader, fShader;
    if (versionGL == 2) {
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

    } else { // version opengl es 3.0 or higher
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
  }

  /**
   * Define view of cube.
   * @param viewMatrix
   * @param projectionMatrix
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
    GLES20.glUniform4fv(colorLink, 1, color, 0); // pass color to shader

    GLES20.glEnableVertexAttribArray(positionLink);// allow cube vertices attribute
    GLES20.glVertexAttribPointer(positionLink, 3, GLES20.GL_FLOAT, false, 0, bufferVertices);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    float[] color2 = Arrays.copyOf(color, 4);

    color2[0] = color[0] * 0.5f; // back - back
    color2[1] = color[1] * 0.5f;
    color2[2] = color[2] * 0.5f;
    GLES20.glUniform4fv(colorLink, 1, color2, 0); // pass color to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 6);

    color2[0] = color[0] * 0.7f; // back-side
    color2[1] = color[1] * 0.7f;
    color2[2] = color[2] * 0.7f;
    GLES20.glUniform4fv(colorLink, 1, color2, 0); // pass color to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 12, 6);

    color2[0] = color[0] * 0.8f; // front-side
    color2[1] = color[1] * 0.8f;
    color2[2] = color[2] * 0.8f;
    GLES20.glUniform4fv(colorLink, 1, color2, 0); // pass color to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 18, 6);

    color2[0] = color[0] * 0.9f; // top
    color2[1] = color[1] * 0.9f;
    color2[2] = color[2] * 0.9f;
    GLES20.glUniform4fv(colorLink, 1, color2, 0); // pass color to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 24, 6);

    color2[0] = color[0] * 0.6f; // down
    color2[1] = color[1] * 0.6f;
    color2[2] = color[2] * 0.6f;
    GLES20.glUniform4fv(colorLink, 1, color2, 0); // pass color to shader
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 30, 6);

    GLES20.glDisableVertexAttribArray(positionLink); // disable cube vertices attribute
  }

  /**
   * Set color for cube.
   * @param color - current color
   */
  public void setColor(float[] color) {
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
