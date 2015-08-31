/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

/**
 *
 * @author Franck
 */
class BREF {
    
    private final long bid;
    private final long ib;

    BREF(long bid, long ib) {
        this.bid = bid;
        this.ib = ib;
    }
    
    public long getBid() {
        return bid;
    }

    public long getIb() {
        return ib;
    }
}
