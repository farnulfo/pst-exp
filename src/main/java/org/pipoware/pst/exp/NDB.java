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
import java.util.Arrays;

/**
 *
 * @author Franck
 */
public class NDB {

  public final PSTFile pst;
  private final Header header;
  
  public enum SEARCH_IMPL { SIMPLE_SEARCH, BINARY_SEARCH}
  
  private static SEARCH_IMPL searchImplementation = SEARCH_IMPL.BINARY_SEARCH;
  
  public static void setSearchImplemation(SEARCH_IMPL search_impl) {
    NDB.searchImplementation = search_impl;
  }

  public static SEARCH_IMPL getSearchImplemation() {
    return NDB.searchImplementation;
  }

  public NDB(PSTFile pst, Header header) {
    this.pst = pst;
    this.header = header;
  }

  public Page getPage(BREF bref) throws IOException {
    return getPage(bref.getIb());
  }
  
  public Page getPage(long pageOffset) throws IOException {
    pst.position(pageOffset);
    byte[] bytes = new byte[Page.PAGE_SIZE];
    pst.read(bytes);
    return new Page(bytes, pst.getHeader().getType());
  }

  public NBTENTRY getNBTENTRYFromNID(int nid) throws IOException {
    Page rootNBTPage = getPage(header.getRoot().bRefNBT.getIb());
    NBTENTRY nbtentry = getNBTENTRYFromNID(rootNBTPage, nid);
    if (nbtentry == null) {
      throw new IllegalArgumentException("NID " + nid +" no found.");
    } else {
      return nbtentry;
    }
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
        NBTENTRY nbtentry = getNBTENTRYFromNID(getPage(btentry.bref), nid);
        if (nbtentry != null) {
          return nbtentry;
        }
      }
    }
    return null;
  }

  public PC getPCFromNID(int nid) throws IOException {
    NBTENTRY nbtentry = getNBTENTRYFromNID(nid);
    Block block = getBlockFromBID(nbtentry.bidData);
    Preconditions.checkArgument(block.blockType == Block.BlockType.DATA_BLOCK, "Blocktype %s not yet handled!", block.blockType);
    byte bCryptMethod = pst.getHeader().getBCryptMethod();
    Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
    if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
      PermutativeEncoding.decode(block.data);
    }
    HN hn = new HN(this, nid, block.data);
    BTH bth = new BTH(hn);
    PC pc = new PC(bth, this, nbtentry);
    return pc;
  }
  
  public Block getBlockFromBID(long bid) throws IOException {
    Page page = getPage(pst.getHeader().getRoot().bRefBBT.getIb());
    Block block;
    if (searchImplementation == SEARCH_IMPL.BINARY_SEARCH) {
      block = getBlockFromBIDBinarySearchImpl(page, bid);
    } else {
      block = getBlockFromBIDSimpleImpl(page, bid);
    }
    return block;
  }
  
  private Block getBlockFromBIDSimpleImpl(Page page, long bid) throws IOException {
    if (page.getDepthLevel() == 0) {
      for (BBTENTRY bbentry : page.bbtentries) {
        if (bbentry.bref.getBid() == bid) {
          Block block = new Block(pst, bbentry, pst.getHeader().getType());
          return block;
        }
      }
    } else {
      for (BTENTRY btentry : page.btentries) {
        Block block = getBlockFromBIDSimpleImpl(getPage(btentry.bref), bid);
        if (block != null) {
          return block;
        }
      }
    }
    return null;
  }
  
    private Block getBlockFromBIDBinarySearchImpl(Page page, long bid) throws IOException {
    if (page.getDepthLevel() == 0) {
      int index = Arrays.binarySearch(
        page.bbtentries,
        new BBTENTRY(new BREF(bid, 0), (short) 0, (short) 0),
        (BBTENTRY o1, BBTENTRY o2) -> (int) (o1.bref.getBid() - o2.bref.getBid()));
      if (index >= 0) {
        Block block = new Block(pst, page.bbtentries[index], pst.getHeader().getType());
        return block;
      } else {
        return null; // not found
      }
    } else {
      int index = Arrays.binarySearch(page.btentries, new BTENTRY(bid, null), (BTENTRY o1, BTENTRY o2) -> (int) (o1.btKey - o2.btKey));
      if (index >= 0) {
        Block block = getBlockFromBIDBinarySearchImpl(getPage(page.btentries[index].bref), bid);
        if (block != null) {
          return block;
        }
      } else if (index < 0) {
        int insertionPoint = (-1 - index);
        if (insertionPoint <= 0) {
          return null;
        } else {
          Block block = getBlockFromBIDBinarySearchImpl(getPage(page.btentries[insertionPoint - 1].bref), bid);
          if (block != null) {
            return block;
          }
        }

      }
    }
    return null;
  }
  
  public TC getTCFromNID(int nid) throws IOException {
    byte bCryptMethod = pst.getHeader().getBCryptMethod();
    NBTENTRY nbtentry = getNBTENTRYFromNID(nid);
    Block block = getBlockFromBID(nbtentry.bidData);
    Preconditions.checkArgument(block.blockType == Block.BlockType.DATA_BLOCK || block.blockType == Block.BlockType.XBLOCK,
            "Blocktype %s not yet handled!", block.blockType);
    HN hn = null;
    if (block.blockType == Block.BlockType.DATA_BLOCK) {
      Preconditions.checkArgument((bCryptMethod == Header.NDB_CRYPT_NONE) || (bCryptMethod == Header.NDB_CRYPT_PERMUTE));
      if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
        PermutativeEncoding.decode(block.data);
      }
      hn = new HN(this, nid, block.data);
    } else if (block.blockType == Block.BlockType.XBLOCK) {
      byte[][] datas = new byte[block.rgbid.length][];
      for (int i = 0; i < block.rgbid.length; i++) {
        long blockId = block.rgbid[i];
        Block subBlock = getBlockFromBID(blockId);
        Preconditions.checkNotNull(subBlock, "BlockId 0x%s not found for XBLOCK 0x%s, rgbid[%s]", Long.toHexString(blockId), Long.toHexString(nbtentry.bidData), i);
        Preconditions.checkArgument(subBlock.blockType == Block.BlockType.DATA_BLOCK, "Unexpected block type %s for BlockId %s rgbid[%s]", subBlock.blockType, nbtentry.bidData, i);
        if (bCryptMethod == Header.NDB_CRYPT_PERMUTE) {
          PermutativeEncoding.decode(subBlock.data);
        }
        datas[i] = subBlock.data;
        }
      hn = new HN(this, nid, datas);
    }
    TC tc = new TC(hn, nbtentry);
    return tc;
  }
}
