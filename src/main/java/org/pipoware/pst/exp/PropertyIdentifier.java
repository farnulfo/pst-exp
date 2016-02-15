package org.pipoware.pst.exp;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Franck Arnulfo
 */
public class PropertyIdentifier {

  
  private PropertyIdentifier() {
  }

  public static final short PidTagMessageDeliveryTime = (short) 0x0E06;
  public static final short PidTagDisplayName = (short) 0x3001;
  public static final short PidTagCreationTime = (short) 0x3007;
  public static final short PidTagLastModificationTime = (short) 0x3008;
  public static final short PidTagContentCount = (short) 0x3602;
  public static final short PidTagContentUnreadCount = (short) 0x3603;
  public static final short PidTagSubfolders = (short) 0x360A;
  public static final short PidTagIpmSubTreeEntryId = (short) 0x35E0;
  public static final short PR_SUBJECT = (short) 0x0037;
  
  public static final ImmutableMap<Short, String> MAP = ImmutableMap.<Short, String>builder()
    .put((short) 0x0E20, "PidTagAttachmentSize")
    .put((short) 0x0E06, "PidTagMessageDeliveryTime")
    .put((short) 0x3001, "PidTagDisplayName")
    .put((short) 0x3007, "PidTagCreationTime")
    .put((short) 0x3008, "PidTagLastModificationTime")
    .put((short) 0x3602, "PidTagContentCount")
    .put((short) 0x3603, "PidTagContentUnreadCount")
    .put((short) 0x360A, "PidTagSubfolders")
    .put((short) 0x35E0, "PidTagIpmSubTreeEntryId")
    .put((short) 0x3704, "PidTagAttachFilenameW")
    .put((short) 0x3705, "PidTagAttachMethod")
    .put((short) 0x370B, "PidTagRenderingPosition")
    .put((short) 0x0037, "PR_SUBJECT")
    .build();
}
