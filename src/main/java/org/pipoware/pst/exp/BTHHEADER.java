package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck Arnulfo
 */
public class BTHHEADER {
  public final byte bType;
  public final byte cbKey;
  public final byte cbEnt;
  public final byte bIdxLevels;
  public final int hidRoot;

  BTHHEADER(byte[] heapItem) {
    Preconditions.checkArgument(heapItem.length == 8);
    ByteBuffer bb = ByteBuffer.wrap(heapItem).order(ByteOrder.LITTLE_ENDIAN);
    bType = bb.get();
    cbKey = bb.get();
    cbEnt = bb.get();
    bIdxLevels = bb.get();
    hidRoot = bb.getInt();
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bType", "0x" + Integer.toHexString(Byte.toUnsignedInt(bType)))
      .add("cbKey", cbKey)
      .add("cbEnt", cbEnt)
      .add("bIdxLevels", bIdxLevels)
      .add("hidRoot", new HID(hidRoot))
      .toString();
  }
}
