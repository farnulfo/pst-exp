package org.pipoware.pst.exp;

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

  private byte data[];
  private long rgbid[];
  private BREF bref;
  

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

          int lcbTotal = bb.getInt();

          rgbid = new long[cEnt];
          for (int i = 0; i < cEnt; i++) {
            rgbid[i] = (type == Header.PST_TYPE.UNICODE) ? bb.getLong() : bb.getInt();
          }

        }
      }

    }
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bref", bref)
      .add("type", blockType)
      .add("rgbid", BID.toString(rgbid))
      .toString();

  }
}
