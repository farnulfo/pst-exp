/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.IOException;

/**
 *
 * @author Franck
 */
public class NDB {

  private final PSTFile pst;
  private final Header header;

  public NDB(PSTFile pst, Header header) {
    this.pst = pst;
    this.header = header;
  }

  private void getPageLeafEntries(long pageOffset) throws IOException {
    Page page = fetchPage(pageOffset);
  }

  private Page fetchPage(long pageOffset) throws IOException {
    pst.position(pageOffset);
    byte[] bytes = new byte[Page.PAGE_SIZE];
    pst.read(bytes);
    return new Page(bytes, pst.getHeader().getType());
  }

  public long getBlockIdFromNID(int nid) throws IOException {
    Page rootNBTPage = fetchPage(header.getRoot().bRefNBT.getIb());
    return getBlockIdFromNID(rootNBTPage, nid);
  }
  
  private long getBlockIdFromNID(Page page, int nid) throws IOException {
    if (page.getDepthLevel() == 0) {
      for (NBTENTRY nbtentry : page.nbtentries) {
        if (nbtentry.nid.data == nid) {
          return nbtentry.bidData;
        }
      }
      return -1;
    } else {
      for (BTENTRY btentry : page.btentries) {
        pst.position(btentry.bref.getIb());
        byte[] b = new byte[Page.PAGE_SIZE];
        pst.read(b);
        Page p = new Page(b, pst.getHeader().getType());
        long bid = getBlockIdFromNID(p, nid);
        if (bid != -1) {
          return bid;
        }
      }
    }
    throw new IllegalArgumentException("NID " + nid +" no found.");
  }
  
      
  public PC getPCFromNID(int nid) throws IOException {
    long bid = getBlockIdFromNID(nid);
    Page page = fetchPage(pst.getHeader().getRoot().bRefBBT.getIb());
    Block block = getBlockFromBID(page, bid);
    HN hn = new HN(block.data);
    BTH bth = new BTH(hn);
    PC pc = new PC(bth);
    return pc;
  }

  private Block getBlockFromBID(Page page, long bid) throws IOException {
    if (page.getDepthLevel() == 0) {
      for (BBTENTRY bbentry : page.bbtentries) {
        if (bbentry.bref.getBid() == bid) {
          Block block = new Block(pst, bbentry, pst.getHeader().getType());
          return block;
        }
      }
    } else {
      for (BTENTRY btentry : page.btentries) {
        pst.position(btentry.bref.getIb());
        byte[] b = new byte[Page.PAGE_SIZE];
        pst.read(b);
        Page p = new Page(b, pst.getHeader().getType());
        Block block = getBlockFromBID(p, bid);
        if (block != null) {
          return block;
        }
      }
    }
    return null;
  }
}
