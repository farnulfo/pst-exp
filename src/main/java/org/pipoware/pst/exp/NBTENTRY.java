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
class NBTENTRY {

  private final long nid;
  private final long bidData;
  private final long bidSub;
  private final long nidParent;

  public NBTENTRY(byte[] entrybyte, Header.PST_TYPE type) {
    ByteBuffer bb = ByteBuffer.wrap(entrybyte).order(ByteOrder.LITTLE_ENDIAN);

    if (type == Header.PST_TYPE.UNICODE) {
      nid = bb.getLong();
      bidData = bb.getLong();
      bidSub = bb.getLong();
      nidParent = bb.getInt();
      int dwPadding = bb.getInt();
      System.out.println(dwPadding);
    } else {
      nid = bb.getInt();
      bidData = bb.getInt();
      bidSub = bb.getInt();
      nidParent = bb.getInt();
    }

  }

}
