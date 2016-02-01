/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import org.pipoware.pst.exp.pages.BBTENTRY;
import org.pipoware.pst.exp.pages.BTENTRY;
import org.pipoware.pst.exp.pages.Page;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Franck
 */
public class PageTest {

  public PageTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test(expected=IllegalArgumentException.class)
  public void testSomeMethod() {
    byte [] bytes = new byte[512];
    bytes[512-16] = (byte) 0x70;
    Page p = new Page(bytes, Header.PST_TYPE.UNICODE);
  }

  @Test
  public void testSampleIntermediateBTPage() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pages/sample_intermediate_bt_page.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);
    Page p = new Page(bytes, Header.PST_TYPE.UNICODE);
  }

  @Test
  public void testSampleLeafNBTPage() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pages/sample_leaf_nbt_page.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);
    Page page = new Page(bytes, Header.PST_TYPE.UNICODE);
    System.out.println("Page " + page);
  }

  @Test
  public void testSampleLeafBBTPage() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pages/sample_leaf_bbt_page.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);

    // Bug in MS File Format documentation 3.5 Sample Leaf BBT Page :
    // cLevel = 0 in text but clLevel = 0x01 in hexadecimal dump
    // This is wrong because it's a leaf page and cLevel must be = 0
    
    // Verify original value
    Assert.assertEquals(0x01, bytes[491]);

    // Correct value
    bytes[491] = 0x00;

    // Verify original dwCRC value in PAGETRAILER
    Assert.assertEquals((byte) 0x2F, bytes[500]);
    Assert.assertEquals((byte) 0xA0, bytes[501]);
    Assert.assertEquals((byte) 0xF6, bytes[502]);
    Assert.assertEquals((byte) 0xA1, bytes[503]);

    // As we've changed cLevel value that is part of data that will be used for page CRC
    // we need to recompute CRC and change the dwCRC value in PAGETRAILER
    int dwNewCRCValue = CRC.computeCRC(0, Arrays.copyOf(bytes, Page.PAGE_SIZE - Page.UNICODE_TRAILER_SIZE));

    ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    bb.putInt(dwNewCRCValue);
    byte dwNewCRCValueBytes[] = bb.array();
    bytes[500] = dwNewCRCValueBytes[0];
    bytes[501] = dwNewCRCValueBytes[1];
    bytes[502] = dwNewCRCValueBytes[2];
    bytes[503] = dwNewCRCValueBytes[3];
    Page p = new Page(bytes, Header.PST_TYPE.UNICODE);
  }

  @Test
  public void testPSTPage() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI());
    PSTFile pstFile = new PSTFile(path);
    BREF bref = pstFile.getHeader().getRoot().bRefNBT;
    long offset = bref.getIb();
    pstFile.position(offset);
    byte []b = new byte[512];
    pstFile.read(b);
    Page page = new Page(b, pstFile.getHeader().getType());
    System.out.println("Page " + page);
  }

  @Test
  public void testPSTPages() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI());
    PSTFile pstFile = new PSTFile(path);
    BREF bref = pstFile.getHeader().getRoot().bRefNBT;
    Page page = pstFile.ndb.getPage(bref);
    System.out.println("Page " + page);
    for (BTENTRY btentry : page.btentries) {
      Page p = pstFile.ndb.getPage(btentry.bref);
      System.out.println("btkey 0x" + Long.toHexString(btentry.btKey));
      System.out.println(p);
    }
  }

  @Test
  public void testPSTPagesBlock() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI());
    PSTFile pstFile = new PSTFile(path);
    BREF bref = pstFile.getHeader().getRoot().bRefBBT;
    Page page = pstFile.ndb.getPage(bref);
    System.out.println("Page " + page);
    for (BTENTRY btentry : page.btentries) {
      bref = btentry.bref;
      Page p = pstFile.ndb.getPage(bref);
      System.out.println("btkey 0x" + Long.toHexString(btentry.btKey));
      System.out.println(p);

      for (BBTENTRY bbtentry : p.bbtentries) {
        bref = bbtentry.bref;
        long offset = bref.getIb();
        pstFile.position(offset);
        bbtentry.bref.getBid();
        bbtentry.bref.getIb();
        int cb = bbtentry.cb;
        Block block = new Block(pstFile, bbtentry);
        System.out.println(block);
      }
    }
  }

  @Test
  public void testPSTPagesBlockTree() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI());
    PSTFile pstFile = new PSTFile(path);
    BREF bref = pstFile.getHeader().getRoot().bRefBBT;
    Page page = pstFile.ndb.getPage(bref);
    System.out.println("Page " + page);
    displayPage(pstFile, page, 0);
  }

  private void displayPage(PSTFile pstFile, Page page, int indent) throws IOException {
    if (page.getDepthLevel() == 0) {
      for (BBTENTRY bbtentry : page.bbtentries) {
        Block block = new Block(pstFile, bbtentry);
        System.out.println(Strings.repeat(" ", indent) + block);
      }
    } else {
      for (BTENTRY btentry : page.btentries) {
        Page p = pstFile.ndb.getPage(btentry.bref);
        System.out.println(Strings.repeat(" ", indent)+ "btkey 0x" + Long.toHexString(btentry.btKey));
        displayPage(pstFile, p, indent + 4);
      }
    }
  }
}
