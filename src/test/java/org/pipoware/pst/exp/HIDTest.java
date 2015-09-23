package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class HIDTest {

  /**
   * Test of toString method, of class HID.
   */
  @Test
  public void testHID() {
    HID hid = new HID(0x20);
    assertEquals(0, hid.type);
    assertEquals(1, hid.hidIndex);
    assertEquals(0, hid.hidBlockIndex);

    hid = new HID(0x10020);
    assertEquals(0, hid.type);
    assertEquals(1, hid.hidIndex);
    assertEquals(1, hid.hidBlockIndex);
  }

}
