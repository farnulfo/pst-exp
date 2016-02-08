package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class HNDataBlockTest {
  
  /**
   * Test of getType method, of class HNDataBlock.
   */
  @Test
  public void testGetType() {
    assertEquals(HNDataBlock.Type.HNHDR,  HNDataBlock.getType(0));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(1));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(2));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(3));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(4));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(5));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(7));
    assertEquals(HNDataBlock.Type.HNBITMAPHDR,  HNDataBlock.getType(8));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(9));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(128));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(135));
    assertEquals(HNDataBlock.Type.HNBITMAPHDR,  HNDataBlock.getType(136));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(137));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(263));
    assertEquals(HNDataBlock.Type.HNBITMAPHDR,  HNDataBlock.getType(264));
    assertEquals(HNDataBlock.Type.HNPAGEHDR,  HNDataBlock.getType(265));
  }
  
}
