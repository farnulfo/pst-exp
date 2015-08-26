/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import com.google.common.io.BaseEncoding;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 *
 * @author fa
 */
public class PSTFile {

  private Path path = null;
  private RandomAccessFile file = null;
  private FileChannel fileChannel = null;
  
  public static final int DWORD_SIZE = 4;
  public static final int WORD_SIZE =2;
  
  public static final int DWMAGIC_OFFSET = 0;
  public static final int DWCRCPARTIAL_OFFSET = 4;
  public static final int WVER_OFFSET = 10;
    
  public PSTFile(Path path) throws FileNotFoundException {
    this.path = path;
    this.file = new RandomAccessFile(this.path.toFile(), "r");
    fileChannel = this.file.getChannel();
  }

  public int readdwMagic() throws FileNotFoundException, IOException {
    ByteBuffer buffer = ByteBuffer.allocate(DWORD_SIZE);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
   
    fileChannel.read(buffer, DWMAGIC_OFFSET);
    buffer.flip();

    return buffer.getInt();
  }

  public int readdwCRCPartial() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(DWORD_SIZE);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    fileChannel.read(buffer, DWCRCPARTIAL_OFFSET);

    buffer.flip();

    return buffer.getInt();
  }
  
  public short readwVer() throws FileNotFoundException, IOException {
    ByteBuffer buffer = ByteBuffer.allocate(WORD_SIZE);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    fileChannel.read(buffer, WVER_OFFSET);

    buffer.flip();

    return buffer.getShort();
  }
  
  public int computeCRCPartial() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(471);
    fileChannel.read(buffer, 8);
    return CRC.computeCRC(0, buffer.array());
  }
}
