package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck Arnulfo
 */
public class HID {
  
  public final int hid;
  public final int type;
  public final int hidIndex;
  public final int hidBlockIndex;
  public static final int SIZE = 4;
  
  public HID(int hid) {
    this.hid = hid;
    type = hid & 0b11111;
    hidIndex = (hid & 0b1111111111100000) >>> 5;
    hidBlockIndex = (hid & 0b11111111111111110000000000000000) >>> 16;
  }
  
  public static String toString(int hid) {
    return new HID(hid).toString();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hid", "0x" + Integer.toHexString(hid))
      .add("type", type)
      .add("hidIndex", hidIndex)
      .add("hidBlockIndex", hidBlockIndex)
      .toString();
  }
}
