/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.IOException;
import java.net.URISyntaxException;
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
public class HexToBinTest {
  
  public HexToBinTest() {
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
   * Test of fromHexToBin method, of class HexToBin.
   * @throws java.net.URISyntaxException
   * @throws java.io.IOException
   */
  @Test
  public void testFromHexToBin() throws URISyntaxException, IOException {
    System.out.println("fromHexToBin");
    Path pathInput = Paths.get(getClass().getResource("/header.txt").toURI());
    Path pathOutput = Paths.get("output.bin");
    //HexToBin.fromHexToBin(pathInput, pathOutput);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }
  
}
