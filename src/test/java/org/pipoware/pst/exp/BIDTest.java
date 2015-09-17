package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck
 */
public class BIDTest {
  
  /**
   * Test of isInternal method, of class BID.
   */
  @Test
  public void testIsInternal() {
    assertEquals(false, BID.isInternal(0x4));
    assertEquals(false, BID.isInternal(0xC));
    assertEquals(true, BID.isInternal(0x176));
    assertEquals(true, BID.isInternal(0x1BA));
  }
  
}
