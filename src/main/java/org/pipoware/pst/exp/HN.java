package org.pipoware.pst.exp;

import java.util.Arrays;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franck Arnulfo
 */
public class HN {

  public static final byte CLIENT_SIG_RESERVED1 = 0x6C;
  public static final byte CLIENT_SIG_TC = 0x7C;
  public static final byte CLIENT_SIG_RESERVED2 = (byte) 0x8C;
  public static final byte CLIENT_SIG_RESERVED3 = (byte) 0x9C;
  public static final byte CLIENT_SIG_RESERVED4 = (byte) 0xA5;
  public static final byte CLIENT_SIG_RESERVED5 = (byte) 0xAC;
  public static final byte CLIENT_SIG_BTH = (byte) 0xB5;
  public static final byte CLIENT_SIG_PC_BTH = (byte) 0xBC;
  public static final byte CLIENT_SIG_RESERVED6 = (byte) 0xCC;

  public final NDB ndb;
  
  /**
   * nid is not used by HN, only for context when there is an error to have.
   */
  public final int nid;
  
  private final List<HNDataBlock> hnDataBlocks = new ArrayList<>();

  public HN(NDB ndb, int nid, byte[]... blockDatasArray) {
    this.ndb = ndb;
    this.nid = nid;

    for (int i = 0; i < blockDatasArray.length; i++) {
      byte[] data = Arrays.copyOf(blockDatasArray[i], blockDatasArray[i].length);
      HNDataBlock hnDataBlock = new HNDataBlock(i, data);
      hnDataBlocks.add(hnDataBlock);
      }
    }
  
  public byte getSig() {
    return hnDataBlocks.get(0).bSig;
  }
  
  public byte getClientSig() {
    return hnDataBlocks.get(0).bClientSig;
  }
  
  public int getHidUserRoot() {
    return hnDataBlocks.get(0).hidUserRoot;
  }

  byte[] getHeapItem(HID hid) {
    int blockIndex = hid.hidBlockIndex;
    Preconditions.checkArgument(blockIndex >= 0);
    Preconditions.checkArgument(blockIndex <= hnDataBlocks.size());
    
    return hnDataBlocks.get(blockIndex).getHeapItem(hid);
  }
}
