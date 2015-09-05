/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumSet;

/**
 *
 * @author Franck
 */
class Page {

  public static final int PAGE_SIZE = 512;
  private static final int UNICODE_TRAILER_SIZE = 16;
  private static final int ANSI_TRAILER_SIZE = 12;

  private final byte[] bytes;
  private byte pTypeRepeat;
  private short wSig;
  private int dwCRC;
  private long bid;

  private enum PageType {

    ptypeBBT((byte) 0x80),
    ptypeNBT((byte) 0x81),
    ptypeFMap((byte) 0x82),
    ptypePMap((byte) 0x83),
    ptypeAMap((byte) 0x84),
    ptypeFPMap((byte) 0x85),
    ptypeDL((byte) 0x86);

    private final byte type;

    private PageType(byte b) {
      type = b;
    }

    public static PageType get(byte b) {
      for (PageType p : EnumSet.allOf(PageType.class)) {
        if (p.getValue() == b) {
          return p;
        }
      }
      throw new IllegalArgumentException("Invalid Page Type " + b);
    }

    public byte getValue() {
      return type;
    }

  };

  private PageType pageType;
  private byte pType;

  Page(byte[] bytes, Header.PST_TYPE type) {
    this.bytes = Arrays.copyOf(bytes, PAGE_SIZE);

    int offset = 0;
    if (type == Header.PST_TYPE.UNICODE) {
      offset = PAGE_SIZE - UNICODE_TRAILER_SIZE;
    } else {
      offset = PAGE_SIZE - ANSI_TRAILER_SIZE;
    }
    pType = this.bytes[offset];
    offset++;
    pTypeRepeat = this.bytes[offset];
    offset++;

    if (pType != pTypeRepeat) {
      throw new IllegalArgumentException("pTYpe : " + pType + " <> pTypeRepeat : " + pTypeRepeat);
    }

    pageType = PageType.get(pType);

    ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
    bb.put(this.bytes, offset, 2);
    wSig = bb.getShort(0);
    if ((pageType == PageType.ptypeAMap) || (pageType == PageType.ptypePMap)
      || (pageType == PageType.ptypeFPMap) || (pageType == PageType.ptypeFPMap)) {
      if (wSig != 0) {
        throw new IllegalArgumentException("wSig : " + wSig + " <> 0");
      }
    }
    offset += 2;

    byte[] crcData;
    int dwComputedCRC;

    switch (type) {

      case UNICODE:
        bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(this.bytes, offset, 4);
        dwCRC = bb.getInt();
        offset += 4;

        crcData = Arrays.copyOf(this.bytes, PAGE_SIZE - UNICODE_TRAILER_SIZE);
        dwComputedCRC = CRC.computeCRC(dwCRC, crcData);
        if (dwCRC != dwComputedCRC) {
          throw new IllegalArgumentException("dwCRC = " + dwCRC + " <> dwComputedCRC = " + dwComputedCRC);
        }

        bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(this.bytes, offset, 8);
        bid = bb.getLong();
        break;

      case ANSI:
        bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(this.bytes, offset, 4);
        bid = bb.getInt();

        bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        bb.put(this.bytes, offset, 4);
        dwCRC = bb.getInt();

        crcData = Arrays.copyOf(this.bytes, PAGE_SIZE - ANSI_TRAILER_SIZE);
        dwComputedCRC = CRC.computeCRC(dwCRC, crcData);
        if (dwCRC != dwComputedCRC) {
          throw new IllegalArgumentException("dwCRC = " + dwCRC + " <> dwComputedCRC = " + dwComputedCRC);
        }
        break;

      default:
        throw new AssertionError();
    }
  }
}
