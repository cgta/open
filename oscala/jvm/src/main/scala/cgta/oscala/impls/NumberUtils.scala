package cgta.oscala
package impls


//From Apache Commons, translated to scala by intellij java to scala converter
object NumberUtils {
  /**
   * <p>Checks whether the String a valid Java number.</p>
   *
   * <p>Valid numbers include hexadecimal marked with the <code>0x</code> or
   * <code>0X</code> qualifier, octal numbers, scientific notation and numbers
   * marked with a type qualifier (e.g. 123L).</p>
   *
   * <p>Non-hexadecimal strings beginning with a leading zero are
   * treated as octal values. Thus the string <code>09</code> will return
   * <code>false</code>, since <code>9</code> is not a valid octal value.
   * However, numbers beginning with {@code 0.} are treated as decimal.</p>
   *
   * <p><code>Null</code> and empty String will return
   * <code>false</code>.</p>
   *
   * @param str  the <code>String</code> to check
   * @return <code>true</code> if the string is a correctly formatted number
   * @since 3.3 the code supports hex { @code 0Xhhh} and octal { @code 0ddd} validation
   */
  def isNumber(str: String): Boolean = {
    if (str == null || str.isEmpty) {
      return false
    }
    val chars: Array[Char] = str.toCharArray
    var sz: Int = chars.length
    var hasExp: Boolean = false
    var hasDecPoint: Boolean = false
    var allowSigns: Boolean = false
    var foundDigit: Boolean = false
    val start: Int = if ((chars(0) == '-')) 1 else 0
    if (sz > start + 1 && chars(start) == '0') {
      if ((chars(start + 1) == 'x') || (chars(start + 1) == 'X')) {
        var i: Int = start + 2
        if (i == sz) {
          return false
        }
        while (i < chars.length) {
          {
            if ((chars(i) < '0' || chars(i) > '9') && (chars(i) < 'a' || chars(i) > 'f') && (chars(i) < 'A' || chars(i) > 'F')) {
              return false
            }
          }
          ({i += 1; i - 1})
        }
        return true
      }
      else if (Character.isDigit(chars(start + 1))) {
        var i: Int = start + 1
        while (i < chars.length) {
          {
            if (chars(i) < '0' || chars(i) > '7') {
              return false
            }
          }
          ({i += 1; i - 1})
        }
        return true
      }
    }
    sz -= 1
    var i: Int = start
    while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
      if (chars(i) >= '0' && chars(i) <= '9') {
        foundDigit = true
        allowSigns = false
      }
      else if (chars(i) == '.') {
        if (hasDecPoint || hasExp) {
          return false
        }
        hasDecPoint = true
      }
      else if (chars(i) == 'e' || chars(i) == 'E') {
        if (hasExp) {
          return false
        }
        if (!foundDigit) {
          return false
        }
        hasExp = true
        allowSigns = true
      }
      else if (chars(i) == '+' || chars(i) == '-') {
        if (!allowSigns) {
          return false
        }
        allowSigns = false
        foundDigit = false
      }
      else {
        return false
      }
      i += 1
    }
    if (i < chars.length) {
      if (chars(i) >= '0' && chars(i) <= '9') {
        return true
      }
      if (chars(i) == 'e' || chars(i) == 'E') {
        return false
      }
      if (chars(i) == '.') {
        if (hasDecPoint || hasExp) {
          return false
        }
        return foundDigit
      }
      if (!allowSigns && (chars(i) == 'd' || chars(i) == 'D' || chars(i) == 'f' || chars(i) == 'F')) {
        return foundDigit
      }
      if (chars(i) == 'l' || chars(i) == 'L') {
        return foundDigit && !hasExp && !hasDecPoint
      }
      return false
    }
    return !allowSigns && foundDigit
  }
}

//Original source for Java
//From apache commons
//http://svn.apache.org/viewvc/commons/proper/lang/trunk/src/main/java/org/apache/commons/lang3/StringUtils.java?view=markup
//http://svn.apache.org/viewvc/commons/proper/lang/trunk/src/main/java/org/apache/commons/lang3/math/NumberUtils.java?view=co
//public class NumberUtils {
//    /**
//     * <p>Checks whether the String a valid Java number.</p>
//     *
//     * <p>Valid numbers include hexadecimal marked with the <code>0x</code> or
//     * <code>0X</code> qualifier, octal numbers, scientific notation and numbers
//     * marked with a type qualifier (e.g. 123L).</p>
//     *
//     * <p>Non-hexadecimal strings beginning with a leading zero are
//     * treated as octal values. Thus the string <code>09</code> will return
//     * <code>false</code>, since <code>9</code> is not a valid octal value.
//     * However, numbers beginning with {@code 0.} are treated as decimal.</p>
//     *
//     * <p><code>Null</code> and empty String will return
//     * <code>false</code>.</p>
//     *
//     * @param str  the <code>String</code> to check
//     * @return <code>true</code> if the string is a correctly formatted number
//     * @since 3.3 the code supports hex {@code 0Xhhh} and octal {@code 0ddd} validation
//     */
//    public static boolean isNumber(final String str) {
//        if (str == null || str.isEmpty()) {
//            return false;
//        }
//        final char[] chars = str.toCharArray();
//        int sz = chars.length;
//        boolean hasExp = false;
//        boolean hasDecPoint = false;
//        boolean allowSigns = false;
//        boolean foundDigit = false;
//        // deal with any possible sign up front
//        final int start = (chars[0] == '-') ? 1 : 0;
//        if (sz > start + 1 && chars[start] == '0') { // leading 0
//            if (
//                 (chars[start + 1] == 'x') ||
//                 (chars[start + 1] == 'X')
//            ) { // leading 0x/0X
//                int i = start + 2;
//                if (i == sz) {
//                    return false; // str == "0x"
//                }
//                // checking hex (it can't be anything else)
//                for (; i < chars.length; i++) {
//                    if ((chars[i] < '0' || chars[i] > '9')
//                        && (chars[i] < 'a' || chars[i] > 'f')
//                        && (chars[i] < 'A' || chars[i] > 'F')) {
//                        return false;
//                    }
//                }
//                return true;
//           } else if (Character.isDigit(chars[start + 1])) {
//               // leading 0, but not hex, must be octal
//               int i = start + 1;
//               for (; i < chars.length; i++) {
//                   if (chars[i] < '0' || chars[i] > '7') {
//                       return false;
//                   }
//               }
//               return true;
//           }
//        }
//        sz--; // don't want to loop to the last char, check it afterwords
//              // for type qualifiers
//        int i = start;
//        // loop to the next to last char or to the last char if we need another digit to
//        // make a valid number (e.g. chars[0..5] = "1234E")
//        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
//            if (chars[i] >= '0' && chars[i] <= '9') {
//                foundDigit = true;
//                allowSigns = false;
//
//            } else if (chars[i] == '.') {
//                if (hasDecPoint || hasExp) {
//                    // two decimal points or dec in exponent
//                    return false;
//                }
//                hasDecPoint = true;
//            } else if (chars[i] == 'e' || chars[i] == 'E') {
//                // we've already taken care of hex.
//                if (hasExp) {
//                    // two E's
//                    return false;
//                }
//                if (!foundDigit) {
//                    return false;
//                }
//                hasExp = true;
//                allowSigns = true;
//            } else if (chars[i] == '+' || chars[i] == '-') {
//                if (!allowSigns) {
//                    return false;
//                }
//                allowSigns = false;
//                foundDigit = false; // we need a digit after the E
//            } else {
//                return false;
//            }
//            i++;
//        }
//        if (i < chars.length) {
//            if (chars[i] >= '0' && chars[i] <= '9') {
//                // no type qualifier, OK
//                return true;
//            }
//            if (chars[i] == 'e' || chars[i] == 'E') {
//                // can't have an E at the last byte
//                return false;
//            }
//            if (chars[i] == '.') {
//                if (hasDecPoint || hasExp) {
//                    // two decimal points or dec in exponent
//                    return false;
//                }
//                // single trailing decimal point after non-exponent is ok
//                return foundDigit;
//            }
//            if (!allowSigns
//                && (chars[i] == 'd'
//                    || chars[i] == 'D'
//                    || chars[i] == 'f'
//                    || chars[i] == 'F')) {
//                return foundDigit;
//            }
//            if (chars[i] == 'l'
//                || chars[i] == 'L') {
//                // not allowing L with an exponent or decimal point
//                return foundDigit && !hasExp && !hasDecPoint;
//            }
//            // last character is illegal
//            return false;
//        }
//        // allowSigns is true iff the val ends in 'E'
//        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
//        return !allowSigns && foundDigit;
//    }
//}
