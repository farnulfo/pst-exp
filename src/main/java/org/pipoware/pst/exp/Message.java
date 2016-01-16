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
  private final PC messageObjectPC;

  public Message(NDB aNDB, int aNID) throws IOException {
    this.ndb = aNDB;
    this.nid = aNID;
    messageObjectPC = ndb.getPCFromNID(nid);
  }
  
  public void toStringMessageObjectPC() {
    for (PCItem item : messageObjectPC.items) {
      System.out.println(item.toString());      
    }
  }
  
  @Override
  public String toString() {
    String subject = "ERROR: Subject not found!";
    try {
      subject = messageObjectPC.getPCItemByPropertyIdentifier((short) 0x37).getString();
    } catch (Exception e) {
      System.out.println(e);
    }
    return MoreObjects.toStringHelper(this)
      .add("nid", "0x" + Integer.toHexString(nid))
      .add("subject", subject)
      .toString();
  }
  
}
