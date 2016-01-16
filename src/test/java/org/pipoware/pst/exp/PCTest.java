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

/**
 *
 * @author Franck Arnulfo
 */
public class PCTest {
  
  @Test
  public void testPC() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/ltp/sample_heap_on_node.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);

    HN hn = new HN(null, 0, bytes);
    BTH bth = new BTH(hn);
    PC pc = new PC(bth, null, null);
    
    System.out.println(pc);
    
  }
  
}
