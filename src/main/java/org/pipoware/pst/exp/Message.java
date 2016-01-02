package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import java.io.IOException;

/**
 *
 * @author Franck Arnulfo
 */
public class Message {
  
  private final NDB ndb;
  private final int nid;

  public Message(NDB aNDB, int aNID) throws IOException {
    this.ndb = aNDB;
    this.nid = aNID;
    PC messageObjectPC = ndb.getPCFromNID(nid);
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nid", "0x" + Integer.toHexString(nid))
      .toString();
  }
  
}
