package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.pipoware.pst.exp.pages.NBTENTRY;

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
    NBTENTRY nbtentry = ndb.getNBTENTRYFromNID(nid);
    if (nbtentry.bidSub != 0) {
      getRecipients(nbtentry);
    }
  }

  private void getRecipients(NBTENTRY nbtentry) throws IOException {
    Block subBlock = ndb.getBlockFromBID(nbtentry.bidSub);
    Preconditions.checkNotNull(subBlock, "Sub Block Id 0x%s not found for NID 0x%s", Long.toHexString(nbtentry.bidSub), Integer.toHexString(nid));
    Preconditions.checkArgument(subBlock.blockType == Block.BlockType.SLBLOCK);
    
    for (SLENTRY slEntry : subBlock.rgentries_slentry) {
      if (new NID(slEntry.nid).nidType == NID.NID_TYPE_RECIPIENT_TABLE) {
        TC recipientTable = ndb.getTCFromSLENTRY(slEntry);
        //System.out.println(recipientTable.getRows());
        //recipientTable.displayRowMatrixData();
      }
    }
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
      subject = messageObjectPC.getPCItemByPropertyIdentifier(PropertyIdentifier.PR_SUBJECT).getString();
    } catch (Exception e) {
      System.out.println(e);
      System.out.println(messageObjectPC);
    }
    return MoreObjects.toStringHelper(this)
      .add("nid", "0x" + Integer.toHexString(nid))
      .add("subject", subject)
      .toString();
  }

}
