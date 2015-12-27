package org.pipoware.pst.exp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class HNTest {

  @Test
  public void testHNFromSampleHeapOnNode() throws Exception {
    Path path = Paths.get(getClass().getResource("/ltp/sample_heap_on_node.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);

    HN hn = new HN(null, bytes);

    assertEquals(0xEC, Byte.toUnsignedInt(hn.bSig));
    assertEquals(0xBC, Byte.toUnsignedInt(hn.bClientSig));
    assertEquals(0x00000020, hn.hidUserRoot);
    assertEquals(8, hn.cAlloc);
    assertEquals(0, hn.cFree);
    assertArrayEquals(new short[]{0x0C, 0x14, 0x6C, 0x7C, 0x8C, 0xA4, 0xBC, 0xD4, 0xEC}, hn.rgibAlloc);
    
    HID hid = new HID(hn.hidUserRoot);
    assertEquals(1, hid.hidIndex);
    
    byte[] b = hn.getHeapItem(hid);
    assertEquals(8, b.length);
    assertArrayEquals(new byte[]{(byte) 0xB5, 0x02, 0x06, 0x00, 0x40, 0x00, 0x00, 0x00}, b);
    System.out.println(hn);
  }

}
