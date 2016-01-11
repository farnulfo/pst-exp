/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp.pages;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumSet;
import org.pipoware.pst.exp.CRC;
import org.pipoware.pst.exp.Header;

/**
 *
 * @author Franck
 */
public class Page {

  public static final int PAGE_SIZE = 512;
  public static final int UNICODE_TRAILER_SIZE = 16;
  public static final int ANSI_TRAILER_SIZE = 12;
  private static final int DWPADDING_SIZE = 4;

  private final byte[] bytes;
  private byte pTypeRepeat;
  private short wSig;
  private int dwCRC;
  private long bid;
  /**
   * The number of BTree entries stored in the page data.
   */
  private byte cEnt;
  
  /**
   * The maximum number of entries that can fit inside the page data.
   */
  private byte cEntMax;
  
  /**
   * The size of each BTree entry
   */
  private byte cbEnt;
  
  /**
   * The depth level of this page.
   */
  private byte cLevel;
  
  public NBTENTRY[] nbtentries;
  public BTENTRY[] btentries;
  public BBTENTRY[] bbtentries;

  public enum PageType {

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
  
  private enum EntryType {
    NBTENTRY, BTENTRY, BBTENTRY
  }

  private PageType pageType;
  private byte pType;

  public Page(byte[] bytes, Header.PST_TYPE type) {
    Preconditions.checkArgument(type == Header.PST_TYPE.ANSI || type == Header.PST_TYPE.UNICODE);
    this.bytes = Arrays.copyOf(bytes, PAGE_SIZE);

    int offset = 0;
    int length = 0;
    if (type == Header.PST_TYPE.UNICODE) {
      offset = PAGE_SIZE - UNICODE_TRAILER_SIZE;
      length = UNICODE_TRAILER_SIZE;
    } else {
      offset = PAGE_SIZE - ANSI_TRAILER_SIZE;
      length = ANSI_TRAILER_SIZE;
    }
    
    ByteBuffer bb = ByteBuffer.wrap(this.bytes, offset, length).order(ByteOrder.LITTLE_ENDIAN);

    pType = bb.get();
    pTypeRepeat = bb.get();
    
    if (pType != pTypeRepeat) {
      throw new IllegalArgumentException("pTYpe : " + pType + " <> pTypeRepeat : " + pTypeRepeat);
    }

    pageType = PageType.get(pType);

    wSig = bb.getShort();
    if ((pageType == PageType.ptypeAMap) || (pageType == PageType.ptypePMap)
      || (pageType == PageType.ptypeFMap) || (pageType == PageType.ptypeFPMap)) {
      if (wSig != 0) {
        throw new IllegalArgumentException("wSig : " + wSig + " <> 0");
      }
    }

    byte[] crcData;
    int dwComputedCRC;

    switch (type) {

      case UNICODE:
        dwCRC = bb.getInt();

        crcData = Arrays.copyOf(this.bytes, PAGE_SIZE - UNICODE_TRAILER_SIZE);
        dwComputedCRC = CRC.computeCRC(0, crcData);
        if (dwCRC != dwComputedCRC) {
          throw new IllegalArgumentException("dwCRC = " + dwCRC + " <> dwComputedCRC = " + dwComputedCRC);
        }

        bid = bb.getLong();
        break;

      case ANSI:
        bid = bb.getInt();
        dwCRC = bb.getInt();

        crcData = Arrays.copyOf(this.bytes, PAGE_SIZE - ANSI_TRAILER_SIZE);
        dwComputedCRC = CRC.computeCRC(0, crcData);
        if (dwCRC != dwComputedCRC) {
          throw new IllegalArgumentException("dwCRC = " + dwCRC + " <> dwComputedCRC = " + dwComputedCRC);
        }
        break;

      default:
        throw new AssertionError();
    }

    switch (pageType) {
      case ptypeBBT:
      case ptypeNBT:
        handleBTreePage(type);
        break;
      default:
        throw new AssertionError();
    }
  }

  private void handleBTreePage(Header.PST_TYPE type) {
    int offset = 0;
    switch (type) {
      case UNICODE:
        offset = PAGE_SIZE - UNICODE_TRAILER_SIZE - DWPADDING_SIZE;
        break;

      case ANSI:
        offset = PAGE_SIZE - ANSI_TRAILER_SIZE;
        break;
      default:
        throw new AssertionError();
    }

    offset -= 4; // 4 = sizeof(cEnt)+ sizeof(cEntMax)+sizeof(cbEnt)+sizeof(cLevel)

    cEnt = this.bytes[offset++];
    cEntMax = this.bytes[offset++];
    cbEnt = this.bytes[offset++];
    cLevel = this.bytes[offset++];

    if (pageType == PageType.ptypeNBT) {
      if (cLevel == 0) {
        // NBTENTRY
        readRgEntries(EntryType.NBTENTRY, type);
      } else if (cLevel > 0) {
        // BTENTRY
        readRgEntries(EntryType.BTENTRY, type);
      } else {
        throw new IllegalArgumentException("Unexpected cLevel value : " + cLevel + " for a NBT page");
      }
    } else if (pageType == PageType.ptypeBBT) {
      if (cLevel == 0) {
        // BBTENTRY
        readRgEntries(EntryType.BBTENTRY, type);
      } else if (cLevel > 0) {
        // BTENTRY
        readRgEntries(EntryType.BTENTRY, type);
      } else {
        throw new IllegalArgumentException("Unexpected cLevel value : " + cLevel + " for a BBT page");
      }
    }
  }

  private void readRgEntries(EntryType entryType, Header.PST_TYPE type) {
    switch (entryType) {
      case NBTENTRY:
        nbtentries = new NBTENTRY[cEnt];
        for (int i = 0; i < cEnt; i++) {
          int offset = i*cbEnt;
          byte entrybyte[] = Arrays.copyOfRange(bytes, offset, offset + cbEnt);
          nbtentries[i] = new NBTENTRY(entrybyte, type);
        }
        break;
      
      case BTENTRY:
        btentries = new BTENTRY[cEnt];
        for (int i = 0; i < cEnt; i++) {
          int offset = i*cbEnt;
          byte entrybyte[] = Arrays.copyOfRange(bytes, offset, offset + cbEnt);
          btentries[i] = new BTENTRY(entrybyte, type);
        }
        break;
        
      case BBTENTRY:
        bbtentries = new BBTENTRY[cEnt];
        for (int i = 0; i < cEnt; i++) {
          int offset = i*cbEnt;
          byte entrybyte[] = Arrays.copyOfRange(bytes, offset, offset + cbEnt);
          bbtentries[i] = new BBTENTRY(entrybyte, type);
        }
        break;

      default:
        throw new UnsupportedOperationException("Unsupported " + entryType);
    }
  }
  
  public int getDepthLevel() {
    if (pageType == PageType.ptypeNBT || pageType == PageType.ptypeBBT) {
      return cLevel;
    } else {
      throw new IllegalArgumentException("Depth level is not a field of type " + pageType);
    }
  }
  
  public PageType getType() {
    return pageType;
  }

  public byte getcEnt() {
    return cEnt;
  }

  public byte getcEntMax() {
    return cEntMax;
  }

  public byte getcbEnt() {
    return cbEnt;
  }

  public byte getcLevel() {
    return cLevel;
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("type", "0x" + Long.toHexString(Byte.toUnsignedInt(pType)) + " (" + pageType + ")")
      .add("cEnt", cEnt)
      .add("cEntMax", cEntMax)
      .add("cbEnt", cbEnt)
      .add("cLevel", cLevel)
      .add("nbtentries", Arrays.toString(nbtentries))
      .add("btentries", Arrays.toString(btentries))
      .add("bbtentries", Arrays.toString(bbtentries))
      .toString();
  }
}