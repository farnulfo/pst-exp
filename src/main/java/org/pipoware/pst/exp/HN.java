/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 *
 * @author Franck Arnulfo
 */
public class HN {
  
  public static final byte CLIENT_SIG_RESERVED1 = 0x6C;
  public static final byte CLIENT_SIG_TC = 0x7C;
  public static final byte CLIENT_SIG_RESERVED2 = (byte) 0x8C;
  public static final byte CLIENT_SIG_RESERVED3 = (byte) 0x9C;
  public static final byte CLIENT_SIG_RESERVED4 = (byte) 0xA5;
  public static final byte CLIENT_SIG_RESERVED5 = (byte) 0xAC;
  public static final byte CLIENT_SIG_BTH = (byte) 0xB5;
  public static final byte CLIENT_SIG_PC_BTH = (byte) 0xBC;
  public static final byte CLIENT_SIG_RESERVED6 = (byte) 0xCC;

  private final byte[] data;
  short ibHnpm;
  public final byte bSig;
  public final byte bClientSig;
  public final int hidUserRoot;
  public final int rgbFillLevel;
  public final short cAlloc;
  public final short cFree;
  public final short[] rgibAlloc;
  public final NDB ndb;

  public HN(NDB ndb, byte[] bytes) {
    this.ndb = ndb;
    data = Arrays.copyOf(bytes, bytes.length);

    // Read HNHDR
    ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    ibHnpm = bb.getShort();
    bSig = bb.get();
    bClientSig = bb.get();
    hidUserRoot = bb.getInt();
    rgbFillLevel = bb.getInt();

    // Forward to HNPAGEMAP
    bb.position(ibHnpm);
    cAlloc = bb.getShort();
    cFree = bb.getShort();

    rgibAlloc = new short[cAlloc + 1];
    for (int i = 0; i < cAlloc + 1; i++) {
      rgibAlloc[i] = bb.getShort();
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("ibHnpm", "0x" + Integer.toHexString(Short.toUnsignedInt(ibHnpm)))
      .add("bSig", "0x" + Integer.toHexString(Byte.toUnsignedInt(bSig)))
      .add("bClientSig", "0x" + Integer.toHexString(Byte.toUnsignedInt(bClientSig)))
      .add("hidUserRoot", "0x" + Integer.toHexString(hidUserRoot) + " " + HID.toString(hidUserRoot))
      .add("rgbFillLevel", "0x" + Integer.toHexString(rgbFillLevel))
      .add("cAlloc", "0x" + Integer.toHexString(Short.toUnsignedInt(cAlloc)))
      .add("cFree", "0x" + Integer.toHexString(Short.toUnsignedInt(cFree)))
      .add("rgiAlloc", Arrays.toString(rgibAlloc))
      .toString();
  }

  byte[] getHeapItem(HID hid) {
    int hidIndex = hid.hidIndex;
    Preconditions.checkArgument(hidIndex > 0, "hidIndex (%s) must be > 0 ", hidIndex);
    Preconditions.checkArgument(hidIndex < rgibAlloc.length + 1, "hidIndex (%s) out of rgiAlloc size (%s)", hidIndex, rgibAlloc.length);
    
    int rgiAllocIndex = hidIndex - 1;
    return Arrays.copyOfRange(data, rgibAlloc[rgiAllocIndex], rgibAlloc[rgiAllocIndex+1]);
  }
}
