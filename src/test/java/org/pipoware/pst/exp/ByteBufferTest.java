package org.pipoware.pst.exp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Test;

public class ByteBufferTest {

  @Test
  public void testByteBuffer1() {
    byte b[] = new byte[]{
      0x0F, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    
    ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
    long l = bb.getLong();
    String s = Long.toHexString(l);
    System.out.println(s);
  }

}
