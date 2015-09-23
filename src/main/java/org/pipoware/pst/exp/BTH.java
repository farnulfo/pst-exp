package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 *
 * @author Franck Arnulfo
 */
public class BTH {
  
  private final HN hn;
  private final BTHHEADER bthHeader;
  
  public BTH(HN aHN) {
    Preconditions.checkArgument(
      aHN.bClientSig == HN.CLIENT_SIG_PC_BTH,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.bClientSig)));
    this.hn = aHN;
    
    HID hid = new HID(hn.hidUserRoot);
    
    byte[] heapItem = hn.getHeapItem(hid.hidIndex);
    bthHeader = new BTHHEADER(heapItem);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hn", hn)
      .add("bthHeader", bthHeader)
      .toString();
  }
}
