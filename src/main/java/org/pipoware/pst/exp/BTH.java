package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franck Arnulfo
 */
public class BTH {
  
  public final HN hn;
  private final BTHHEADER bthHeader;
  public List<KeyData> keyDatas = new ArrayList<>();
  
  public BTH(HN aHN) {
    Preconditions.checkArgument(
      aHN.bClientSig == HN.CLIENT_SIG_PC_BTH,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.bClientSig)));
    this.hn = aHN;
    
    HID hid = new HID(hn.hidUserRoot);
    
    byte[] heapItem = hn.getHeapItem(hid);
    bthHeader = new BTHHEADER(heapItem);
    
    if (bthHeader.bIdxLevels != 0) {
      throw new UnsupportedOperationException("Only bIdxLevels = 0 for now.");
    }
    
    byte[] b = hn.getHeapItem(new HID(bthHeader.hidRoot));
    ByteBuffer bb = ByteBuffer.wrap(b);
    Preconditions.checkArgument(b.length % (bthHeader.cbKey + bthHeader.cbEnt) == 0);
    
    keyDatas = new ArrayList<>();
    int nbRecords = b.length / (bthHeader.cbKey + bthHeader.cbEnt);
    for (int i = 0; i < nbRecords; i++) {
      byte[] key = new byte[bthHeader.cbKey];
      bb.get(key);
      byte[] data = new byte[bthHeader.cbEnt];
      bb.get(data);
      KeyData keyData = new KeyData(key, data);
      keyDatas.add(keyData);
    }
    
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hn", hn)
      .add("bthHeader", bthHeader)
      .add("records", keyDatas)
      .toString();
  }
}
