package alexrnov.ledcubes;

import android.graphics.Color;

/** Util-class for managing colors */
public class BasicColor {

  /**
   * Create shades of color.
   * @param color - color as RGBA format
   * @return - color with shades for cube
   */
  /*
  public static float[][] shades(float[] color) {
    float[][] colorWithShades = new float[6][4];
    // front face
    colorWithShades[0] = new float[] {color[0], color[1], color[2], 1.0f};
    // back face
    colorWithShades[1] = new float[] {color[0] * 0.5f, color[1] * 0.5f, color[2] * 0.5f, 1.0f };
    // back-side face
    colorWithShades[2] = new float[] {color[0] * 0.7f, color[1] * 0.7f, color[2] * 0.7f, 1.0f };
    // front-side face
    colorWithShades[3] = new float[] {color[0] * 0.8f, color[1] * 0.8f, color[2] * 0.8f, 1.0f };
    // top face
    colorWithShades[4] = new float[] {color[0] * 0.9f, color[1] * 0.9f, color[2] * 0.9f, 1.0f };
    // down face
    colorWithShades[5] = new float[] {color[0] * 0.6f, color[1] * 0.6f, color[2] * 0.6f, 1.0f};

    return colorWithShades;
  }
   */

  /**
   * Create shades of color.
   * @param color - color as RGBA format
   * @return - color with shades for cube
   */
  public static float[][] shades(float[] color) {
    float[][] colorWithShades = new float[3][4];
    // front face
    colorWithShades[0] = new float[] {color[0], color[1], color[2], 0.4f };
    // one color for other three faces
    colorWithShades[1] = new float[] {color[0] * 0.7f, color[1] * 0.7f, color[2] * 0.7f, 0.4f };
    // top and down faces
    colorWithShades[2] = new float[] {color[0] * 0.9f, color[1] * 0.9f, color[2] * 0.9f, 0.4f };
    return colorWithShades;
  }

  /*
   * Created by Seker on 7/1/2015.
   * Some color static methods so I can setup the color quickly and not think hard either.
   */
  public static float[] red() {
    return new float[] {
            Color.red(Color.RED) / 255f,
            Color.green(Color.RED) / 255f,
            Color.blue(Color.RED) / 255f,
            1.0f };
  }

  public static float[] green() {
    return new float[] {
            Color.red(Color.GREEN) / 255f,
            Color.green(Color.GREEN) / 255f,
            Color.blue(Color.GREEN) / 255f,
            1.0f };
  }


  public static float[] blue() {
    return new float[] {
            Color.red(Color.BLUE) / 255f,
            Color.green(Color.BLUE) / 255f,
            Color.blue(Color.BLUE) / 255f,
            1.0f };
  }

  public static float[] yellow() {
    return new float[] {
            Color.red(Color.YELLOW) / 255f,
            Color.green(Color.YELLOW) / 255f,
            Color.blue(Color.YELLOW) / 255f,
            1.0f };
  }

  public static float[] cyan() {
    return new float[] {
            Color.red(Color.CYAN) / 255f,
            Color.green(Color.CYAN) / 255f,
            Color.blue(Color.CYAN) / 255f,
            1.0f };
  }

  public static float[] gray() {
    return new float[] {
            Color.red(Color.GRAY) / 255f,
            Color.green(Color.GRAY) / 255f,
            Color.blue(Color.GRAY) / 255f,
            1.0f };
  }

  public static float[] magenta() {
    return new float[] {
            Color.red(Color.MAGENTA) / 255f,
            Color.green(Color.MAGENTA) / 255f,
            Color.blue(Color.MAGENTA) / 255f,
            1.0f };
  }

  public static float[] white() {
    return new float[] {
            Color.red(Color.WHITE) / 255f,
            Color.green(Color.WHITE) / 255f,
            Color.blue(Color.WHITE) / 255f,
            1.0f };
  }



}