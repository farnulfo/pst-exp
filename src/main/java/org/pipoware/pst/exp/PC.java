package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.pipoware.pst.exp.pages.NBTENTRY;

/**
 *
 * @author Franck Arnulfo
 */
public class PC {

  private final BTH bth;
  private final List<PCITem> items = new ArrayList<>();

  public PC(BTH aBTH, NDB ndb, NBTENTRY nbtentry) throws IOException {
    this.bth = aBTH;
    
    for(KeyData keyData : bth.keyDatas) {
      PCITem item = new PCITem(bth, keyData, ndb, nbtentry);
      items.add(item);
    }
    
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("bth", bth)
      .add("items", items.toString())
      .toString();
  }

}
