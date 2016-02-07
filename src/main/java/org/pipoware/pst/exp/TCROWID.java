package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck Arnulfo
 */
public class TCROWID {

  public final int dwRowID;
  public final int dwRowIndex;

  public TCROWID(int dwRowID, int dwRowIndex) {
    this.dwRowID = dwRowID;
    this.dwRowIndex = dwRowIndex;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("dwRowID", "0x" + Long.toHexString(dwRowID))
      .add("dwRowIndex", dwRowIndex)
      .toString();
  }
}
