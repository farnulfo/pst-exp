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
        String files[] = new String[]{
            "/header.bin",
            "/header2.bin",
            "/pstsdk/sample1.pst",
            // "/pstsdk/sample2.pst", bSentinel != 0x80
            "/pstsdk/submessage.pst",
            // "/pstsdk/test_ansi.pst", bSentinel != 0x80
            "/pstsdk/test_unicode.pst"
        };

        for (String f : files) {
            Path path = Paths.get(getClass().getResource(f).toURI());
            PSTFile pst = new PSTFile(path);
            RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
            file.seek(8);
            byte data[] = new byte[471];
            file.read(data);

            int crc = CRC.computeCRC(0, data);
            assertEquals("computeCRC for " + f, pst.getHeader().getDwCRCPartial(), crc);
        }
    }

}
