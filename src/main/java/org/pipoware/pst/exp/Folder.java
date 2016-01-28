package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagContentCount;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagContentUnreadCount;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagDisplayName;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagSubfolders;

/**
 *
 * @author Franck Arnulfo
 */
public class Folder implements Iterable<Message> {

  private final NDB ndb;
  private final PC folderPC;
  private final TC hierarchyTable;
  private final TC contentTable;
  private final String displayName;
  private final int contentCount;
  private final int contentUnreadCount;
  private final boolean subFolders;
  private final List<Folder> folders = new ArrayList<>();

  public Folder(NDB ndb, PC pc, TC hierarchyTable, TC aContentTable) {
    this.ndb = ndb;
    this.folderPC = pc;
    this.hierarchyTable = hierarchyTable;
    this.contentTable = aContentTable;
    displayName = folderPC.getPCItemByPropertyIdentifier(PidTagDisplayName).getString();
    contentCount = folderPC.getPCItemByPropertyIdentifier(PidTagContentCount).getInt();
    contentUnreadCount = folderPC.getPCItemByPropertyIdentifier(PidTagContentUnreadCount).getInt();
    subFolders = folderPC.getPCItemByPropertyIdentifier(PidTagSubfolders).getBoolean();
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getContentCount() {
    return contentCount;
  }

  public int getContentUnreadCount() {
    return contentUnreadCount;
  }
  
  public boolean hasSubFolers() {
    return subFolders;
  }

  public List<Folder> getFolders() throws IOException {
    if (hasSubFolers()) {
      for (TCROWID row : hierarchyTable.getRows()) {
        int dwRowId = row.dwRowID;
        PC subFolderPC = ndb.getPCFromNID(dwRowId);
        boolean hasFolder = subFolderPC.getPCItemByPropertyIdentifier(PidTagSubfolders).getBoolean();
        TC hc = null;
        if (hasFolder) {
          int tcNID = (dwRowId & 0xFFFFFFE0) | NID.NID_TYPE_HIERARCHY_TABLE;
          hc = ndb.getTCFromNID(tcNID);
        }
        
        TC contentTableTC = null;
        int nbSubFolderMessages = subFolderPC.getPCItemByPropertyIdentifier(PidTagContentCount).getInt();
        if (nbSubFolderMessages > 0) {
          int contentTableNID = (dwRowId & 0xFFFFFFE0) | NID.NID_TYPE_CONTENTS_TABLE;
          contentTableTC = ndb.getTCFromNID(contentTableNID);
        }

        folders.add(new Folder(ndb, subFolderPC, hc, contentTableTC));
      }
    }
    return folders;
  }
  
  public List<Message> getMessages() throws IOException {
    List<Message> messages = new ArrayList<>();
    for (TCROWID tcrowid : contentTable.getRows()) {
      try {
        Message message = new Message(ndb, tcrowid.dwRowID);
        messages.add(message);
      } catch (Exception e) {
        System.out.println("getMessages() Exception : " + e);
        e.printStackTrace();
      }
    }
    return messages;
  }
  
  public Iterator<Message> iterator() {
    return new MessageIterator();
  }
  
  public Iterator<Message> messages() {
    return new MessageIterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("displayName", displayName)
            .add("contentCount", contentCount)
            .add("contentUnreadCount", contentUnreadCount)
            .add("hasSubFolders", subFolders)
            .toString();
  }

  private class MessageIterator implements Iterator<Message> {

    private int cursor;

    public MessageIterator() {
      this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
      return cursor < Folder.this.contentTable.getRows().size();
    }

    @Override
    public Message next() {
      if (this.hasNext()) {
        int rowId = Folder.this.contentTable.getRows().get(cursor).dwRowID;
        try {
          Message message = new Message(ndb, rowId);
          cursor++;
          return message;
        } catch (IOException ex) {
          Logger.getLogger(Folder.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
