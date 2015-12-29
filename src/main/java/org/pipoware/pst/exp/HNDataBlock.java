package org.pipoware.pst.exp;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Franck Arnulfo
 */
public class HNDataBlock {

  public enum Type {

    HNHDR, HNPAGEHDR, HNBITMAPHDR
  }

  public final Type type;
  private final byte[] data;

  short ibHnpm;
  public byte bSig;
  public byte bClientSig;
  public int hidUserRoot;
  public int rgbFillLevel;
  public final short cAlloc;
  public final short cFree;
  public final short[] rgibAlloc;
  public byte[] rgbFillLevel64 = null;

  public HNDataBlock(int index, byte[] aData) {
    this.data = aData;
    ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

    type = getType(index);
    Preconditions.checkArgument(type == Type.HNBITMAPHDR || type == Type.HNHDR || type == Type.HNPAGEHDR);

    if (type == Type.HNHDR) {
      // Read HNHDR
      ibHnpm = bb.getShort();
      bSig = bb.get();
      bClientSig = bb.get();
      hidUserRoot = bb.getInt();
      rgbFillLevel = bb.getInt();
    } else if (type == Type.HNPAGEHDR) {
      ibHnpm = bb.getShort();
    } else if (type == Type.HNBITMAPHDR) {
      ibHnpm = bb.getShort();
      rgbFillLevel64 = new byte[64];
      bb.get(rgbFillLevel64);
    }

    // Forward to HNPAGEMAP
    bb.position(ibHnpm);
    cAlloc = bb.getShort();
    cFree = bb.getShort();

    rgibAlloc = new short[cAlloc + 1];
    for (int i = 0; i < cAlloc + 1; i++) {
      rgibAlloc[i] = bb.getShort();
    }
  }

  public static Type getType(int anIndex) {
    if (anIndex == 0) {
      return Type.HNHDR;
    } else if (anIndex == 8) {
      return Type.HNBITMAPHDR;
    } else if ((anIndex > 8) && ((anIndex - 8) % 128 == 0)) {
      return Type.HNBITMAPHDR;
    } else {
      return Type.HNPAGEHDR;
    }
  }
  
  byte[] getHeapItem(HID hid) {
    int hidIndex = hid.hidIndex;
    Preconditions.checkArgument(hidIndex > 0, "hidIndex (%s) must be > 0 ", hidIndex);
    Preconditions.checkArgument(hidIndex < rgibAlloc.length + 1, "hidIndex (%s) out of rgiAlloc size (%s)", hidIndex, rgibAlloc.length);
    
    int rgiAllocIndex = hidIndex - 1;
    return Arrays.copyOfRange(data, rgibAlloc[rgiAllocIndex], rgibAlloc[rgiAllocIndex+1]);
  }
}
