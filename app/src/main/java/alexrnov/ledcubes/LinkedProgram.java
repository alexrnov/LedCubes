package alexrnov.ledcubes;

import android.opengl.GLES20;

/** The class links an OpenGL program object. */
public class LinkedProgram {
  private int programObject;

  /**
   * @param codeVertexShader source code of vertex shader
   * @param codeFragmentShader source code of fragment shader
   */
  public LinkedProgram(String codeVertexShader, String codeFragmentShader) {
    programObject = linkProgramAndGetId(codeVertexShader, codeFragmentShader);
  }

  // link the program and returns its identifier
  private int linkProgramAndGetId(String v, String f) {
    int vertexShader;
    int fragmentShader;
    int programId;
    // load vertex shader
    vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, v);
    if (vertexShader == 0) {
      GLES20.glDeleteShader(vertexShader);
      return 0;
    }
    // load fragment shader
    fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, f);
    if (fragmentShader == 0) {
      GLES20.glDeleteShader(fragmentShader);
      return 0;
    }
    // an optional instruction that can be called after all shaders have
    // been compiled to reduce the overhead of the compiler
    GLES20.glReleaseShaderCompiler();
    // create program-object
    programId = GLES20.glCreateProgram();
    if (programId == 0) {
      return 0;
    }
    // attaching shaders to the program
    GLES20.glAttachShader(programId, vertexShader);
    GLES20.glAttachShader(programId, fragmentShader);
    GLES20.glLinkProgram(programId);
    int[] linked = new int[1];
    GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linked, 0);
    if (linked[0] == 0) {
      GLES20.glDeleteProgram(programId);
      return 0;
    }

    // free up resources that are no longer needed
    GLES20.glDeleteShader(vertexShader);
    GLES20.glDeleteShader(fragmentShader);
    return programId;
  }

  // compile shader
  private int loadShader(int type, String shaderSrc) {
    int shader;

    shader = GLES20.glCreateShader(type);
    if (shader == 0) {
      return 0;
    }

    GLES20.glShaderSource(shader, shaderSrc); // load code of shader
    GLES20.glCompileShader(shader);
    int[] compiled = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

    if (compiled[0] == 0) {
      GLES20.glDeleteShader(shader);
      return 0;
    }

    // check if OpenGL implementation supports compilation at runtime
    boolean[] runtime = new boolean[1];
    GLES20.glGetBooleanv(GLES20.GL_SHADER_COMPILER, runtime, 0);
    if (!runtime[0]) {
      return 0;
    }
    return shader;
  }

  /**
   * Returns the identifier of the OpenGL program object
   * @return - OpenGL program object identifier
   */
  public int get() {
    return programObject;
  }
}
