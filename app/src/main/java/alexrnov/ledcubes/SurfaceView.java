package alexrnov.ledcubes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;

public class SurfaceView extends GLSurfaceView implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

  SceneRenderer renderer;
  private GestureDetectorCompat detector;

  public SurfaceView(Context context) {
    super(context);
  }

  public SurfaceView(Context context, AttributeSet attributes) {
    super(context, attributes);
  }

  public void init(int versionGLES, Context context) {
    setPreserveEGLContextOnPause(true); // save context OpenGL
    // Tell the OGLView container that we want to create an OpenGL ES 2.0/3.0
    // compatible context and install an OpenGL ES 2.0/3.0 compatible render
    setEGLContextClientVersion(versionGLES);
    renderer = new SceneRenderer(versionGLES);
    setRenderer(renderer);
    detector = new GestureDetectorCompat(context, this);
    detector.setOnDoubleTapListener(this); // set gesture detector as double tap listener
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    if (detector.onTouchEvent(e)) {
      return true;
    }
    return super.onTouchEvent(e);
  }

  @Override
  public boolean onScroll(MotionEvent event1, MotionEvent event2,
                          float distanceX, float distanceY) {
    renderer.setMotion(distanceX, distanceY); // camera rotation
    return true;
  }

  @Override
  public boolean onDoubleTapEvent(MotionEvent motionEvent) {
    renderer.defaultView(); // return the camera to default view
    return true;
  }

  @Override
  public boolean onDown(MotionEvent event) {
    return true;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
    return true;
  }

  @Override
  public boolean onDoubleTap(MotionEvent motionEvent) {
    return true;
  }

  @Override
  public void onShowPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onSingleTapUp(MotionEvent motionEvent) {
    return true;
  }

  @Override
  public void onLongPress(MotionEvent motionEvent) {

  }

  @Override
  public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2,
                         float v1, float v2) {
    return true;
  }

  public SceneRenderer getSceneRenderer() {
    return renderer;
  }
}
