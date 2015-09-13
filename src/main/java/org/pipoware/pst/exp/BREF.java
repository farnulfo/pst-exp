/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck
 */
class BREF {

  private final long bid;
  private final long ib;

  BREF(long bid, long ib) {
    this.bid = bid;
    this.ib = ib;
  }

  public long getBid() {
    return bid;
  }

  public long getIb() {
    return ib;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bid", "0x" + Long.toHexString(bid))
      .add("ib", ib)
      .toString();
  }
}
