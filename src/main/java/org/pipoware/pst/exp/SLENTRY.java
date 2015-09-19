/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck Arnulfo
 */
class SLENTRY {

  public final long nid;
  public final long bidData;
  public final long bidSub;

  SLENTRY(long nid, long bidData, long bidSub) {
    this.nid = nid;
    this.bidData = bidData;
    this.bidSub = bidSub;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nid", new NID(nid))
      .add("bidData", BID.toString(bidData))
      .add("bidSub", BID.toString(bidSub))
      .toString();

  }
}
