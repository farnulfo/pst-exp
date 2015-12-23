package org.pipoware.pst.exp;

import java.io.IOException;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagDisplayName;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagIpmSubTreeEntryId;

/**
 *
 * @author Franck Arnulfo
 */
class MessageStore {

  private final String displayName;
  private final PC messageStorePC;
  private final NDB ndb;

  public MessageStore(NDB ndb, PC messageStorePC) {
    this.ndb = ndb;
    this.messageStorePC = messageStorePC;
    displayName = this.messageStorePC.getPCItemByPropertyIdentifier(PidTagDisplayName).getString();
  }

  public String getDisplayName() {
    return displayName;
  }

  public Folder getRootFolder() throws IOException {
    PCItem pic = messageStorePC.getPCItemByPropertyIdentifier(PidTagIpmSubTreeEntryId);
    EntryID rootEntryId = new EntryID(pic.dataValue);
    PC rootFolderPC = ndb.getPCFromNID(rootEntryId.nid);
    return new Folder(rootFolderPC);
  }

}
