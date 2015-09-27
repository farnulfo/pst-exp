package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Franck Arnulfo
 */
public class TC {

  private final HN hn;
  private final TCINFO tcinfo;
  private final BTHHEADER bthHeaderRowIndex;
  private final List<TCROWID> tcRowIds;

  public TC(HN aHN) {
    Preconditions.checkArgument(
      aHN.bClientSig == HN.CLIENT_SIG_TC,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.bClientSig)));
    this.hn = aHN;

    HID hid = new HID(hn.hidUserRoot);

    byte[] heapItem = hn.getHeapItem(hid);
    tcinfo = new TCINFO(heapItem);
    
    heapItem = hn.getHeapItem(new HID(tcinfo.hidRowIndex));
    bthHeaderRowIndex = new BTHHEADER(heapItem);
    
    heapItem = hn.getHeapItem(new HID(bthHeaderRowIndex.hidRoot));
    
    ByteBuffer bb = ByteBuffer.wrap(heapItem).order(ByteOrder.LITTLE_ENDIAN);
    Preconditions.checkArgument(heapItem.length % (bthHeaderRowIndex.cbKey + bthHeaderRowIndex.cbEnt) == 0);
    
    tcRowIds = new ArrayList<>();
    int nbRecords = heapItem.length / (bthHeaderRowIndex.cbKey + bthHeaderRowIndex.cbEnt);
    Preconditions.checkArgument((bthHeaderRowIndex.cbEnt == 4) || (bthHeaderRowIndex.cbEnt == 2), "Invalid cbEnt (%s) for a TC BTH", bthHeaderRowIndex.cbEnt);
    for (int i = 0; i < nbRecords; i++) {
      int dwRowID = bb.getInt();
      
      int dwRowIndex;
      if (bthHeaderRowIndex.cbEnt == 4) {
        dwRowIndex = bb.getInt();
      } else {
        dwRowIndex = bb.getShort();
      }
      
      tcRowIds.add(new TCROWID(dwRowID, dwRowIndex));
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hn", hn)
      .add("tcinfo", tcinfo)
      .add("bthheader", bthHeaderRowIndex)
      .add("tcRowIds", tcRowIds.toString())
      .toString();
  }
}
