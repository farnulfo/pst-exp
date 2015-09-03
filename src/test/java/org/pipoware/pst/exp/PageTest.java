/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
  
}
