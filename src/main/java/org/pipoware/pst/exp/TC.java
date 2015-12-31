package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pipoware.pst.exp.pages.NBTENTRY;

/**
 *
 * @author Franck Arnulfo
 */
public class TC {

  private final HN hn;
  private final TCINFO tcinfo;
  private final BTHHEADER bthHeaderRowIndex;
  private final List<TCROWID> tcRowIds;
  private final NBTENTRY nbtentry;

  public TC(HN aHN, NBTENTRY aNBTENTRY) throws IOException {
    Preconditions.checkArgument(
      aHN.getClientSig() == HN.CLIENT_SIG_TC,
      "Unsupported bClientSig 0x%s", Integer.toHexString(Byte.toUnsignedInt(aHN.getClientSig())));
    this.hn = aHN;
    this.nbtentry = aNBTENTRY;

    HID hid = new HID(hn.getHidUserRoot());

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
    
    byte[] rowMatrixData;
    HID hnidRows = new HID(tcinfo.hnidRows);
    if (hnidRows.type == NID.NID_TYPE_HID) {
      rowMatrixData = hn.getHeapItem(hnidRows);
    } else {
      Block block = hn.ndb.getBlockFromBID(nbtentry.bidSub);
      Preconditions.checkArgument(block.blockType == Block.BlockType.SLBLOCK, "Not yet supported BlockType %s", block.blockType);
      SLENTRY slentry = null;
      for (SLENTRY s : block.rgentries_slentry) {
        if (s.nid == tcinfo.hnidRows) {
          slentry = s;
        }
      }
      Preconditions.checkNotNull(slentry, "SLENTRY not found");
      Block b = hn.ndb.getBlockFromBID(slentry.bidData);
      System.out.println("Block : " + b);
      Preconditions.checkArgument(b.blockType == Block.BlockType.DATA_BLOCK, "Blocktype %s not yet handled!", b.blockType);
      byte bCryptMethod = hn.ndb.pst.getHeader().getBCryptMethod();
      Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
      if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
        PermutativeEncoding.decode(b.data);
      }
      rowMatrixData = b.data;
    }

    Preconditions.checkArgument((rowMatrixData.length / tcRowIds.size()) == tcinfo.TCI_bm);

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
      byte[] rowData = Arrays.copyOfRange(rowMatrixData, i * tcinfo.TCI_bm, i * tcinfo.TCI_bm + tcinfo.TCI_bm);
      System.out.println("row data : [" + BaseEncoding.base16().withSeparator(",", 2).encode(rowData) + "]");
      bb = ByteBuffer.wrap(rowData).order(ByteOrder.LITTLE_ENDIAN);
      int dwRowId = bb.getInt();

      bb.position(tcinfo.TCI_1b);
      byte[] CEBArray = new byte[rgbCEBSize];
      bb.get(CEBArray);
      System.out.println("CEBArray : [" + BaseEncoding.base16().withSeparator(",", 2).encode(CEBArray) + "]");
      for (byte b1 : CEBArray) {
        System.out.print(String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0') + " ");
      }
      System.out.println();
      
      for (TCOLDESC tColDesc : tcinfo.tColDesc) {
        byte iBit = tColDesc.iBit;
        byte a1 = CEBArray[iBit / 8];
        byte b1 = (byte) (1 << (7 - (iBit % 8)));
        boolean fCEB = (a1 & b1) != 0;
        System.out.println("iBit " + iBit + " = " + fCEB);
      }

      System.out.println(tcRowIds.get(i));
      for (TCOLDESC tColDesc : tcinfo.tColDesc) {
        bb.position(tColDesc.ibData);
        byte[] data = new byte[tColDesc.cbData];
        bb.get(data);
        System.out.println("\t" + tColDesc);
        System.out.println("\t\t row data : [" + BaseEncoding.base16().withSeparator(",", 2).encode(data) + "]");
        System.out.println("\t\t is null : " + isRowDataNull(CEBArray, tColDesc.iBit));
        
        if (!isRowDataNull(CEBArray, tColDesc.iBit)) {
          short wPropType = (short) (tColDesc.tag & 0x0000FFFF);
          PropertyDataType propertyDataType;
          try {
            propertyDataType = PropertyDataType.get(wPropType);
            System.out.println("\t\t propertyDataType : " + propertyDataType);
            System.out.println("\t\t value : [" + BaseEncoding.base16().withSeparator(",", 2).encode(getValue(propertyDataType, data, hn)) + "]");
            if (propertyDataType == PropertyDataType.PtypString) {
              System.out.println("\t\t String(value, UTF_16LE) : " + new String(getValue(propertyDataType, data, hn), StandardCharsets.UTF_16LE));
            }
          } catch (Exception e) {
            System.out.println(e);
          }
        }
        
      }
    }
  }
  
  public static boolean isRowDataNull(byte[] cebArray, byte iBit) {
    byte a1 = cebArray[iBit / 8];
    byte b1 = (byte) (1 << (7 - (iBit % 8)));
    return !((a1 & b1) != 0);
  }
  
  public List<TCROWID> getRows() {
    return tcRowIds;
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

  private byte[] getValue(PropertyDataType propertyDataType, byte[] data, HN hn) {
    ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    byte[] dataValue;
    
    if (propertyDataType.isVariableSize()) {
      Preconditions.checkArgument(data.length == 4);
      int dwValueHnid = bb.getInt();
      HID hid = new HID(dwValueHnid);
      
      if (hid.type == NID.NID_TYPE_HID) {
        dataValue = hn.getHeapItem(hid);
      } else {
        throw new UnsupportedOperationException("dwValueHnid with NID not yet implemented !");
      }
      
    } else {
      if (propertyDataType.getFixedDataSizeInByte() <= 8) {
        dataValue = Arrays.copyOf(data, data.length);
      } else {
        int dwValueHnid = bb.getInt();
        HID hid = new HID(dwValueHnid);
        Preconditions.checkArgument(hid.type == NID.NID_TYPE_HID);
        dataValue = hn.getHeapItem(hid);
      }
    }
    
    return dataValue;
  }
}
