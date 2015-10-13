package org.pipoware.pst.exp;

import java.util.EnumSet;

/**
 *
 * @author Franck Arnulfo
 */
public enum PropertyTag {

  PidTagNameidBucketCount((short) 0x0001),
  PidTagNameidStreamGuid((short) 0x0002),
  PidTagNameidStreamEntry((short) 0x0003),
  PidTagNameidStreamString((short) 0x0004),
  PidTagNameidBucketBase((short) 0x1000),
  PidTagItemTemporaryFlags((short) 0x1097),
  PidTagPstBestBodyProptag((short) 0x661D),
  PidTagPstHiddenCount((short) 0x6635),
  PidTagPstHiddenUnread((short) 0x6636),
  PidTagPstIpmsubTreeDescendant((short) 0x6705),
  PidTagPstSubTreeContainer((short) 0x6772),
  PidTagLtpParentNid((short) 0x67F1),
  PidTagLtpRowId((short) 0x67F2),
  PidTagLtpRowVer((short) 0x67F3),
  PidTagPstPassword((short) 0x67FF),
  PidTagMapiFormComposeCommand((short) 0x682F);

  private final short propertyIdentifier;

  private PropertyTag(short propertyIdentifier) {
    this.propertyIdentifier = propertyIdentifier;
  }

  public short getPropertyIdentifier() {
    return propertyIdentifier;
  }

  public static PropertyTag getPropertyTagFromIdentifier(short propertyIdentifier) {
    for (PropertyTag tag : EnumSet.allOf(PropertyTag.class)) {
      if (tag.getPropertyIdentifier() == propertyIdentifier) {
        return tag;
      }
    }
    return null;
  }
};
