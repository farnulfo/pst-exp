package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class PropertyTagTest {

  /**
   * Test of getPropertyTagFromIdentifier method, of class PropertyTag.
   */
  @Test
  public void testGetPropertyTagFromIdentifier() {
    System.out.println("getPropertyTagFromIdentifier");
    assertEquals(PropertyTag.PidTagDisplayName,
      PropertyTag.getPropertyTagFromIdentifier((short) 0x3001, PropertyDataType.PtypString));

    assertEquals(PropertyTag.PidTagDisplayName,
      PropertyTag.getPropertyTagFromIdentifier((short) 0x3001, PropertyDataType.PtypString8));
  }

}
