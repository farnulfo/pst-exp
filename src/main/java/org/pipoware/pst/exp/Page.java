/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pipoware.pst.exp;

import java.util.Arrays;
import java.util.EnumSet;

/**
 *
 * @author Franck
 */
class Page {

  static int PAGE_SIZE = 512;

  private final byte[] bytes;
  private enum PageType {

    ptypeBBT((byte) 0x80),
    ptypeNBT((byte) 0x81),
    ptypeFMap((byte) 0x82),
    ptypePMap((byte) 0x83),
    ptypeAMap((byte) 0x84),
    ptypeFPMap((byte) 0x85),
    ptypeDL((byte) 0x86);

    private final byte type;

    private PageType(byte b) {
      type = b;
    }
    
    public static PageType get(byte b) {
      for (PageType p : EnumSet.allOf(PageType.class)) {
        if (p.getValue() == b) {
          return p;
        }
      }
      throw new IllegalArgumentException("Invalid Page Type " + b);
    }
    
    public byte getValue() {
      return type;
    }

  };

  private PageType pType;

  Page(byte[] bytes, Header.PST_TYPE type) {
    this.bytes = Arrays.copyOf(bytes, PAGE_SIZE);
    if (type == Header.PST_TYPE.UNICODE) {
      pType = PageType.get(this.bytes[PAGE_SIZE - 16]);
    }
  }

}
