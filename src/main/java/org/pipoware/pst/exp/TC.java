package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 *
 * @author Franck Arnulfo
 */
public class TC {

  private final HN hn;
  private final TCINFO tcinfo;
  private final BTHHEADER bthHeaderRowIndex;

  public TC(HN aHN) {
    Preconditions.checkArgument(
      aHN.bClientSig == HN.CLIENT_SIG_TC,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.bClientSig)));
    this.hn = aHN;

    HID hid = new HID(hn.hidUserRoot);

    byte[] heapItem = hn.getHeapItem(hid.hidIndex);
    tcinfo = new TCINFO(heapItem);
    
    heapItem = hn.getHeapItem(new HID(tcinfo.hidRowIndex).hidIndex);
    bthHeaderRowIndex = new BTHHEADER(heapItem);

  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hn", hn)
      .add("tcinfo", tcinfo)
      .add("bthheader", bthHeaderRowIndex)
      .toString();
  }
}
