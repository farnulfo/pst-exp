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

/**
 * 
 *  +-+-+-+-+-+-+-+-+-+-----+-+-+-+-+-+-+-+-----+-+-+-+-+-+-+-+-----+-+-+       
 *  | | | | | | | | | | |1| | | | | | | | | |2| | | | | | | | | |3| | | |       
 *  |0|1|2|3|4|5|6|7|8|9|0|1|2|3|4|5|6|7|8|9|0|1|2|3|4|5|6|7|8|9|0|1|2|3|       
 *  +-------------------------------------------------------------------+       
 *  |A|B|                          bidIndex                             |       
 *  +-------------------------------------------------------------------+       
 *  |                               ...                                 |       
 *  +-------------------------------------------------------------------+       
 */

public class BID {
  
  public static boolean isInternal(long bid) {
    return (bid & 0b10) != 0 ;
  }
  
  public static String toString(long bid) {
    return "0x" + Long.toHexString(bid) + " " + ((isInternal(bid) ? "INTERNAL" : "NOT INTERNAL"));
  }

  public static String toString(long[] arrayOfBID) {
        if (arrayOfBID == null)
            return "null";
        int iMax = arrayOfBID.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(BID.toString(arrayOfBID[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
    
}
