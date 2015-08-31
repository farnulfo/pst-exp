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
class Root {
    
    private int dwReserved;
    private long ibFileEof;
    private long ibAMapLast;
    long cbAMapFree;
    long cbPMapFree;
    BREF bRefNBT;
    BREF bRefBBT;
    byte fAMapValid;
    byte bReserved;
    short wReserved;

    Root(IPSTFile pst, Header.PST_TYPE type) throws IOException {
        dwReserved = pst.readDWORD();
        if (type == Header.PST_TYPE.UNICODE) {
            ibFileEof = pst.readULONG();
            ibAMapLast = pst.readULONG();
            cbAMapFree = pst.readDWORD();
            cbPMapFree = pst.readDWORD();
            long bid = pst.readULONG();
            long ib = pst.readULONG();
            bRefNBT = new BREF(bid, ib);
            bid = pst.readULONG();
            ib = pst.readULONG();
            bRefBBT = new BREF(bid, ib);
        } else if (type == Header.PST_TYPE.ANSI) {
            ibFileEof = pst.readDWORD();
            ibAMapLast = pst.readDWORD();
            cbAMapFree = pst.readDWORD();
            cbPMapFree = pst.readDWORD();
            int bid = pst.readDWORD();
            int ib = pst.readDWORD();
            bRefNBT = new BREF(bid, ib);
            bid = pst.readDWORD();
            ib = pst.readDWORD();
            bRefBBT = new BREF(bid, ib);
        }
        fAMapValid = pst.readBYTE();
        bReserved = pst.readBYTE();
        wReserved = pst.readWORD();
    }
    
}
