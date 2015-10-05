/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

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
public class PSTFile implements IPSTFile {

    private Path path = null;
    private RandomAccessFile file = null;
    private FileChannel fileChannel = null;

    private static final int ULONG_SIZE = 8;
    public static final int DWORD_SIZE = 4;
    public static final int WORD_SIZE = 2;

    public static final int DWMAGIC_OFFSET = 0;
    public static final int DWCRCPARTIAL_OFFSET = 4;
    public static final int WVER_OFFSET = 10;
    private final ByteBuffer dword = ByteBuffer.allocate(DWORD_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    private final ByteBuffer word = ByteBuffer.allocate(WORD_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    private final ByteBuffer ulong = ByteBuffer.allocate(ULONG_SIZE).order(ByteOrder.LITTLE_ENDIAN);
    private final ByteBuffer ubyte = ByteBuffer.allocate(1);
    private final Header header;
  private final NDB nbd;
  private final LTP ltp;
  private final Messaging messaging;

    public PSTFile(Path path) throws FileNotFoundException, IOException {
        this.path = path;
        this.file = new RandomAccessFile(this.path.toFile(), "r");
        fileChannel = this.file.getChannel();
        header = new Header(this);
        nbd = new NDB(this, header);
        ltp = new LTP(nbd);
        messaging = new Messaging(ltp);
    }

    public Header getHeader() {
        return header;
    }

    @Override
    public int readDWORD() throws FileNotFoundException, IOException {
        dword.clear();
        fileChannel.read(dword);
        dword.flip();
        return dword.getInt();
    }

    @Override
    public short readWORD() throws FileNotFoundException, IOException {
        word.clear();
        fileChannel.read(word);
        word.flip();
        return word.getShort();
    }

    @Override
    public long readULONG() throws FileNotFoundException, IOException {
        ulong.clear();
        fileChannel.read(ulong);
        ulong.flip();
        return ulong.getLong();
    }

    @Override
    public byte readBYTE() throws FileNotFoundException, IOException {
        ubyte.clear();
        fileChannel.read(ubyte);
        ubyte.flip();
        return ubyte.get();
    }

    @Override
    public void position(long newPosition) throws IOException {
        fileChannel.position(newPosition);
    }

    @Override
    public int read(ByteBuffer buffer, long position) throws IOException {
        return fileChannel.read(buffer, position);
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return fileChannel.read(b);
    }
}
