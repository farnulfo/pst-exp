package org.pipoware.pst.exp;

import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;
import java.util.Arrays;

/**
 *
 * @author Franck Arnulfo
 */
public class KeyData {

  public final byte[] key;
  public final byte[] data;

  public KeyData(byte[] key, byte[] data) {
    this.key = Arrays.copyOf(key, key.length);
    this.data = Arrays.copyOf(data, data.length);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("key", "[" + BaseEncoding.base16().withSeparator(",", 2).encode(key) + "]")
      .add("data", "[" + BaseEncoding.base16().withSeparator(",", 2).encode(data) + "]")
      .toString();
  }
}
