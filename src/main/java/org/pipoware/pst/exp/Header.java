package org.pipoware.pst.exp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck
 */
public class Header {

  public static final byte NDB_CRYPT_NONE = 0x00;
  public static final byte NDB_CRYPT_PERMUTE = 0x01;
  public static final byte NDB_CRYPT_CYCLIC = 0x02;

  private IPSTFile pst = null;
  private final int dwMagic;
  private final int wMagicClient;

  private final byte[] DW_MAGIC_BYTE = {0x21, 0x42, 0x44, 0x4E};
  private final int DW_MAGIC = ByteBuffer.wrap(DW_MAGIC_BYTE).order(ByteOrder.LITTLE_ENDIAN).getInt();

  private final byte[] DW_MAGIC_CLIENT_BYTE = {0x53, 0x4D};
  private final short DW_MAGIC_CLIENT = ByteBuffer.wrap(DW_MAGIC_CLIENT_BYTE).order(ByteOrder.LITTLE_ENDIAN).getShort();

  private int dwCRCPartial;
  private static final int CRC_PARTIAL_BEGIN_OFFSET = 8;
  private static final int CRC_PARTIAL_DATA_SIZE = 471;
  private static final int CRC_FULL_DATA_SIZE = 516;
  private static final int CRC_FULL_BEGIN_OFFSET = 8;

  private static final int ULONG_SIZE = 8;

  private short wVer;
  private short wVerClient;
  private byte bPlatformCreate;
  private byte bPlatformAccess;
  private int dwReserved1;
  private int dwReserved2;
  long bidUnused;
  long bidNextP;
  long bidNextB;

  int rgnid[] = new int[32];
  private long qwUnused;
  private Root root;
  private int dwAlign;
  private final byte rgbFM[] = new byte[128];
  private final byte rgbFP[] = new byte[128];
  private byte bSentinel;
  private byte bCryptMethod;
  private short rgbReserved;
  private int dwCRCFull;
  private long ullReserved;
  private int dwReservedANSI;
  private final byte rgbReserved2[] = new byte[3];
  ;
  private byte bReserved;
  private final byte rgbReserved3[] = new byte[32];

  public byte[] getRgbFM() {
    return rgbFM;
  }

  public byte[] getRgbFP() {
    return rgbFP;
  }

  public int getwMagicClient() {
    return wMagicClient;
  }

  public int getDwReserved1() {
    return dwReserved1;
  }

  public int getDwReserved2() {
    return dwReserved2;
  }

  public Root getRoot() {
    return root;
  }

  public long getBidUnused() {
    return bidUnused;
  }

  public long getBidNextP() {
    return bidNextP;
  }

  public long getBidNextB() {
    return bidNextB;
  }

  public int getDwUnique() {
    return dwUnique;
  }

  public PST_TYPE getType() {
    return type;
  }
  private int dwUnique;

  public enum PST_TYPE {

    ANSI, UNICODE
  };
  private PST_TYPE type;

  public Header(IPSTFile pst) throws IOException {
    this.pst = pst;

    pst.position(0);

    dwMagic = pst.readDWORD();

    if (dwMagic != DW_MAGIC) {
      throw new IllegalArgumentException("Illegal Magic value : " + dwMagic + ", expected : " + DW_MAGIC);
    }

    dwCRCPartial = pst.readDWORD();
    int dwComputedCRCPartial = computeCRCPartial();

    if (dwCRCPartial != dwComputedCRCPartial) {
      throw new IllegalArgumentException("Computed CRCPartial value : " + dwComputedCRCPartial + " not equals to dwCRCPartial : " + dwCRCPartial);
    }

    wMagicClient = pst.readWORD();
    if (wMagicClient != DW_MAGIC_CLIENT) {
      throw new IllegalArgumentException("Illegal wMagicClient value : " + wMagicClient + ", expected : " + DW_MAGIC_CLIENT);
    }

    wVer = pst.readWORD();
    if ((wVer == 14) || (wVer == 15)) {
      type = PST_TYPE.ANSI;
    } else if (wVer == 23) {
      type = PST_TYPE.UNICODE;
    } else {
      throw new IllegalArgumentException("Illegal wVer value: " + wVer + ", expected : 14,15 or 23");
    }

    wVerClient = pst.readWORD();
    bPlatformCreate = pst.readBYTE();
    bPlatformAccess = pst.readBYTE();
    dwReserved1 = pst.readDWORD();
    dwReserved2 = pst.readDWORD();

    if (type == PST_TYPE.UNICODE) {
      bidUnused = pst.readULONG();
      bidNextP = pst.readULONG();
    } else if (type == PST_TYPE.ANSI) {
      bidNextP = pst.readDWORD();
      bidNextB = pst.readDWORD();
    }

    dwUnique = pst.readDWORD();

    for (int i = 0; i < rgnid.length; i++) {
      rgnid[i] = pst.readDWORD();
    }

    qwUnused = pst.readULONG();
    root = new Root(pst, type);

    if (type == PST_TYPE.UNICODE) {
      dwAlign = pst.readDWORD();
    }

    pst.read(rgbFM);
    pst.read(rgbFP);

    bSentinel = pst.readBYTE();
    if (bSentinel != (byte) 0x80) {
      throw new IllegalArgumentException("Illegal bSentinel value: " + bSentinel + ", expected : 0x80");
    }

    bCryptMethod = pst.readBYTE();
    if (!((bCryptMethod == NDB_CRYPT_NONE)
      || (bCryptMethod == NDB_CRYPT_PERMUTE)
      || (bCryptMethod == NDB_CRYPT_CYCLIC))) {
      throw new IllegalArgumentException("Illegal bCryptMethod value: " + bCryptMethod + ", expected : 0x00, 0x01 or 0x02");
    }
    
    if (bCryptMethod ==  NDB_CRYPT_CYCLIC) {
      throw new UnsupportedOperationException("NDB CRYPT METHOD NDB_CRYPT_CYCLIC not yet implemented.");
    }

    rgbReserved = pst.readWORD();

    if (type == PST_TYPE.UNICODE) {
      bidNextB = pst.readULONG();
    }

    if (type == PST_TYPE.UNICODE) {
      dwCRCFull = pst.readDWORD();
      int dwCRCFullComputed = computeCRCFull();
      if (dwCRCFull != dwCRCFullComputed) {
        throw new IllegalArgumentException("Incorrect dwCRCFull computed : " + dwCRCFullComputed + ", expected : " + dwCRCFull);
      }
    }

    if (type == PST_TYPE.ANSI) {
      ullReserved = pst.readULONG();
      dwReservedANSI = pst.readDWORD();
    }

    pst.read(rgbReserved2);

    bReserved = pst.readBYTE();

    pst.read(rgbReserved3);
  }

  private int computeCRCPartial() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(CRC_PARTIAL_DATA_SIZE);
    pst.read(buffer, CRC_PARTIAL_BEGIN_OFFSET);
    return CRC.computeCRC(0, buffer.array());
  }

  private int computeCRCFull() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(CRC_FULL_DATA_SIZE);
    pst.read(buffer, CRC_PARTIAL_BEGIN_OFFSET);
    return CRC.computeCRC(0, buffer.array());
  }

  public int getDwMagic() {
    return dwMagic;
  }

  public int getDwCRCPartial() {
    return dwCRCPartial;
  }

  public short getwVer() {
    return wVer;
  }

  public short getwVerClient() {
    return wVerClient;
  }

  public byte getbPlatformCreate() {
    return bPlatformCreate;
  }

  public byte getbPlatformAccess() {
    return bPlatformAccess;
  }

  public int[] getRgnid() {
    return rgnid;
  }

}
