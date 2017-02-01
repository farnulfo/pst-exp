package org.pipoware.pst.exp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Franck Arnulfo
 */
public enum NIDType {

  NID_TYPE_HID((byte) 0x00, "Heap node"),
  NID_TYPE_INTERNAL((byte) 0x01, "Internal node"),
  NID_TYPE_NORMAL_FOLDER((byte) 0x02, "Normal Folder object (PC)"),
  NID_TYPE_SEARCH_FOLDER((byte) 0x03, "Search Folder object (PC)"),
  NID_TYPE_NORMAL_MESSAGE((byte) 0x04, "Normal Message object (PC)"),
  NID_TYPE_ATTACHMENT((byte) 0x05, "Attachment object (PC)"),
  NID_TYPE_SEARCH_UPDATE_QUEUE((byte) 0x06, "Queue of changed objects for search Folder objects"),
  NID_TYPE_SEARCH_CRITERIA_OBJECT((byte) 0x07, "Defines the search criteria for a search Folder object"),
  NID_TYPE_ASSOC_MESSAGE((byte) 0x08, "Folder associated information (FAI) Message object (PC)"),
  NID_TYPE_CONTENTS_TABLE_INDEX((byte) 0x0A, "Internal, persisted view-related"),
  NID_TYPE_RECEIVE_FOLDER_TABLE((byte) 0x0B, "Receive Folder object (Inbox)"),
  NID_TYPE_OUTGOING_QUEUE_TABLE((byte) 0x0C, "Outbound queue (Outbox)"),
  NID_TYPE_HIERARCHY_TABLE((byte) 0x0D, "Hierarchy table (TC)"),
  NID_TYPE_CONTENTS_TABLE((byte) 0x0E, "Contents table (TC)"),
  NID_TYPE_ASSOC_CONTENTS_TABLE((byte) 0x0F, "FAI contents table (TC)"),
  NID_TYPE_SEARCH_CONTENTS_TABLE((byte) 0x10, "Contents table (TC) of a search Folder object"),
  NID_TYPE_ATTACHMENT_TABLE((byte) 0x11, "Attachment table (TC)"),
  NID_TYPE_RECIPIENT_TABLE((byte) 0x12, "Recipient table (TC)"),
  NID_TYPE_SEARCH_TABLE_INDEX((byte) 0x13, "Internal, persisted view-related"),
  NID_TYPE_LTP((byte) 0x1F, "LTP");

  public final byte value;
  public final String comment;

  private static final Map<Byte, NIDType> map = new HashMap<>();

  static {
    for (NIDType nidType : NIDType.values()) {
      map.put(nidType.value, nidType);
    }
  }

  NIDType(byte value, String comment) {
    this.value = value;
    this.comment = comment;
  }
  
  public static Optional<NIDType> valueOf(byte value) {
    return Optional.ofNullable(map.get(value));
  }

}
