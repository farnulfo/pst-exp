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
class NBD {

  private final PSTFile pst;
  private final Header header;

  public NBD(PSTFile pst, Header header) {
    this.pst = pst;
    this.header = header;
  }
  
  private void getPageLeafEntries(long pageOffset) throws IOException {
    Page page = fetchPage(pageOffset);
  }

  private Page fetchPage(long pageOffset) throws IOException {
    pst.position(pageOffset);
    byte[] bytes = new byte[Page.PAGE_SIZE];
    pst.read(bytes);
    return new Page(bytes, pst.getHeader().getType());
  }

}
