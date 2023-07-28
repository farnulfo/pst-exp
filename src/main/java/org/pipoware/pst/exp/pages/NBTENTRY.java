/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp.pages;

import com.google.common.base.MoreObjects;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.pipoware.pst.exp.Header;
import org.pipoware.pst.exp.NID;

/**
 *
 * @author Franck
 */
public class NBTENTRY {

  public final NID nid;
  public final long bidData;
  public final long bidSub;
  public final long nidParent;

  public NBTENTRY(int nid, long bidData, long bidSub, long nidParent) {
    this.nid = new NID(nid);
    this.bidData = bidData;
    this.bidSub = bidSub;
    this.nidParent = nidParent;
  }

  // TODO mettre de la doc !
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
      .add("bidData", "0x" + Long.toHexString(bidData))
      .add("bidSub", "0x" + Long.toHexString(bidSub))
      .add("nidParent", "0x" + Long.toHexString(nidParent))
      .toString();
  }

}
