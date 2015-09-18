package org.pipoware.pst.exp;

import org.junit.Assert;
import org.junit.Test;
import static org.pipoware.pst.exp.Header.PST_TYPE.UNICODE;

/**
 *
 * @author Franck
 */
public class BlockTest {
  
  @Test
  public void testDiskSize() {
    Assert.assertEquals(192, Block.diskSize(156, UNICODE));
    Assert.assertEquals(320, Block.diskSize(276, UNICODE));
    Assert.assertEquals(192, Block.diskSize(124, UNICODE));
  }
  
}
