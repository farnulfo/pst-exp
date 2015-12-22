package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;

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

  public static final short PidTagDisplayName = (short) 0x3001;
  public static final short PidTagContentCount = (short) 0x3602;
  public static final short PidTagContentUnreadCount = (short) 0x3603;
  public static final short PidTagSubfolders = (short) 0x360A;

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
