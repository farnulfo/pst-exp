package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import org.pipoware.pst.exp.Block.BlockType;
import org.pipoware.pst.exp.pages.NBTENTRY;

/**
 *
 * @author Franck Arnulfo
 */
public class PCItem {

  private final PropertyDataType propertyDataType;
  public final short propertyIdentifier;
  private final byte[] propertyData;
  private int int32;
  private String string;
  private HID hid;
  private boolean bool;
  public byte[] dataValue;

  public PCItem(BTH bth, KeyData keyData, NDB ndb, NBTENTRY nbtentry) throws IOException, UnknownPropertyDataTypeException {
    Preconditions.checkArgument(keyData.key.length == 2, "Incorrect cbKey size (%s) for a PC Item", keyData.key.length);
    Preconditions.checkArgument(keyData.data.length == 6, "Incorrect cbEnt size (%s) for a PC Item", keyData.data.length);

    ByteBuffer bb = ByteBuffer.wrap(keyData.key).order(ByteOrder.LITTLE_ENDIAN);
    short wPropId = bb.getShort();
    propertyIdentifier = wPropId;

    propertyData = Arrays.copyOf(keyData.data, keyData.data.length);
    
    bb = ByteBuffer.wrap(keyData.data).order(ByteOrder.LITTLE_ENDIAN);
    short wPropType = bb.getShort();
    Preconditions.checkArgument(wPropType != 0x000D, "wPropType 0x000D (PtypObject) not yet supported, check MS-PST 2.3.3.5");
    propertyDataType = PropertyDataType.get(wPropType);
    
    int dwValueHnid = bb.getInt();
    
    if (propertyDataType.isVariableSize()) {
      hid = new HID(dwValueHnid);
      
      if (hid.type == NID.NID_TYPE_HID) {
        if (dwValueHnid != 0) {
          dataValue = bth.hn.getHeapItem(hid);
        } else {
          dataValue = null;
        }
      } else {
        // dwValueHnid = NID (subnode)
        long subBlockId = nbtentry.bidSub;
        Block subBlock = ndb.getBlockFromBID(subBlockId);
        Preconditions.checkArgument(subBlock.blockType == BlockType.SLBLOCK, "Not yet supported block type : " + subBlock.blockType);
        Block block = null;
        long blockId = 0;
        for (SLENTRY slEntry : subBlock.rgentries_slentry) {
          if (dwValueHnid == slEntry.nid) {
            blockId = slEntry.bidData;
            block = ndb.getBlockFromBID(blockId);
            break;
          }
        }
        if (block != null) {
          byte[] data = getBlockData(block, ndb);
          dataValue = Arrays.copyOf(data, data.length);
        } else {
          StringBuilder sb = new StringBuilder();
          sb.append("Block Id ").append(blockId).append(" not found, context : ")
            .append("dwValueHnid=").append(dwValueHnid)
            .append(",propertyIdentifier=").append(propertyIdentifier)
            .append(",propertyDataType=").append(propertyDataType);
          //throw new IllegalArgumentException(sb.toString());
          System.out.println(sb);
        }
      }
      if (propertyDataType == PropertyDataType.PtypString) {
        string = (dataValue != null) ? new String(dataValue, StandardCharsets.UTF_16LE) : "NULLLLLLL";
      } else if (propertyDataType == PropertyDataType.PtypString8) {
        string = (dataValue != null) ?  new String(dataValue, StandardCharsets.UTF_8) : "NULLLLLLL";
      }
    } else {
      if (propertyDataType.getFixedDataSizeInByte() > 4) {
        hid = new HID(dwValueHnid);
        dataValue = bth.hn.getHeapItem(hid);
      } else {
        switch (propertyDataType) {
          case PtypBoolean:
            bool = dwValueHnid != 0;
            break;
          case PtypInteger32:
          case PtypInteger16:
            int32 = dwValueHnid;
            break;
          default:
            throw new IllegalArgumentException("Unhandled PropertyDataType : " + propertyDataType);
        }
      }
    }
    
  }

  private byte[] getBlockData(Block block, NDB ndb) throws IOException {
    Preconditions.checkArgument(block.blockType == BlockType.DATA_BLOCK || block.blockType == BlockType.XBLOCK);
    byte bCryptMethod = ndb.pst.getHeader().getBCryptMethod();
    Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));

    if (block.blockType == BlockType.DATA_BLOCK) {
      if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
        PermutativeEncoding.decode(block.data);
      }
      byte data[] = block.data;
      return data;
    } else if (block.blockType == BlockType.XBLOCK) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      for (int i = 0; i < block.rgbid.length; i++) {
        long blockId = block.rgbid[i];
        Block subBlock = ndb.getBlockFromBID(blockId);
        Preconditions.checkArgument(subBlock.blockType == Block.BlockType.DATA_BLOCK);
        if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
          PermutativeEncoding.decode(subBlock.data);
        }
        baos.write(subBlock.data);
      }
      return baos.toByteArray();
    }
    return null;
  }
  
  public int getInt() {
    if (propertyDataType != PropertyDataType.PtypInteger32) {
      throw new IllegalArgumentException("propertyDataType : " + propertyDataType + " <> " + PropertyDataType.PtypInteger32);
    }
    return int32;
  }

  public String getString() {
    if ((propertyDataType == PropertyDataType.PtypString) || (propertyDataType == PropertyDataType.PtypString8)) {
      return string;
    } else {
      throw new IllegalArgumentException("propertyDataType : " + propertyDataType + " <> " + PropertyDataType.PtypString);
    }
  }
  
  public boolean getBoolean() {
    if (propertyDataType != PropertyDataType.PtypBoolean) {
      throw new IllegalArgumentException("propertyDataType : " + propertyDataType + " <> " + PropertyDataType.PtypBoolean);
    }
    return bool;    
  }
  
  /**
   * PTypeTime : MS-DTYP 2.3.3
   * 
   * @return Date
   */
  public Date getTime() {
    final long EPOCH_DIFF = 11644473600000L;
    Preconditions.checkArgument(propertyDataType == PropertyDataType.PtypTime, "propertyDataType : %s <> %s", propertyDataType, PropertyDataType.PtypTime);
    Preconditions.checkArgument(dataValue.length == 8);
    ByteBuffer bb = ByteBuffer.wrap(dataValue).order(ByteOrder.LITTLE_ENDIAN);
    long time = bb.getLong();
    final long ms_since_16010101 = time / (1000 * 10);
    final long ms_since_19700101 = ms_since_16010101 - EPOCH_DIFF;
    return new Date(ms_since_19700101);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("propertyIdentifier", Integer.toHexString(Short.toUnsignedInt(propertyIdentifier)))
      .add("propertyTag", PropertyTag.getPropertyTagFromIdentifier(propertyIdentifier))
      .add("propertyData", "[" + BaseEncoding.base16().withSeparator(",", 2).encode(propertyData) + "]")
      .add("propertyDataType", propertyDataType)
      .add("int32", int32)
      .add("bool", bool)
      .add("hid", hid)
      .add("dataValue", dataValue == null ? "null" : "(size=" + dataValue.length + ") [" + BaseEncoding.base16().withSeparator(",", 2).encode(dataValue)+ "]")
      .add("dataValue String", string)
      .toString();
  }
}
