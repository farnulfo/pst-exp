/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Franck Arnulfo
 */
public class TCINFO {

  public final byte bType;
  public final byte cCols;
  public final short TCI_4b;
  public final short TCI_2b;
  public final short TCI_1b;
  public final short TCI_bm;
  public final int hidRowIndex;
  public final int hnidRows;
  public final int hnidIndex;
  public TCOLDESC[] tColDesc;

  public TCINFO(byte[] heapItem) {
    ByteBuffer bb = ByteBuffer.wrap(heapItem).order(ByteOrder.LITTLE_ENDIAN);
    bType = bb.get();

    Preconditions.checkArgument(bType == HN.CLIENT_SIG_TC, "Invalid bType(%s) for a TC", bType);

    cCols = bb.get();

    TCI_4b = bb.getShort();
    TCI_2b = bb.getShort();
    TCI_1b = bb.getShort();
    TCI_bm = bb.getShort();

    hidRowIndex = bb.getInt();
    hnidRows = bb.getInt();
    hnidIndex = bb.getInt();

    tColDesc = new TCOLDESC[cCols];
    for (int i = 0; i < cCols; i++) {
      int tag = bb.getInt();
      short ibData = bb.getShort();
      byte cbData = bb.get();
      byte iBit = bb.get();
      tColDesc[i] = new TCOLDESC(tag, ibData, cbData, iBit);
    }

  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bType", bType)
      .add("cCols", cCols)
      .add("TCI_4b", TCI_4b)
      .add("TCI_2b", TCI_2b)
      .add("TCI_1b", TCI_1b)
      .add("TCI_bm", TCI_bm)
      .add("hidRowIndex", hidRowIndex)
      .add("hnidRows", hnidRows)
      .add("hnidIndex", hnidIndex)
      .add("tColDesc", Arrays.toString(tColDesc))
      .toString();
  }
}
