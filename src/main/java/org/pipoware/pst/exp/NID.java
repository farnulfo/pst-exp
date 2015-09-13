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
public class NID {

  private final long data;
  private final long nidType;
  
  public NID(long data) {
    this.data = data;
    nidType =  data & 0x1F;  
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nidType", "0x" + Long.toHexString(nidType))
      .toString();
  }
}
