package org.pipoware.pst.exp;

import org.pipoware.pst.exp.pages.BBTENTRY;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Franck
 */
public class Block {

  public static final int MAX_BLOCK_SIZE = 8192;
  public static final int BLOCK_UNIT_SIZE = 64;
  public static final int BLOCK_BOUNDARY = 64;
  public static final int UNICODE_BLOCKTRAILER_SIZE = 16;
  public static final int ANSI_BLOCKTRAILER_SIZE = 12;

  public static final byte BTYPE_XBLOCK_OR_XXBLOCK = 0x01;
  public static final byte BTYPE_SLBLOCK_OR_SIBLOCK = 0x02;

  public enum BlockType {

    DATA_BLOCK, XBLOCK, XXBLOCK, SLBLOCK, SIBLOCK
  };
  public BlockType blockType;

  public byte data[];
  public long rgbid[];
  private BREF bref;
  private long lcbTotal;
  public SLENTRY[] rgentries_slentry;
  public SIENTRY[] rgentries_sientry;
  

  public static int diskSize(int size, Header.PST_TYPE type) {
    int blocktrailerSize;
    if (type == Header.PST_TYPE.UNICODE) {
      blocktrailerSize = UNICODE_BLOCKTRAILER_SIZE;
    } else if (type == Header.PST_TYPE.ANSI) {
      blocktrailerSize = ANSI_BLOCKTRAILER_SIZE;
    } else {
      throw new AssertionError("Unhandled type :" + type);
    }
    int nb = (int) Math.ceil((double) (size + blocktrailerSize) / BLOCK_UNIT_SIZE);
    return nb * BLOCK_UNIT_SIZE;
  }

  Block(PSTFile pstFile, BBTENTRY bbtentry, Header.PST_TYPE type) throws IOException {
    Preconditions.checkArgument((type == Header.PST_TYPE.ANSI || type == Header.PST_TYPE.UNICODE), "Unhandled PST Type %s", type);
    
    this.bref = new BREF(bbtentry.bref.getBid(), bbtentry.bref.getIb());
    
    int diskBlockSize = diskSize(bbtentry.cb, type);
    byte bytes[] = new byte[diskBlockSize];
    pstFile.position(bbtentry.bref.getIb());
    pstFile.read(bytes);

    int offset;
    ByteBuffer bb;
    switch (type) {
      case UNICODE:
        offset = diskBlockSize - UNICODE_BLOCKTRAILER_SIZE;
        bb = ByteBuffer
          .wrap(Arrays.copyOfRange(bytes, offset, offset + UNICODE_BLOCKTRAILER_SIZE)).
          order(ByteOrder.LITTLE_ENDIAN);
        break;

      case ANSI:
        offset = diskBlockSize - ANSI_BLOCKTRAILER_SIZE;
        bb = ByteBuffer
          .wrap(Arrays.copyOfRange(bytes, offset, offset + ANSI_BLOCKTRAILER_SIZE)).
          order(ByteOrder.LITTLE_ENDIAN);
        break;
      default:
        throw new AssertionError();
    }

    short cb = bb.getShort();
    short wSig = bb.getShort();
    
    int wSigComputed = computeSig(bbtentry.bref.getIb(), bbtentry.bref.getBid());
    Preconditions.checkArgument(wSig == wSigComputed,
      "wSig (0x%s) <> wSigComputed (0x%s) (BREF=%s)",
      Integer.toHexString(wSig),
      Integer.toHexString(wSigComputed),
      bref);

    int dwCRC;
    long bid;
    if (type == Header.PST_TYPE.UNICODE) {
      dwCRC = bb.getInt();
      bid = bb.getLong();
    } else if (type == Header.PST_TYPE.ANSI) {
      bid = bb.getInt();
      dwCRC = bb.getInt();
    } else {
      throw new AssertionError();
    }

    Preconditions.checkArgument(bbtentry.bref.getBid() == bid, "BBTENTRY bid (%s) <> bid from block bytes (%s)", bbtentry.bref.getBid(), bid);

    byte crcData[] = Arrays.copyOf(bytes, bbtentry.cb);
    int dwComputedCRC = CRC.computeCRC(0, crcData);

    Preconditions.checkArgument(dwCRC == dwComputedCRC, "dwCRC (%s) <> dwComputedCRC(%s)", dwCRC, dwComputedCRC);
    Preconditions.checkArgument(bbtentry.cb == cb, "BBTENTRY cb(%s) <> cb(%s)", bbtentry.cb, cb);

    if (!BID.isInternal(bid)) {
      // Block type = Data Tree
      // Data Structure = Data block
      blockType = BlockType.DATA_BLOCK;
      data = Arrays.copyOf(bytes, cb);
    } else {
      bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

      byte btype = bb.get();
      byte cLevel = bb.get();

      short cEnt = bb.getShort();
      Preconditions.checkArgument(cEnt > 0);

      Preconditions.checkArgument((btype == BTYPE_XBLOCK_OR_XXBLOCK) || (btype == BTYPE_SLBLOCK_OR_SIBLOCK), "Unknown btype %s", btype);

      if (btype == BTYPE_XBLOCK_OR_XXBLOCK) {
        if (cLevel == 0x01) {
          blockType = BlockType.XBLOCK;
        } else if (cLevel == 0x02) {
          blockType = BlockType.XXBLOCK;
        } else {
          throw new IllegalArgumentException("Unsupported cLevel (" + cLevel + ") for btype " + btype);
        }

          lcbTotal = bb.getInt();

          rgbid = new long[cEnt];
          for (int i = 0; i < cEnt; i++) {
            rgbid[i] = (type == Header.PST_TYPE.UNICODE) ? bb.getLong() : bb.getInt();
          }
      } else if (btype == BTYPE_SLBLOCK_OR_SIBLOCK) {
        if (cLevel == 0x00) {
          blockType = BlockType.SLBLOCK;
          
          // MS-PST is wrong, dwPadding only in UNICODE file format
          if (pstFile.getHeader().getType() == Header.PST_TYPE.UNICODE) {
            int dwPadding = bb.getInt();
            Preconditions.checkArgument(dwPadding == 0, "dwPadding %s <> 0", dwPadding);
          }
          
          rgentries_slentry = new SLENTRY[cEnt];
          for (int i = 0; i < cEnt; i++) {
            long nid;
            long bidData;
            long bidSub;

            if (type == Header.PST_TYPE.UNICODE) {
              nid = bb.getInt();
              bb.getInt(); // Don't read this as it's not allways 0
              bidData = bb.getLong();
              bidSub = bb.getLong();
            } else {
              nid = bb.getInt();
              bidData = bb.getInt();
              bidSub = bb.getInt();
            }
            
            rgentries_slentry[i] = new SLENTRY(nid, bidData, bidSub);
          }
          
        } else if (cLevel == 0x01) {
          blockType = BlockType.SIBLOCK;

          // MS-PST is wrong, dwPadding only in UNICODE file format
          if (pstFile.getHeader().getType() == Header.PST_TYPE.UNICODE) {
            int dwPadding = bb.getInt();
            Preconditions.checkArgument(dwPadding == 0, "dwPadding %s <> 0", dwPadding);
          }
          
          readRgSIENTRY(cEnt, type, bb);
        }
        else {
          throw new IllegalArgumentException("Unsupported cLevel (" + cLevel + ") for btype " + btype);
        }
      }

    }
  }

  private void readRgSIENTRY(short cEnt, Header.PST_TYPE type, ByteBuffer bb) {
    rgentries_sientry = new SIENTRY[cEnt];
    for (int i = 0; i < cEnt; i++) {
      long nid;
      long bid;
      
      if (type == Header.PST_TYPE.UNICODE) {
        nid = bb.getLong();
        bid = bb.getLong();
      } else {
        nid = bb.getInt();
        bid = bb.getInt();
      }
      
      rgentries_sientry[i] = new SIENTRY(nid, bid);
    }
  }

  public BREF getBREF() {
    return bref;
  }
  
  public static short computeSig(long ib, long bid) {
    ib ^= bid;
    int a = (int) (ib >>> 16);
    int b = (int) ib & 0x0000FFFF;
    return (short) (a ^ b);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bref", bref)
      .add("type", blockType)
      .add("rgbid", BID.toString(rgbid))
      .add("rgentries_slentry", Arrays.toString(rgentries_slentry))
      .add("rgentries_sientry", Arrays.toString(rgentries_sientry))
      .toString();

  }
}
