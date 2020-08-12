package alexrnov.ledcubes;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import static alexrnov.ledcubes.Buffers.newFloatBuffer;

public class Cube {
  //ссылка на переменную вершинного шейдера, содержащую итоговую
  //MVP-матрицу uniform mat4 uMVPMatrix;
  private final int mvpMatrixLink;

  private final int programObject;

  private float x;

  private float y;

  private float z;

  private float[] color;

  private float[] mvpMatrix = new float[16];

  private float[] rotationMatrix = new float[16];

  //инициируемый размер куба
  private float size = 1.0f;

  //данные вершин для граней куба
  private float[] verticesTemplate = new float[] {
          // FRONT
          // Triangle 1
          -size, size, size, // top-left
          -size, -size, size, // bottom-left
          size, -size, size, // bottom-right
          // Triangle 2
          size, -size, size, // bottom-right
          size, size, size, // top-right
          -size, size, size, // top-left

          // BACK
          // Triangle 1
          -size, size, -size, // top-left
          -size, -size, -size, // bottom-left
          size, -size, -size, // bottom-right
          // Triangle 2
          size, -size, -size, // bottom-right
          size, size, -size, // top-right
          -size, size, -size, // top-left

          // LEFT
          // Triangle 1
          -size, size, -size, // top-left
          -size, -size, -size, // bottom-left
          -size, -size, size, // bottom-right
          // Triangle 2
          -size, -size, size, // bottom-right
          -size, size, size, // top-right
          -size, size, -size, // top-left

          // RIGHT
          // Triangle 1
          size, size, -size, // top-left
          size, -size, -size, // bottom-left
          size, -size, size, // bottom-right
          // Triangle 2
          size, -size, size, // bottom-right
          size, size, size, // top-right
          size, size, -size, // top-left

          // TOP
          // Triangle 1
          -size, size, -size, // top-left
          -size, size, size, // bottom-left
          size, size, size, // bottom-right
          // Triangle 2
          size, size, size, // bottom-right
          size, size, -size, // top-right
          -size, size, -size, // top-left

          // BOTTOM
          // Triangle 1
          -size, -size, -size, // top-left
          -size, -size, size, // bottom-left
          size, -size, size, // bottom-right
          // Triangle 2
          size, -size, size, // bottom-right
          size, -size, -size, // top-right
          -size, -size, -size // top-left
  };

  private float[] verticesWithScale = new float[108 * 2];
  private float scale;

  FloatBuffer vertices;


  public Cube(float scale) {
    this.scale = scale;
    setSize(scale);


    String codeVertexShader =
            "#version 300 es   \n" +
                    "uniform mat4 uMVPMatrix;    \n" +
                    "layout(location = 0) in vec4 vPosition;    \n" +
                    "void main()    \n" +
                    "{   \n" +
                    "gl_Position = uMVPMatrix * vPosition;    \n" +
                    "}   \n";

    String codeFragmentShader =
            "#version 300 es       \n" +
                    "precision mediump float;       \n" +
                    "uniform vec4 vColor;      \n" +
                    "out vec4 fragColor;       \n" +
                    "void main()       \n" +
                    "{       \n" +
                    "fragColor = vColor;       \n" +
                    "}     \n";

    LinkedProgram linkedProgramGL = new LinkedProgram(
            codeVertexShader, codeFragmentShader);

    this.programObject = linkedProgramGL.get();

    //связать vPosition с атрибутом 0 в шейдере
    GLES30.glBindAttribLocation(this.programObject, 0, "vPosition");

    //Получить ссылку на переменную, содержащую итоговую MPV-матрицу.
    //Эта переменная находится в вершинном шейдере: uniform mat4 uMVPMatrix;
    mvpMatrixLink = GLES30.glGetUniformLocation(this.programObject,
            "uMVPMatrix");
  }

  public void defineView(float[] viewMatrix, float[] projectionMatrix) {
    //сбросить матрицу на единичную
    Matrix.setIdentityM(rotationMatrix, 0);
    //переместить куб вверх/вниз и влево/вправо
    Matrix.translateM(rotationMatrix, 0, x, y, z);
    //комбинировать видовую и модельные матрицы
    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, rotationMatrix, 0);
    //комбинировать модельно-видовую матрицу и проектирующую матрицу
    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

  }

  public void setColor(float[] color) {
    this.color = color;
  }

  public void draw() {
    GLES30.glUseProgram(this.programObject);//использовать объект-программу
    //итоговая MVP-матрица загружается в соответствующую uniform-переменную
    //вершинного шейдера: uniform mat4 uMVPMatrix
    GLES30.glUniformMatrix4fv(mvpMatrixLink, 1, false, mvpMatrix, 0);
    //ErrorGL.checkGlError("glUniformMatrix4fv");

    int VERTEX_POS_INDEX = 0;
    //Загрузить данные вершин, которые затем могут быть корректно
    //трансформирваны. Это нужно было бы сделать, даже если бы не было ни
    //какой трансформации.
    GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, 3, GLES30.GL_FLOAT,
            false, 0, vertices);
    GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);//разрешить атрибут вершин
    //Now we are ready to draw the cube finally.
    int startPos = 0;
    //изменить цвет через шесть вершин, т.е. через два треугольника, которые
    //представляют одну грань куба
    int verticesPerface = 6;
    //получить ссылку на переменную vColor во фрагментном шейдере
    int mColorHandle = GLES30.glGetUniformLocation(this.programObject, "vColor");
    //нарисовать переднюю грань
    //массив текущего цвета для грани помещается в соответствующую
    //переменную фрагментого шейдера: uniform vec4 vColor
    GLES30.glUniform4fv(mColorHandle, 1, color, 0);
    GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, 36);
  }

  public void setSize(float scale) {
    for (int i = 0; i < verticesTemplate.length; i++) {
      verticesWithScale[i] = verticesTemplate[i] * scale;
    }
    vertices = newFloatBuffer(verticesWithScale);
    vertices.position(0);
  }

  public void setPosition(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
