package alexrnov.ledcubes;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import static alexrnov.ledcubes.Buffers.newFloatBuffer;

public class Cube {
  private final int programObject;

  private final int mvpMatrixLink; // link of uniform for mvpMatrix
  private final int colorLink; // link of uniform for color
  private final int positionLink; // link of vertex attribute

  private final float size = 1.0f; // init size of cube

  private float[] mvpMatrix = new float[16];
  private float[] modelMatrix = new float[16];

  private float x, y, z;
  private float[] color;

  private float[] vertices = new float[] {
          -size, size, size, // top-left
          -size, -size, size, // bottom-left
          size, -size, size, // bottom-right
          size, -size, size, // bottom-right
          size, size, size, // top-right
          -size, size, size, // top-left
          -size, size, -size, // top-left
          -size, -size, -size, // bottom-left
          size, -size, -size, // bottom-right
          size, -size, -size, // bottom-right
          size, size, -size, // top-right
          -size, size, -size, // top-left
          -size, size, -size, // top-left
          -size, -size, -size, // bottom-left
          -size, -size, size, // bottom-right
          -size, -size, size, // bottom-right
          -size, size, size, // top-right
          -size, size, -size, // top-left
          size, size, -size, // top-left
          size, -size, -size, // bottom-left
          size, -size, size, // bottom-right
          size, -size, size, // bottom-right
          size, size, size, // top-right
          size, size, -size, // top-left
          -size, size, -size, // top-left
          -size, size, size, // bottom-left
          size, size, size, // bottom-right
          size, size, size, // bottom-right
          size, size, -size, // top-right
          -size, size, -size, // top-left
          -size, -size, -size, // top-left
          -size, -size, size, // bottom-left
          size, -size, size, // bottom-right
          size, -size, size, // bottom-right
          size, -size, -size, // top-right
          -size, -size, -size// top-left
  };


  private FloatBuffer bufferVertices;

  public Cube(float scale) {
    for (int i = 0; i < vertices.length; i++) vertices[i] = vertices[i] * scale;
    bufferVertices = newFloatBuffer(vertices);
    bufferVertices.position(0);

    String vShader =
            "#version 300 es                                      \n" +
            "uniform mat4 mvp_matrix;                      \n" +
            "in vec4 a_position;                                   \n" +
            "void main()                                               \n" +
            "{                                                              \n" +
              "gl_Position = mvp_matrix * a_position;  \n" +
            "}                                                              \n";

    String fShader =
            "#version 300 es                                      \n" +
            "precision mediump float;                          \n" +
            "uniform vec4 v_color;                              \n" +
            "out vec4 fragColor;                                  \n" +
            "void main()                                               \n" +
            "{                                                              \n" +
              "fragColor = v_color;                               \n" +
            "}                                                               \n";

    LinkedProgram linkedProgram = new LinkedProgram(vShader, fShader);
    programObject = linkedProgram.get();

    mvpMatrixLink = GLES30.glGetUniformLocation(this.programObject, "mvp_matrix");
    colorLink = GLES30.glGetUniformLocation(this.programObject, "v_color");
    positionLink = GLES20.glGetAttribLocation(programObject, "a_position");
    //normalLink = GLES20.glGetAttribLocation(programObject, "a_normal;");
  }

  public void defineView(float[] viewMatrix, float[] projectionMatrix) {
    //сбросить матрицу на единичную
    Matrix.setIdentityM(modelMatrix, 0);
    //переместить куб вверх/вниз и влево/вправо
    Matrix.translateM(modelMatrix, 0, x, y, z);
    //комбинировать видовую и модельные матрицы
    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    //комбинировать модельно-видовую матрицу и проектирующую матрицу
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
  }

  public void setColor(float[] color) {
    this.color = color;
  }

  public void draw() {
    GLES30.glUseProgram(this.programObject);
    GLES30.glUniformMatrix4fv(mvpMatrixLink, 1, false, mvpMatrix, 0);
    GLES30.glUniform4fv(colorLink, 1, color, 0);

    GLES30.glEnableVertexAttribArray(positionLink);//разрешить атрибут вершин
    GLES30.glVertexAttribPointer(positionLink, 3, GLES30.GL_FLOAT, false, 0, bufferVertices);

    GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
    // GLES30.glDisableVertexAttribArray(positionLink); // отключить атрибут вершин куба
  }

  public void setPosition(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
