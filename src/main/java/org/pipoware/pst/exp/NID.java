/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;

/**
 *
 * @author Franck
 */
public class NID {

  // Heap node
  public static final byte NID_TYPE_HID = 0x00;
  // Internal node (section 2.4.1)
  public static final byte NID_TYPE_INTERNAL = 0x01;
  // Normal Folder object (PC)
  public static final byte NID_TYPE_NORMAL_FOLDER = 0x02;
  // Search Folder object (PC)
  public static final byte NID_TYPE_SEARCH_FOLDER = 0x03;
  // Normal Message object (PC)
  public static final byte NID_TYPE_NORMAL_MESSAGE = 0x04;
  // Attachment object (PC)
  public static final byte NID_TYPE_ATTACHMENT = 0x05;
  // Queue of changed objects for search Folder objects
  public static final byte NID_TYPE_SEARCH_UPDATE_QUEUE = 0x06;
  // Defines the search criteria for a search Folder object
  public static final byte NID_TYPE_SEARCH_CRITERIA_OBJECT = 0x07;
  // Folder associated information (FAI) Message object (PC)
  public static final byte NID_TYPE_ASSOC_MESSAGE = 0x08;
  // Internal, persisted view-related
  public static final byte NID_TYPE_CONTENTS_TABLE_INDEX = 0x0A;
  // Receive Folder object (Inbox)
  public static final byte NID_TYPE_RECEIVE_FOLDER_TABLE = 0X0B;
  // Outbound queue (Outbox)
  public static final byte NID_TYPE_OUTGOING_QUEUE_TABLE = 0x0C;
  // Hierarchy table (TC)
  public static final byte NID_TYPE_HIERARCHY_TABLE = 0x0D;
  // Contents table (TC)
  public static final byte NID_TYPE_CONTENTS_TABLE = 0x0E;
  // FAI contents table (TC)
  public static final byte NID_TYPE_ASSOC_CONTENTS_TABLE = 0x0F;
  // Contents table (TC) of a search Folder object
  public static final byte NID_TYPE_SEARCH_CONTENTS_TABLE = 0x10;
  // Attachment table (TC)
  public static final byte NID_TYPE_ATTACHMENT_TABLE = 0x11;
  // Recipient table (TC)
  public static final byte NID_TYPE_RECIPIENT_TABLE = 0x12;
  // Internal, persisted view-related
  public static final byte NID_TYPE_SEARCH_TABLE_INDEX = 0x13;
  // LTP
  public static final byte NID_TYPE_LTP = 0x1F;

  private final long data;
  private final long nidType;

  public NID(long data) {
    this.data = data;
    nidType = data & 0x1F;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("nidType", "0x" + Long.toHexString(nidType))
      .add("nidData", "0x" + Long.toHexString(data))
      .toString();
  }
}
