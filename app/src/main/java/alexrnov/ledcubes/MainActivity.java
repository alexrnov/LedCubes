package alexrnov.ledcubes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  private CustomSurfaceView customSurfaceView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    customSurfaceView = findViewById(R.id.oglView);

    customSurfaceView.init(3);
  }
}
