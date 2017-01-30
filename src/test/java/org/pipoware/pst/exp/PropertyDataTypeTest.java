package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class PropertyDataTypeTest {
  
  /**
   * Test of values method, of class PropertyDataType.
   */
  @Test
  public void testGet() throws UnknownPropertyDataTypeException {
    assertEquals(PropertyDataType.PtypString8, PropertyDataType.get((short) 0x001E));
  }

}
