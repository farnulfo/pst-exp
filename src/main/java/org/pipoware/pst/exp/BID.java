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
  
}
