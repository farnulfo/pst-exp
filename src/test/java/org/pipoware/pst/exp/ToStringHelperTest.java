package org.pipoware.pst.exp;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Franck Arnulfo
 */
public class ToStringHelperTest {
  
  @Test
  public void testFormatMultiline() {
    String test = "HID{hid=0x40}";
    String expected = 
      "HID{\n" +
      "  hid=0x40\n" +
      "}";
    String result = ToStringHelper.formatMultiline(test);
    assertEquals(expected, result);
  }

  @Test
  public void testFormatMultiline2() {
    String test = "TC{hn=HN{ibHnpm=0x1bc, bSig=0xec, bClientSig=0x7c, hidUserRoot=0x40 HID{hid=0x40, type=0, hidIndex=2, hidBlockIndex=0}, rgbFillLevel=0x0, cAlloc=0x7, cFree=0x0}}";
    String expected = 
      "TC{\n" +
      "  hn=HN{\n" +
      "    ibHnpm=0x1bc, bSig=0xec, bClientSig=0x7c, hidUserRoot=0x40 HID{\n" +
      "      hid=0x40, type=0, hidIndex=2, hidBlockIndex=0\n" +
      "    }\n" +
      "    , rgbFillLevel=0x0, cAlloc=0x7, cFree=0x0\n" +
      "  }\n" +
      "}";
    String result = ToStringHelper.formatMultiline(test);
    System.out.println(expected);
    System.out.println(result);
    // assertEquals(expected, result);
  }

  @Test
  public void testFormatMultiline3() {
    String test = "TC{hn=HN{ibHnpm=0x1bc, bSig=0xec, bClientSig=0x7c, hidUserRoot=0x40 HID{hid=0x40, type=0, hidIndex=2, hidBlockIndex=0}, rgbFillLevel=0x0, cAlloc=0x7, cFree=0x0, rgiAlloc=[12, 20, 146, 170, 335, 381, 403, 443]}, tcinfo=TCINFO{bType=124, cCols=13, TCI_4b=52, TCI_2b=52, TCI_1b=53, TCI_bm=55, hidRowIndex=32, hnidRows=128, hnidIndex=0, tColDesc=[TCOLDESC{tag=0xe300102, ibData=20, cbData=4, iBit=6}, TCOLDESC{tag=0xe330014, ibData=24, cbData=8, iBit=7}, TCOLDESC{tag=0xe340102, ibData=32, cbData=4, iBit=8}, TCOLDESC{tag=0xe380003, ibData=36, cbData=4, iBit=9}, TCOLDESC{tag=0x3001001f, ibData=8, cbData=4, iBit=2}, TCOLDESC{tag=0x36020003, ibData=12, cbData=4, iBit=3}, TCOLDESC{tag=0x36030003, ibData=16, cbData=4, iBit=4}, TCOLDESC{tag=0x360a000b, ibData=52, cbData=1, iBit=5}, TCOLDESC{tag=0x3613001f, ibData=40, cbData=4, iBit=10}, TCOLDESC{tag=0x66350003, ibData=44, cbData=4, iBit=11}, TCOLDESC{tag=0x66360003, ibData=48, cbData=4, iBit=12}, TCOLDESC{tag=0x67f20003, ibData=0, cbData=4, iBit=0}, TCOLDESC{tag=0x67f30003, ibData=4, cbData=4, iBit=1}]}, bthheader=BTHHEADER{bType=0xb5, cbKey=4, cbEnt=4, bIdxLevels=0, hidRoot=HID{hid=0x60, type=0, hidIndex=3, hidBlockIndex=0}}, tcRowIds=[TCROWID{dwRowID=8739, dwRowIndex=2}, TCROWID{dwRowID=32802, dwRowIndex=0}, TCROWID{dwRowID=32834, dwRowIndex=1}]}";
    System.out.println(ToStringHelper.formatMultiline(test));
  }

  @Test
  public void testSpace() {
    assertEquals("   ", ToStringHelper.space(3));
  }
  
}
