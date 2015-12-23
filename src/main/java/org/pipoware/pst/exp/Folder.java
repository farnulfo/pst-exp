package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagContentCount;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagContentUnreadCount;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagDisplayName;
import static org.pipoware.pst.exp.PropertyIdentifier.PidTagSubfolders;

/**
 *
 * @author Franck Arnulfo
 */
public class Folder {

  private final PC folderPC;
  private final String displayName;
  private final int contentCount;
  private final int contentUnreadCount;
  private final boolean subFolders;

  public Folder(PC pc) {
    folderPC = pc;
    displayName = folderPC.getPCItemByPropertyIdentifier(PidTagDisplayName).getString();
    contentCount = folderPC.getPCItemByPropertyIdentifier(PidTagContentCount).getInt();
    contentUnreadCount = folderPC.getPCItemByPropertyIdentifier(PidTagContentUnreadCount).getInt();
    subFolders = folderPC.getPCItemByPropertyIdentifier(PidTagSubfolders).getBoolean();
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getContentCOunt() {
    return contentCount;
  }

  public int getContentUnreadCount() {
    return contentUnreadCount;
  }
  
  public boolean hasSubFolers() {
    return subFolders;
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
}
