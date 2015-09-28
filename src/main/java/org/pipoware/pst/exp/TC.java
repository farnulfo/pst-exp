package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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
    
    HID hidRowsData = new HID(tcinfo.hnidRows);
    heapItem = hn.getHeapItem(hidRowsData);

    Preconditions.checkArgument((heapItem.length / tcRowIds.size()) == tcinfo.TCI_bm);

    System.out.println("0x00000000\t" + Strings.repeat("X", tcinfo.TCI_bm));
    for (TCOLDESC tColDesc : tcinfo.tColDesc) {
      System.out.print("0x" + Integer.toHexString(tColDesc.tag) + "\t");
      System.out.print(Strings.repeat(" ", tColDesc.ibData));
      System.out.println(Strings.repeat("X", tColDesc.cbData));
    }

    int sizeAll = 0;
    for (TCOLDESC tColDesc : tcinfo.tColDesc) {
      sizeAll += tColDesc.cbData;
    }
    int rgbCEBSize = tcinfo.TCI_bm - sizeAll;
    int rgbCEBComputedSize = (int) Math.ceil((double) tcinfo.cCols / 8);
    Preconditions.checkArgument(rgbCEBSize == rgbCEBComputedSize);

    for (int i = 0; i < tcRowIds.size(); i++) {
      byte[] rowData = Arrays.copyOfRange(heapItem, i * tcinfo.TCI_bm, i * tcinfo.TCI_bm + tcinfo.TCI_bm);
      System.out.println("row data : [" + BaseEncoding.base16().withSeparator(",", 2).encode(rowData) + "]");
      bb = ByteBuffer.wrap(rowData).order(ByteOrder.LITTLE_ENDIAN);
      int dwRowId = bb.getInt();

      System.out.println(tcRowIds.get(i));
      for (TCOLDESC tColDesc : tcinfo.tColDesc) {
        bb.position(tColDesc.ibData);
        byte[] data = new byte[tColDesc.cbData];
        bb.get(data);
        System.out.println("\t" + tColDesc);
        System.out.println("\t\t[" + BaseEncoding.base16().withSeparator(",", 2).encode(data) + "]");
      }
      
      bb.position(tcinfo.TCI_1b);
      byte[] CEBArray = new byte[rgbCEBSize];
      bb.get(CEBArray);
      System.out.println("CEBArray : [" + BaseEncoding.base16().withSeparator(",", 2).encode(CEBArray) + "]");
      for (byte b1 : CEBArray) {
        System.out.print(String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0') + " ");
      }
      System.out.println();
      BitSet bitSet = BitSet.valueOf(CEBArray);
      for (int k = 0; k < bitSet.size(); k++) {
        System.out.println("BitSet(" + k + ") = " + bitSet.get(k));
      }
      
      for (TCOLDESC tColDesc : tcinfo.tColDesc) {
        byte iBit = tColDesc.iBit;
        byte a1 = CEBArray[iBit / 8];
        byte b1 = (byte) (1 << (7 - (iBit % 8)));
        boolean fCEB = (a1 & b1) != 0;
        System.out.println("iBit " + iBit + " = " + fCEB);
      }
      
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
