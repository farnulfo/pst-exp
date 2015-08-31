/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Franck
 */
public interface IPSTFile {
    int readDWORD() throws IOException;
    short readWORD() throws IOException;
    byte readBYTE() throws IOException;
    long readULONG() throws IOException;
    int read(ByteBuffer buffer, long position) throws IOException;
    void position(long newPosition) throws IOException;
}
