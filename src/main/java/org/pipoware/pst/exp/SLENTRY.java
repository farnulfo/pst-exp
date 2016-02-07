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
  public int hashCode() {
    int hash = 5;
    hash = 31 * hash + (int) (this.nid ^ (this.nid >>> 32));
    hash = 31 * hash + (int) (this.bidData ^ (this.bidData >>> 32));
    hash = 31 * hash + (int) (this.bidSub ^ (this.bidSub >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SLENTRY other = (SLENTRY) obj;
    if (this.nid != other.nid) {
      return false;
    }
    if (this.bidData != other.bidData) {
      return false;
    }
    if (this.bidSub != other.bidSub) {
      return false;
    }
    return true;
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
