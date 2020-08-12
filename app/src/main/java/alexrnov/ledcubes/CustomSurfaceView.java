package alexrnov.ledcubes;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CustomSurfaceView extends GLSurfaceView {

  GLSurfaceView.Renderer renderer;

  public CustomSurfaceView(Context context) {
    super(context);
  }

  public CustomSurfaceView(Context context, AttributeSet attributes) {
    super(context, attributes);
  }

  public void init(int versionGLES) {
    setPreserveEGLContextOnPause(true); // save context OpenGL
    // Tell the OGLView container that we want to create an OpenGL ES 2.0/3.0
    // compatible context and install an OpenGL ES 2.0/3.0 compatible render
    setEGLContextClientVersion(versionGLES);
    renderer = new SceneRenderer(versionGLES);
    setRenderer(renderer);
    //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
  }

}
