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
    Preconditions.checkArgument(bType == HN.CLIENT_SIG_BTH);
    cbKey = bb.get();
    Preconditions.checkArgument(cbKey == 2 || cbKey == 4 || cbKey == 8 || cbKey == 16);
    cbEnt = bb.get();
    bIdxLevels = bb.get();
    Preconditions.checkArgument(bIdxLevels >= 0);
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
