package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck Arnulfo
 */
public class Message {
  
  private final NDB ndb;
  private final int nid;

  public Message(NDB aNDB, int aNID) {
    this.ndb = aNDB;
    this.nid = aNID;
  }
  
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nid", "0x" + Integer.toHexString(nid))
      .toString();
  }
  
}
