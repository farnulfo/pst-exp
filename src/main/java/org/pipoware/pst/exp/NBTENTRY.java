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
class NBTENTRY {

  private final NID nid;
  private final long bidData;
  private final long bidSub;
  private final long nidParent;

  public NBTENTRY(byte[] entrybyte, Header.PST_TYPE type) {
    ByteBuffer bb = ByteBuffer.wrap(entrybyte).order(ByteOrder.LITTLE_ENDIAN);

    if (type == Header.PST_TYPE.UNICODE) {
      nid = new NID(bb.getLong());
      bidData = bb.getLong();
      bidSub = bb.getLong();
      nidParent = bb.getInt();
      int dwPadding = bb.getInt();
    } else {
      nid = new NID(bb.getInt());
      bidData = bb.getInt();
      bidSub = bb.getInt();
      nidParent = bb.getInt();
    }

  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("NID", nid)
      .add("bidData", bidData)
      .add("bidSub", bidSub)
      .add("nidParent", nidParent)
      .toString();
  }

}
