package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

  public PCItem(BTH bth, KeyData keyData, NDB ndb, NBTENTRY nbtentry) throws IOException {
    Preconditions.checkArgument(keyData.key.length == 2, "Incorrect cbKey size (%s) for a PC Item", keyData.key.length);
    Preconditions.checkArgument(keyData.data.length == 6, "Incorrect cbEnt size (%s) for a PC Item", keyData.data.length);

    ByteBuffer bb = ByteBuffer.wrap(keyData.key).order(ByteOrder.LITTLE_ENDIAN);
    short wPropId = bb.getShort();
    propertyIdentifier = wPropId;

    propertyData = Arrays.copyOf(keyData.data, keyData.data.length);
    
    bb = ByteBuffer.wrap(keyData.data).order(ByteOrder.LITTLE_ENDIAN);
    short wPropType = bb.getShort();
    propertyDataType = PropertyDataType.get(wPropType);
    
    int dwValueHnid = bb.getInt();
    
    if (propertyDataType.isVariableSize()) {
      hid = new HID(dwValueHnid);
      
      if (hid.type == NID.NID_TYPE_HID) {
        if (dwValueHnid != 0) {
          dataValue = bth.hn.getHeapItem(hid);
        } else {
          System.out.println("dwValueHnid == null");
        }
      } else {
        // dwValueHnid = NID (subnode)
        long blockId = nbtentry.bidSub;
        Block block = ndb.getBlockFromBID(blockId);
        Preconditions.checkArgument(block.blockType == BlockType.SLBLOCK, "Not yet supported block type : " + block.blockType);
        for (SLENTRY slEntry : block.rgentries_slentry) {
          if (dwValueHnid == slEntry.nid) {
            Block b = ndb.getBlockFromBID(slEntry.bidData);
            byte bCryptMethod = ndb.pst.getHeader().getBCryptMethod();
            Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
            if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
              PermutativeEncoding.decode(b.data);
            }
            dataValue = Arrays.copyOf(b.data, b.data.length);
          }
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
            int32 = dwValueHnid;
            break;
          default:
            throw new IllegalArgumentException("Unhandled PropertyDataType : " + propertyDataType);
        }
      }
    }
    
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
