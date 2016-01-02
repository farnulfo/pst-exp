package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franck Arnulfo
 */
public class BTH {
  
  public final HN hn;
  private final BTHHEADER bthHeader;
  public final List<KeyData> keyDatas = new ArrayList<>();
  
  public BTH(HN aHN) {
    this(aHN, aHN.getHidUserRoot());
  }    
  
  public BTH(HN aHN, int hidOfBTHHEADER) {
    Preconditions.checkArgument(
      aHN.getClientSig() == HN.CLIENT_SIG_PC_BTH ||
      aHN.getClientSig() == HN.CLIENT_SIG_TC,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.getClientSig())));
    this.hn = aHN;
    
    HID hid = new HID(hidOfBTHHEADER);
    
    byte[] heapItem = hn.getHeapItem(hid);
    bthHeader = new BTHHEADER(heapItem);
    handleLevel(bthHeader.bIdxLevels, bthHeader.hidRoot);
  }

  private void handleLevel(int level, int hid) {
    if (level == 0) {
      byte[] b = hn.getHeapItem(new HID(hid));
      ByteBuffer bb = ByteBuffer.wrap(b);
      Preconditions.checkArgument(b.length % (bthHeader.cbKey + bthHeader.cbEnt) == 0);

      int nbRecords = b.length / (bthHeader.cbKey + bthHeader.cbEnt);
      for (int i = 0; i < nbRecords; i++) {
        byte[] key = new byte[bthHeader.cbKey];
        bb.get(key);
        byte[] data = new byte[bthHeader.cbEnt];
        bb.get(data);
        KeyData keyData = new KeyData(key, data);
        keyDatas.add(keyData);
      }
    } else {
      byte[] b = hn.getHeapItem(new HID(hid));
      ByteBuffer bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
      // Level > 0 only contains only HID as data
      Preconditions.checkArgument(b.length % (bthHeader.cbKey + HID.SIZE) == 0);

      int nbRecords = b.length / (bthHeader.cbKey + HID.SIZE);
      for (int i = 0; i < nbRecords; i++) {
        byte[] key = new byte[bthHeader.cbKey];
        bb.get(key);
        int subLevelHid = bb.getInt();
        handleLevel(level - 1, subLevelHid);
      }
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
