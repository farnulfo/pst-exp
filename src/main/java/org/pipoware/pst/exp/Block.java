package org.pipoware.pst.exp;

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
  public static int MAX_BLOCK_SIZE = 8192;
  public static int BLOCK_UNIT_SIZE = 64;
  public static int BLOCK_BOUNDARY = 64;
  public static int UNICODE_BLOCKTRAILER_SIZE = 16;
  public static int ANSI_BLOCKTRAILER_SIZE = 12;
  
  public static int diskSize(int size, Header.PST_TYPE type) {
    int blocktrailerSize;
    if (type == Header.PST_TYPE.UNICODE) {
      blocktrailerSize = UNICODE_BLOCKTRAILER_SIZE;
    } else if (type == Header.PST_TYPE.ANSI) {
      blocktrailerSize = ANSI_BLOCKTRAILER_SIZE;
    } else {
      throw new AssertionError("Unhandled type :" + type);
    }
    int nb = (int) Math.ceil((double) (size + blocktrailerSize)  / BLOCK_UNIT_SIZE);
    return nb * BLOCK_UNIT_SIZE;
  }

  Block(PSTFile pstFile, BBTENTRY bbtentry, Header.PST_TYPE type) throws IOException {
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

    Preconditions.checkArgument(dwCRC == dwComputedCRC, "dwCRC (%s) <> dwComputedCRC(%s)", dwCRC ,dwComputedCRC);
    
  }
}
