/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck
 */
class BBTENTRY {
  private final BREF bref;
  private final short cb;
  private final short cref;

  BBTENTRY(byte[] entrybyte, Header.PST_TYPE type) {
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
  
}
