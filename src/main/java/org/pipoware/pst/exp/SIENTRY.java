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
class SIENTRY {

  public final long nid;
  public final long bid;

  public SIENTRY(long nid, long bid) {
    this.nid = nid;
    this.bid = bid;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nid", new NID(nid))
      .add("bid", bid)
      .toString();

  }
}
