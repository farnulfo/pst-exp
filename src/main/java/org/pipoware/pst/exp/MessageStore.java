package org.pipoware.pst.exp;

import java.io.IOException;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagDisplayName;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagIpmSubTreeEntryId;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagSubfolders;

/**
 *
 * @author Franck Arnulfo
 */
public class MessageStore {

  private final String displayName;
  private final PC messageStorePC;
  private final NDB ndb;

  public MessageStore(NDB ndb, PC messageStorePC) {
    this.ndb = ndb;
    this.messageStorePC = messageStorePC;
    displayName = this.messageStorePC.findPCItemByPropertyTag(PropertyTag.PidTagDisplayName).getString();
  }

  public String getDisplayName() {
    return displayName;
  }

  public Folder getRootFolder() throws IOException {
    PCItem pic = messageStorePC.findPCItemByPropertyTag(PropertyTag.PidTagIpmSubTreeEntryId);
    EntryID rootEntryId = new EntryID(pic.dataValue);
    PC rootFolderPC = ndb.getPCFromNID(rootEntryId.nid);
    boolean hasFolder = rootFolderPC.findPCItemByPropertyTag(PropertyTag.PidTagSubfolders).getBoolean();
    int rootFolderHCnid = (rootEntryId.nid & 0xFFFFFFE0) | NID.NID_TYPE_HIERARCHY_TABLE;
    TC hc = ndb.getTCFromNID(rootFolderHCnid);
    return new Folder(ndb, rootFolderPC, hc, null);
  }

}
