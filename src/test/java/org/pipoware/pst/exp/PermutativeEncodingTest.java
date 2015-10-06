package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class PermutativeEncodingTest {
  
  @Test
  public void testDecodeCryptPermute() {
    System.out.println("CryptPermute");
    
    byte[] data = new byte[] {
      (byte) 0xCC, (byte) 0x36, (byte) 0xFF, (byte) 0x93,
      (byte) 0x4C, (byte) 0x41, (byte) 0x41, (byte) 0x41,
      (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41};
    
    byte[] dataDecoded = new byte[] {
      (byte) 0x0A, (byte) 0x01, (byte) 0xEC, (byte) 0xBC,
      (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    
    PermutativeEncoding.decode(data);
    
    assertArrayEquals(dataDecoded, data);
  }
  
  @Test
  public void testEncodeCryptPermute() {
    System.out.println("CryptPermute");
    
    byte[] data = new byte[] {
      (byte) 0x0A, (byte) 0x01, (byte) 0xEC, (byte) 0xBC,
      (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    byte[] dataEncoded = new byte[] {
      (byte) 0xCC, (byte) 0x36, (byte) 0xFF, (byte) 0x93,
      (byte) 0x4C, (byte) 0x41, (byte) 0x41, (byte) 0x41,
      (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41};
    
    
    PermutativeEncoding.encode(data);
    
    assertArrayEquals(dataEncoded, data);
  }

}
