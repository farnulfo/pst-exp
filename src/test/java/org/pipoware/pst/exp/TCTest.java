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
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Franck Arnulfo
 */
public class TCTest {

  @Test
  public void testTC() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/ltp/sample_tc.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);
    
    
    Header mockedHeader = mock(Header.class);
    when(mockedHeader.getType()).thenReturn(Header.PST_TYPE.UNICODE);
    PSTFile mockedPSTFile = mock(PSTFile.class);
    when(mockedPSTFile.getHeader()).thenReturn(mockedHeader);

    NDB ndb = new NDB(mockedPSTFile, mockedHeader);
    HN hn = new HN(ndb, 0, bytes);
    TC tc = new TC(hn, null);

    System.out.println(tc);

  }
  
  @Test
  public void testRowsPerBlock() {
    int blockSize = 10;
    for(int i = 1; i < 15; i++) {
      int tmp = blockSize / i;
      System.out.println("Blocksize=" + blockSize + ", row size=" + i + ", Rows per block=" + tmp);
    }
  }

}
