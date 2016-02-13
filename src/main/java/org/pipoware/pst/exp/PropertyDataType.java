package org.pipoware.pst.exp;

import com.google.common.base.Preconditions;
import java.util.EnumSet;

/**
 * [MS-OXCDATA].pdf, 2.11.1 Property Data Types
 * 
 * 
 * @author Franck Arnulfo
 */
public enum PropertyDataType {

  PtypInteger16((short) 0x0002, false, 2),
  PtypInteger32((short) 0x0003, false, 4),
  PtypFloating32((short) 0x0004, false, 4),
  PtypFloating64((short) 0x0005, false, 8),
  PtypCurrency((short) 0x0006, false, 8),
  PtypFloatingTime((short) 0x0007, false, 8),
  PtypErrorCode((short) 0x000A, false, 4),
  PtypBoolean((short) 0x000B, false, 1),
  PtypInteger64((short) 0x0014, false, 8),
  PtypString((short) 0x001F, true, 0),
  PtypString8((short) 0x001E, true, 0),
  PtypTime((short) 0x0040, false, 8),
  PtypGuid((short) 0x0048, false, 16),
  PtypServerId((short) 0x00FB, true, 0),
  PtypRestriction((short) 0x00FD, true, 0),
  PtypRuleAction((short) 0x00FE, true, 0),
  PtypBinary((short) 0x0102, true, 0),
  PtypMultipleInteger16((short) 0x1002, true, 0),
  PtypMultipleInteger32((short) 0x1003, true, 0),
  PtypMultipleFloating32((short) 0x1004, true, 0),
  PtypMultipleFloating64((short) 0x1005, true, 0),
  PtypMultipleCurrency((short) 0x1006, true, 0),
  PtypMultipleFloatingTime((short) 0x1007, true, 0),
  PtypMultipleInteger64((short) 0x1014, true, 0),
  PtypMultipleString((short) 0x101F, true, 0),
  PtypMultipleString8((short) 0x101E, true, 0),
  PtypMultipleTime((short) 0x1040, true, 0),
  PtypMultipleGuid((short) 0x1048, true, 0),
  PtypMultipleBinary((short) 0x1102, true, 0),
  PtypObject((short) 0x000D, true, 0);

  private final short propertyTypeValue;
  private final boolean variableSize;
  private final int fixedDataSizeInByte;

  private PropertyDataType(short propertyTypeValue, boolean variableSize, int fixedDataSizeInByte) {
    Preconditions.checkArgument(
      (variableSize && (fixedDataSizeInByte == 0))
      || (!variableSize && (fixedDataSizeInByte > 0)));
    this.propertyTypeValue = propertyTypeValue;
    this.variableSize = variableSize;
    this.fixedDataSizeInByte = fixedDataSizeInByte;
  }

  public boolean isVariableSize() {
    return variableSize;
  }

  public static PropertyDataType get(int propertyTypeValue) throws UnknownPropertyDataTypeException {
    for (PropertyDataType p : EnumSet.allOf(PropertyDataType.class)) {
      if (p.propertyTypeValue == propertyTypeValue) {
        return p;
      }
    }
    throw new UnknownPropertyDataTypeException("Unsupported PropertyDataType : 0x" + Integer.toHexString(propertyTypeValue), propertyTypeValue);
  }

  int getFixedDataSizeInByte() {
    return fixedDataSizeInByte;
  }

}
