package org.pipoware.pst.exp;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fa
 */
public class PSTFileTest {

  public PSTFileTest() {
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

  /**
   * Test of readdwMagic method, of class PSTFile.
   */
  @Test
  public void testReaddwMagic() throws Exception {
    System.out.println("readdwMagic");
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/header.bin").toURI()));
    int dwMagic = pstFile.readdwMagic();
    assertEquals(1313096225L, Integer.toUnsignedLong(dwMagic));
  }

  /**
   * Test of readdwCRCPartial method, of class PSTFile.
   */
  @Test
  public void testReaddwCRCPartial() throws Exception {
    System.out.println("readdwCRCPartial");
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/header.bin").toURI()));
    int dwCRCPartial = pstFile.readdwCRCPartial();
    assertEquals(932882702L, Integer.toUnsignedLong(dwCRCPartial));
    
  }

  @Test
  public void testReadwVer() throws Exception {
    System.out.println("readwVer");
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/header.bin").toURI()));
    int dWver = pstFile.readwVer();
    assertEquals(23, dWver);
  }

  @Test
  public void testReadwVerEnron() throws Exception {
    System.out.println("readwVer");
    Path path = Paths.get("/Users/fa/Downloads/albert_meyers/albert_meyers_000_1_1.pst");
    PSTFile pstFile = new PSTFile(path);
    int dWver = pstFile.readwVer();
    assertEquals(23, dWver);
  }
  
  @Test
  public void testCRCEnron() throws Exception {
    Path path = Paths.get("/Users/fa/Downloads/albert_meyers/albert_meyers_000_1_1.pst");
    PSTFile pstFile = new PSTFile(path);
    int dwCRCPartial = pstFile.readdwCRCPartial();
    assertEquals(dwCRCPartial, pstFile.computeCRCPartial());
  }
}
