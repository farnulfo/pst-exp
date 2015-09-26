package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Franck Arnulfo
 */
public class PCITem {

  private final PropertyDataType propertyDataType;
  private final short propertyIdentifier;
  private final byte[] propertyData;
  private int int32;
  private HID hid;
  private boolean bool;
  private byte[] dataValue;

  public PCITem(BTH bth, KeyData keyData) {
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
        dataValue = bth.hn.getHeapItem(hid.hidIndex);
      } else {
        throw new UnsupportedOperationException("dwValueHnid with NID not yet implemented !");
      }
      
    } else {
      if (propertyDataType.getFixedDataSizeInByte() > 4) {
        hid = new HID(dwValueHnid);
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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("propertyIdentifier", Integer.toHexString(Short.toUnsignedInt(propertyIdentifier)))
      .add("propertyData", "[" + BaseEncoding.base16().withSeparator(",", 2).encode(propertyData) + "]")
      .add("propertyDataType", propertyDataType)
      .add("int32", int32)
      .add("bool", bool)
      .add("hid", hid)
      .add("dataValue", "[" + (dataValue != null ? BaseEncoding.base16().withSeparator(",", 2).encode(dataValue) : "null")+ "]")
      .toString();
  }
}
