/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author fa
 */
public class CRCTest {

  public CRCTest() {
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

  @Test
  public void testByteBuufer() {
    final byte test[] = {
      0x21, 0x42, 0x44, 0x4E
    };

    System.out.println(Integer.toUnsignedString(ByteBuffer.wrap(test).getInt()));
    System.out.println(Integer.toUnsignedString(ByteBuffer.wrap(test).order(ByteOrder.LITTLE_ENDIAN).getInt()));

  }

  @Test
  public void testCRC() throws Exception {
    Path path = Paths.get(getClass().getResource("/header.bin").toURI());
    RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
    file.seek(8);
    byte data[] = new byte[471];
    file.read(data);

    PSTFile pst = new PSTFile(path);
    System.out.println("dwCRCPartial: " + Integer.toUnsignedString(pst.getHeader().getDwCRCPartial()));
    int crc = CRC.computeCRC(0, data);
    System.out.println(Integer.toUnsignedString(crc));
  }

  @Test
  public void testCRCEnron() throws Exception {
    //Path path = Paths.get("/Users/fa/Downloads/albert_meyers/albert_meyers_000_1_1.pst");
    //Path path = Paths.get("/Users/fa/Projects/java-libpst/src/test/resources/dist-list.pst");
    //Path path = Paths.get("/Users/fa/Projects/java-libpst/src/test/resources/passworded.pst");
    Path path = Paths.get(getClass().getResource("/header2.bin").toURI());
    PSTFile pst = new PSTFile(path);
    System.out.println("dwMagic: " + Integer.toUnsignedString(pst.getHeader().getDwMagic()));
    System.out.println("dwCRCPartial: " + Integer.toUnsignedString(pst.getHeader().getDwCRCPartial()));

    RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
    file.seek(8);
    byte data[] = new byte[471];
    file.read(data);

    int crc = CRC.computeCRC(0, data);
    System.out.println("computed dwCRCPartial: " + Integer.toUnsignedString(crc));
  }

//  @Test
//  public void testCRC2() throws Exception {
//    String paths[] = {
//      "/Users/fa/Downloads/albert_meyers/albert_meyers_000_1_1.pst",
//      "/Users/fa/Projects/java-libpst/src/test/resources/dist-list.pst",
//      "/Users/fa/Projects/java-libpst/src/test/resources/passworded.pst"
//    };
//
//    for (String p : paths) {
//      Path path = Paths.get(p);
//      PSTFile pst = new PSTFile(path);
//      System.out.println("path: " + path);
//      System.out.println("dwMagic: " + Integer.toUnsignedString(pst.readdwMagic()));
//      System.out.println("dwCRCPartial: " + Integer.toUnsignedString(pst.readdwCRCPartial()));
//
//      RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
//      file.seek(8);
//      byte data[] = new byte[471];
//      file.read(data);
//
//      int crc = CRC.computeCRC(0, data);
//      System.out.println("computed dwCRCPartial: " + Integer.toUnsignedString(crc));
//
//      assertEquals(Integer.toUnsignedString(pst.readdwCRCPartial()), Integer.toUnsignedString(crc));
//      assertEquals(pst.readdwCRCPartial(), crc);
//    }
//  }
}
