/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck
 */
public class BBTENTRY {

  public final BREF bref;
  
  /**
   * The count of bytes of the raw data contained in the block 
   * referenced by BREF excluding the block trailer 
   * and alignment padding, if any.
   */
  public final short cb;
  
  /**
   * Reference count indicating the count of references to this block.
   * See section 2.2.2.7.7.3.1 regarding how reference counts work.
   */
  private final short cref;

  public BBTENTRY(byte[] entrybyte, Header.PST_TYPE type) {
    ByteBuffer bb = ByteBuffer.wrap(entrybyte).order(ByteOrder.LITTLE_ENDIAN);

    if (type == Header.PST_TYPE.UNICODE) {
      long bid = bb.getLong();
      long ib = bb.getLong();
      bref = new BREF(bid, ib);
      cb = bb.getShort();
      cref = bb.getShort();
      int dwPadding = bb.getInt();
    } else {
      long bid = bb.getInt();
      long ib = bb.getInt();
      bref = new BREF(bid, ib);
      cb = bb.getShort();
      cref = bb.getShort();
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bref", bref)
      .add("cb", cb)
      .add("cref", cref)
      .toString();
  }
}
