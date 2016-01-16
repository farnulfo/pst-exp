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
public class BTHTest {

  @Test
  public void testToString() throws URISyntaxException, IOException {
    Path path = Paths.get(getClass().getResource("/ltp/sample_heap_on_node.bin").toURI());
    byte[] bytes = Files.readAllBytes(path);

    HN hn = new HN(null, 0, bytes);
    BTH bth = new BTH(hn);
    
    System.out.println(bth);
  }

}
