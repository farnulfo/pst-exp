package org.pipoware.pst.exp;

/**
 *
 * @author Franck Arnulfo
 */
public class ToStringHelper {

  public static String formatMultiline(String s) {
    StringBuilder sb = new StringBuilder();
    int indent = 0;
    int first = 0;
    int currPos;
    for (int i = 0; i < s.length(); i++) {
      currPos = i;
      char c = s.charAt(i);
      switch (c) {
        case '{':
          if (first != 0) {
            sb.append('\n');
          }
          sb.append(space(indent));
          sb.append(s.substring(first, currPos + 1));
          indent += 2;
          first = currPos + 1;
          break;
        case '}':
          sb.append('\n');
          sb.append(space(indent));
          sb.append(s.substring(first, currPos));
          indent -= 2;
          sb.append('\n');
          sb.append(space(indent));
          sb.append('}');
          first = currPos + 1;
          break;
      }
    }
    return sb.toString();
  }

  public static String space(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }

}
