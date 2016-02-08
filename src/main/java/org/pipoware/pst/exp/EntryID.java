package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Franck Arnulfo
 */
public class EntryID {
  
  public final int rgbFlags;
  public byte[] uid;
  public int nid;
  
  public EntryID(byte[] bytes) {
    ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    rgbFlags = bb.getInt();
    uid = new byte[16];
    bb.get(uid);
    nid = bb.getInt();
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("rgbFlags", Integer.toHexString(rgbFlags))
      .add("uid", "[" + BaseEncoding.base16().withSeparator(",", 2).encode(uid) + "]")
      .add("nid", Integer.toHexString(nid))
      .toString();
  }
}
