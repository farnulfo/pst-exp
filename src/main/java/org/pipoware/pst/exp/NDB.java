/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import org.pipoware.pst.exp.pages.BBTENTRY;
import org.pipoware.pst.exp.pages.BTENTRY;
import org.pipoware.pst.exp.pages.Page;
import org.pipoware.pst.exp.pages.NBTENTRY;
import com.google.common.base.Preconditions;
import java.io.IOException;

/**
 *
 * @author Franck
 */
public class NDB {

  public final PSTFile pst;
  private final Header header;

  public NDB(PSTFile pst, Header header) {
    this.pst = pst;
    this.header = header;
  }

  private Page fetchPage(long pageOffset) throws IOException {
    pst.position(pageOffset);
    byte[] bytes = new byte[Page.PAGE_SIZE];
    pst.read(bytes);
    return new Page(bytes, pst.getHeader().getType());
  }

  private NBTENTRY geNBTENTRYFromNID(int nid) throws IOException {
    Page rootNBTPage = fetchPage(header.getRoot().bRefNBT.getIb());
    return getNBTENTRYFromNID(rootNBTPage, nid);
  }
  
  public NBTENTRY getNBTENTRYFromNID(Page page, int nid) throws IOException {
    if (page.getDepthLevel() == 0) {
      for (NBTENTRY nbtentry : page.nbtentries) {
        if (nbtentry.nid.data == nid) {
          return nbtentry;
        }
      }
      return null;
    } else {
      for (BTENTRY btentry : page.btentries) {
        pst.position(btentry.bref.getIb());
        byte[] b = new byte[Page.PAGE_SIZE];
        pst.read(b);
        Page p = new Page(b, pst.getHeader().getType());
        NBTENTRY nbtentry = getNBTENTRYFromNID(p, nid);
        if (nbtentry != null) {
          return nbtentry;
        }
      }
    }
    throw new IllegalArgumentException("NID " + nid +" no found.");
  }

  public PC getPCFromNID(int nid) throws IOException {
    NBTENTRY nbtentry = geNBTENTRYFromNID(nid);
    Page page = fetchPage(pst.getHeader().getRoot().bRefBBT.getIb());
    Block block = getBlockFromBID(page, nbtentry.bidData);
    byte bCryptMethod = pst.getHeader().getBCryptMethod();
    Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
    if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
      PermutativeEncoding.decode(block.data);
    }
    HN hn = new HN(block.data);
    BTH bth = new BTH(hn);
    PC pc = new PC(bth, this, nbtentry);
    return pc;
  }
  
  public Block getBlockFromBID(long bid) throws IOException {
    Page page = fetchPage(pst.getHeader().getRoot().bRefBBT.getIb());
    Block block = getBlockFromBID(page, bid);
    return block;
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
  
  public TC getTCFromNID(int nid) throws Exception {
    NBTENTRY nbtentry = geNBTENTRYFromNID(nid);
    Page page = fetchPage(pst.getHeader().getRoot().bRefBBT.getIb());
    Block block = getBlockFromBID(page, nbtentry.bidData);
    byte bCryptMethod = pst.getHeader().getBCryptMethod();
    Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
    if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
      PermutativeEncoding.decode(block.data);
    }
    HN hn = new HN(block.data);
    TC tc = new TC(hn);
    return tc;
  }
}
