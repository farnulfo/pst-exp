package org.pipoware.pst.exp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import static org.pipoware.pst.exp.Header.PST_TYPE.UNICODE;
import org.pipoware.pst.exp.pages.BBTENTRY;

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

    // MS-PST 3.6 Sample Data Tree
    int nbBIDs = 0x35;
    int sizeOfBID_UNICODE = 8;
    Assert.assertEquals(448, Block.diskSize(nbBIDs * sizeOfBID_UNICODE, UNICODE));
  }

  @Test
  public void testSampleDataTreeXBlock() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/blocks/sample_data_xblock.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);
    // MS-PST 3.6 Sample Data Tree

    // 00000000005A67B0  B0 01 38 67 51 CD EE 3F-62 01 00 00 00 00 00 00
    //                     BID = LITTLE ENDIAN ( ^^ ^^ ^^ ^^ ^^ ^^ ^^ ^^ )
    long bid = 0x162;

    // 00000000005A6600  01 01 35 00 49 9C 06 00-5C 01 00 00 00 00 00 00  *..5.I...\.......*
    // ^^^^^^^^^^^^^^^^ = Byte Index (IB) of block  
    long ib = 0x5A6600;

    BREF bref = new BREF(bid, ib);

    int nbBIDs = 0x35;
    short bbtentry_cb = Block.computeXBlockDataSize(nbBIDs, UNICODE);
    // cref is not part of block data
    // MS-PST 3.6 Sample Data Tree didn't tell what value is so 0 (not used in building a block)
    short bbtentry_cref = 0;

    BBTENTRY bbtentry = new BBTENTRY(bref, bbtentry_cb, bbtentry_cref);
    Block block = new Block(bytes, bbtentry, UNICODE);

    Assert.assertEquals(Block.BlockType.XBLOCK, block.blockType);
    long[] expectedBids = new long[]{
      0x15c, 0x164, 0x168, 0x16c, 0x170, 0x174, 0x178, 0x17c,
      0x180, 0x184, 0x188, 0x18c, 0x190, 0x194, 0x198, 0x19c,
      0x1a0, 0x1a4, 0x1a8, 0x1ac, 0x1b0, 0x1b4, 0x1b8, 0x1bc,
      0x1c0, 0x1c4, 0x1c8, 0x1cc, 0x1d0, 0x1d4, 0x1d8, 0x1dc,
      0x1e0, 0x1e4, 0x1e8, 0x1ec, 0x1f0, 0x1f4, 0x1f8, 0x1fc,
      0x200, 0x204, 0x208, 0x20c, 0x210, 0x214, 0x218, 0x21c,
      0x220, 0x224, 0x228, 0x22c, 0x230};
    Assert.assertArrayEquals(expectedBids, block.rgbid);
  }

  @Test
  public void testBuildXBlock() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/blocks/sample_data_xblock.bin").toURI());
    byte[] expectedBytes = Files.readAllBytes(path);
    long bid = 0x162;
    long ib = 0x5A6600;
    int lcbTotal = 0x00069C49;
    long[] bids = new long[]{
      0x15c, 0x164, 0x168, 0x16c, 0x170, 0x174, 0x178, 0x17c,
      0x180, 0x184, 0x188, 0x18c, 0x190, 0x194, 0x198, 0x19c,
      0x1a0, 0x1a4, 0x1a8, 0x1ac, 0x1b0, 0x1b4, 0x1b8, 0x1bc,
      0x1c0, 0x1c4, 0x1c8, 0x1cc, 0x1d0, 0x1d4, 0x1d8, 0x1dc,
      0x1e0, 0x1e4, 0x1e8, 0x1ec, 0x1f0, 0x1f4, 0x1f8, 0x1fc,
      0x200, 0x204, 0x208, 0x20c, 0x210, 0x214, 0x218, 0x21c,
      0x220, 0x224, 0x228, 0x22c, 0x230};

    Assert.assertArrayEquals(expectedBytes, Block.buildXBlock(bid, ib, lcbTotal, bids, Header.PST_TYPE.UNICODE));
  }

  /*
   * Last bit of a BID according to MS-PST documentation v20100627 : Reserved bit.
   * Readers MUST ignore this bit and treat it as zero (0) before looking up the BID from the BBT.
   * Writers MUST set this bit to zero (0).
   */
  @Test
  public void testXBlockWithIncorrectBID() throws URISyntaxException, IOException {
    long bid = 0x162;
    long ib = 0x5A6600;
    int lcbTotal = 0x00069C49;
    long[] expectedBids = new long[]{
      0x200,
      0x204,
      0x208,
      0x20c,
      0x210};

    long[] bidsWithReservedBitSet = new long[expectedBids.length];
    for (int i = 0; i < expectedBids.length; i++) {
      bidsWithReservedBitSet[i] = expectedBids[i] | 0x1;
    }

    byte[] blockBytes = Block.buildXBlock(bid, ib, lcbTotal, bidsWithReservedBitSet, Header.PST_TYPE.UNICODE);
    final BREF bref = new BREF(bid, ib);
    BBTENTRY bbtentry = new BBTENTRY(bref, Block.computeXBlockDataSize(expectedBids.length, UNICODE), (short) 0);
    Block block = new Block(blockBytes, bbtentry, UNICODE);

    Assert.assertArrayEquals(expectedBids, block.rgbid);
  }

  @Test
  public void testSignature() {
    Assert.assertEquals((short) 0x4C04, Block.computeSig(19456, 0x4));
    Assert.assertEquals((short) 0x8DD8, Block.computeSig(36160, 0x98));
    Assert.assertEquals((short) 0x2D7A, Block.computeSig(142336, 0x178));
    Assert.assertEquals((short) 0x8490, Block.computeSig(9372634112L, 0x877ae40));
  }
}
