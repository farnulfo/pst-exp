package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck Arnulfo
 */
public class TCOLDESC {
  
  public final int tag;
  public final short ibData;
  public final byte cbData;
  public final byte iBit;

  TCOLDESC(int tag, short ibData, byte cbData, byte iBit) {
    this.tag = tag;
    this.ibData = ibData;
    this.cbData = cbData;
    this.iBit = iBit;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("tag", "0x" + Integer.toHexString(tag))
      .add("ibData", ibData)
      .add("cbData", cbData)
      .add("iBit", iBit)
      .toString();
  }  
}
