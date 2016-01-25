/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp.pages;

import com.google.common.base.MoreObjects;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.pipoware.pst.exp.BREF;
import org.pipoware.pst.exp.Header;

/**
 *
 * @author Franck
 */
public class BTENTRY {

  public final long btKey;
  public final BREF bref;

  public BTENTRY(long btKey, BREF bref) {
    this.btKey = btKey;
    this.bref = bref;
  }

  public BTENTRY(byte[] entrybyte, Header.PST_TYPE type) {
    ByteBuffer bb = ByteBuffer.wrap(entrybyte).order(ByteOrder.LITTLE_ENDIAN);

    if (type == Header.PST_TYPE.UNICODE) {
      btKey = bb.getLong();
      long bid = bb.getLong();
      long ib = bb.getLong();
      bref = new BREF(bid, ib);
    } else {
      btKey = bb.getInt();
      long bid = bb.getInt();
      long ib = bb.getInt();
      bref = new BREF(bid, ib);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("btKey", "0x" + Long.toHexString(btKey))
      .add("bref", bref)
      .toString();
  }
}
