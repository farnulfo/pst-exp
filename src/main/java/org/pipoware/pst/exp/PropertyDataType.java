package org.pipoware.pst.exp;

import com.google.common.base.Preconditions;
import java.util.EnumSet;

/**
 *
 * @author Franck Arnulfo
 */
public enum PropertyDataType {

  PtypInteger32((short) 0x0003, false, 4),
  PtypBoolean((short) 0x000B, false, 1),
  PtypString((short) 0x001F, true, 0),
  PtypBinary((short) 0x0102, true, 0);

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

  public static PropertyDataType get(int propertyTypeValue) {
    for (PropertyDataType p : EnumSet.allOf(PropertyDataType.class)) {
      if (p.propertyTypeValue == propertyTypeValue) {
        return p;
      }
    }
    throw new IllegalArgumentException("Unsupported PropertyDataType : " + Integer.toHexString(propertyTypeValue));
  }

  int getFixedDataSizeInByte() {
    return fixedDataSizeInByte;
  }

}
