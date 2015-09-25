package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Franck Arnulfo
 */
public class PC {

  private final BTH bth;
  private final List<PCITem> items = new ArrayList<>();

  public PC(BTH aBTH) {
    this.bth = aBTH;
    
    for(KeyData keyData : bth.keyDatas) {
      PCITem item = new PCITem(keyData);
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
