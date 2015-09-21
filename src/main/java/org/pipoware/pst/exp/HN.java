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

/**
 *
 * @author Franck Arnulfo
 */
public class HN {

  private final byte[] data;
  short ibHnpm;
  public final byte bSig;
  public final byte bClientSig;
  public final int hidUserRoot;
  public final int rgbFillLevel;
  public final short cAlloc;
  public final short cFree;
  public final short[] rgibAlloc;

  public HN(byte[] bytes) {
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
      .add("hidUserRoot", "0x" + Integer.toHexString(hidUserRoot))
      .add("rgbFillLevel", "0x" + Integer.toHexString(rgbFillLevel))
      .add("cAlloc", "0x" + Integer.toHexString(Short.toUnsignedInt(cAlloc)))
      .add("cFree", "0x" + Integer.toHexString(Short.toUnsignedInt(cFree)))
      .add("rgiAlloc", Arrays.toString(rgibAlloc))
      .toString();
  }
}
