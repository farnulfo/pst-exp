package org.pipoware.pst.exp;

import com.google.common.io.BaseEncoding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author fa
 */
public class HexToBin {

  private static final String EXAMPLE_LINE = " 0000000000000000  21 42 44 4E 0E A9 9A 37-53 4D 17 00 13 00 01 01  *!BDN...7SM......*";
  private static final String PREFIX = " 0000000000000000  ";
  private static final String SUFFIX = "  *!BDN...7SM......*";

  public static void fromHexToBin(Path pathInput, Path output) throws IOException {
    try (BufferedReader br = new BufferedReader(Files.newBufferedReader(pathInput, StandardCharsets.UTF_8));
            OutputStream os = Files.newOutputStream(output, StandardOpenOption.CREATE_NEW);) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.substring(PREFIX.length(), EXAMPLE_LINE.length() - SUFFIX.length());
        System.out.println(line);

        String tokens[] = line.split("( |-)");
        for (String token : tokens) {
          System.out.println(token);

          byte[] decode = BaseEncoding.base32Hex().decode(token);
          os.write(decode);
        }
      }
    }
  }

}
