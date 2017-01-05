package org.pipoware.pst.exp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Franck Arnulfo
 */
public class MessageTest {

  @Test
  public void testMessageProperty() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    PC messagePC = ndb.getPCFromNID(messageNid);
    System.out.println("PC:");
    for (PCItem item : messagePC.items) {
      System.out.println(item);
    }
  }

  @Test
  public void testMessagePropertySubject1() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    PC messagePC = ndb.getPCFromNID(messageNid);
    Message message = new Message(ndb, messageNid);
    String subject = message.getSubject();
    Assert.assertEquals("Here is a sample message", message.getSubject());
  }

  @Test
  public void testMessagePropertySubject2() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/submessage.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    PC messagePC = ndb.getPCFromNID(messageNid);
    Message message = new Message(ndb, messageNid);
    String subject = message.getSubject();
    Assert.assertEquals("This is a message which has an embedded message attached", message.getSubject());
  }

  @Test
  public void testMessageHasAttachment1() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/sample1.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    Message message = new Message(ndb, messageNid);
    Assert.assertEquals(true, message.hasAttachment());
  }

  @Test
  public void testMessageHasAttachment2() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/submessage.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    Message message = new Message(ndb, messageNid);
    Assert.assertEquals(true, message.hasAttachment());
  }

  @Test
  public void testMessageHasAttachment3() throws IOException, URISyntaxException {
    PSTFile pstFile = new PSTFile(Paths.get(getClass().getResource("/pstsdk/test_unicode.pst").toURI()));
    NDB ndb = new NDB(pstFile, pstFile.getHeader());
    int messageNid = 0x200024;
    Message message = new Message(ndb, messageNid);
    Assert.assertEquals(false, message.hasAttachment());
  }
}
