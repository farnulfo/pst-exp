/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    Page p = new Page(bytes, Header.PST_TYPE.UNICODE);
  }
  
  @Test
  public void testSampleLeafBBTPage() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/pages/sample_leaf_bbt_page.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);
    // Bug in MS File Format documentation 3.5 Sample Leaf BBT Page
    // cLevel = 0 in text but clLevel = 0x01 in hexadecimal dump
    Assert.assertEquals(0x01, bytes[491]);
    bytes[491] = 0x00;
    Page p = new Page(bytes, Header.PST_TYPE.UNICODE);
  }
}
