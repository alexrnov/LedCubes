package alexrnov.ledcubes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class Buffers {

  public static FloatBuffer newFloatBuffer(float[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4)
            .order(ByteOrder.nativeOrder());
    FloatBuffer returnBuffer = byteBuffer.asFloatBuffer();
    return returnBuffer.put(data);
  }

  public static DoubleBuffer newDoubleBuffer(double[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 8)
            .order(ByteOrder.nativeOrder());
    DoubleBuffer returnBuffer = byteBuffer.asDoubleBuffer();
    return returnBuffer.put(data);
  }

  public static ShortBuffer newShortBuffer(short[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 2)
            .order(ByteOrder.nativeOrder());
    ShortBuffer returnBuffer = byteBuffer.asShortBuffer();
    return returnBuffer.put(data);
  }

  public static CharBuffer newCharBuffer(char[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 2)
            .order(ByteOrder.nativeOrder());
    CharBuffer returnBuffer = byteBuffer.asCharBuffer();
    return returnBuffer.put(data);
  }

  public static IntBuffer newIntBuffer(int[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4)
            .order(ByteOrder.nativeOrder());
    IntBuffer returnBuffer = byteBuffer.asIntBuffer();
    return returnBuffer.put(data);
  }

  public static LongBuffer newLongBuffer(long[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 8)
            .order(ByteOrder.nativeOrder());
    LongBuffer returnBuffer = byteBuffer.asLongBuffer();
    return returnBuffer.put(data);
  }

  public static ByteBuffer newByteBuffer(byte[] data) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length);
    byteBuffer.order(ByteOrder.nativeOrder());
    return byteBuffer.put(data);
  }
}
