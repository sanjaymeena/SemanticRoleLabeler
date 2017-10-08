/**
 * SweNLP is a framework for performing parallel processing of text. 
 * Copyright � 2011 Peter Exner
 * 
 * This file is part of SweNLP.
 *
 * SweNLP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SweNLP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SweNLP.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.lth.cs.srl.preprocessor.tokenization.exner;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 12/7/11 2:23 PM from the specification file
 * <tt>D:/Workspace/Swedish Tokenizer/src/base/Tokenizer.flex</tt>
 */
class Tokenizer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\62\1\61\1\0\1\63\1\60\22\0\1\62\1\21\3\0"+
    "\1\17\1\11\1\3\1\13\1\15\1\17\1\17\1\22\1\7\1\5"+
    "\1\22\12\14\1\4\1\16\1\0\1\17\1\0\1\21\1\12\3\6"+
    "\1\31\6\6\1\41\1\6\1\51\2\6\1\53\5\6\1\54\4\6"+
    "\4\0\1\10\1\0\1\42\1\55\1\24\1\32\1\26\1\36\1\40"+
    "\1\25\1\37\1\50\1\44\1\27\1\47\1\46\1\23\1\43\1\6"+
    "\1\30\1\34\1\35\1\45\1\33\4\6\54\0\1\20\2\0\1\56"+
    "\12\0\1\56\4\0\1\56\5\0\27\56\1\0\15\56\1\52\21\56"+
    "\1\0\u01ca\56\4\0\14\56\16\0\5\56\7\0\1\56\1\0\1\56"+
    "\201\0\5\56\1\0\2\56\2\0\4\56\10\0\1\56\1\0\3\56"+
    "\1\0\1\56\1\0\24\56\1\0\123\56\1\0\213\56\10\0\236\56"+
    "\11\0\46\56\2\0\1\56\7\0\47\56\110\0\33\56\5\0\3\56"+
    "\55\0\53\56\25\0\12\2\4\0\2\56\1\0\143\56\1\0\1\56"+
    "\17\0\2\56\7\0\2\56\12\2\3\56\2\0\1\56\20\0\1\56"+
    "\1\0\36\56\35\0\131\56\13\0\1\56\16\0\12\2\41\56\11\0"+
    "\2\56\4\0\1\56\5\0\26\56\4\0\1\56\11\0\1\56\3\0"+
    "\1\56\27\0\31\56\253\0\66\56\3\0\1\56\22\0\1\56\7\0"+
    "\12\56\4\0\12\2\1\0\7\56\1\0\7\56\5\0\10\56\2\0"+
    "\2\56\2\0\26\56\1\0\7\56\1\0\1\56\3\0\4\56\3\0"+
    "\1\56\20\0\1\56\15\0\2\56\1\0\3\56\4\0\12\2\2\56"+
    "\23\0\6\56\4\0\2\56\2\0\26\56\1\0\7\56\1\0\2\56"+
    "\1\0\2\56\1\0\2\56\37\0\4\56\1\0\1\56\7\0\12\2"+
    "\2\0\3\56\20\0\11\56\1\0\3\56\1\0\26\56\1\0\7\56"+
    "\1\0\2\56\1\0\5\56\3\0\1\56\22\0\1\56\17\0\2\56"+
    "\4\0\12\2\25\0\10\56\2\0\2\56\2\0\26\56\1\0\7\56"+
    "\1\0\2\56\1\0\5\56\3\0\1\56\36\0\2\56\1\0\3\56"+
    "\4\0\12\2\1\0\1\56\21\0\1\56\1\0\6\56\3\0\3\56"+
    "\1\0\4\56\3\0\2\56\1\0\1\56\1\0\2\56\3\0\2\56"+
    "\3\0\3\56\3\0\14\56\26\0\1\56\25\0\12\2\25\0\10\56"+
    "\1\0\3\56\1\0\27\56\1\0\12\56\1\0\5\56\3\0\1\56"+
    "\32\0\2\56\6\0\2\56\4\0\12\2\25\0\10\56\1\0\3\56"+
    "\1\0\27\56\1\0\12\56\1\0\5\56\3\0\1\56\40\0\1\56"+
    "\1\0\2\56\4\0\12\2\1\0\2\56\22\0\10\56\1\0\3\56"+
    "\1\0\51\56\2\0\1\56\20\0\1\56\21\0\2\56\4\0\12\2"+
    "\12\0\6\56\5\0\22\56\3\0\30\56\1\0\11\56\1\0\1\56"+
    "\2\0\7\56\71\0\1\1\60\56\1\1\2\56\14\1\7\56\11\1"+
    "\12\2\47\0\2\56\1\0\1\56\2\0\2\56\1\0\1\56\2\0"+
    "\1\56\6\0\4\56\1\0\7\56\1\0\3\56\1\0\1\56\1\0"+
    "\1\56\2\0\2\56\1\0\4\56\1\0\2\56\11\0\1\56\2\0"+
    "\5\56\1\0\1\56\11\0\12\2\2\0\2\56\42\0\1\56\37\0"+
    "\12\2\26\0\10\56\1\0\44\56\33\0\5\56\163\0\53\56\24\0"+
    "\1\56\12\2\6\0\6\56\4\0\4\56\3\0\1\56\3\0\2\56"+
    "\7\0\3\56\4\0\15\56\14\0\1\56\1\0\12\2\6\0\46\56"+
    "\12\0\53\56\1\0\1\56\3\0\u0149\56\1\0\4\56\2\0\7\56"+
    "\1\0\1\56\1\0\4\56\2\0\51\56\1\0\4\56\2\0\41\56"+
    "\1\0\4\56\2\0\7\56\1\0\1\56\1\0\4\56\2\0\17\56"+
    "\1\0\71\56\1\0\4\56\2\0\103\56\45\0\20\56\20\0\125\56"+
    "\14\0\u026c\56\2\0\21\56\1\0\32\56\5\0\113\56\25\0\15\56"+
    "\1\0\4\56\16\0\22\56\16\0\22\56\16\0\15\56\1\0\3\56"+
    "\17\0\64\56\43\0\1\56\4\0\1\56\3\0\12\2\46\0\12\2"+
    "\6\0\130\56\10\0\51\56\1\0\1\56\5\0\106\56\12\0\35\56"+
    "\51\0\12\2\36\56\2\0\5\56\13\0\54\56\25\0\7\56\10\0"+
    "\12\2\46\0\27\56\11\0\65\56\53\0\12\2\6\0\12\2\15\0"+
    "\1\56\135\0\57\56\21\0\7\56\4\0\12\2\51\0\36\56\15\0"+
    "\2\56\12\2\6\0\46\56\32\0\44\56\34\0\12\2\3\0\3\56"+
    "\12\2\44\56\153\0\4\56\1\0\4\56\16\0\300\56\100\0\u0116\56"+
    "\2\0\6\56\2\0\46\56\2\0\6\56\2\0\10\56\1\0\1\56"+
    "\1\0\1\56\1\0\1\56\1\0\37\56\2\0\65\56\1\0\7\56"+
    "\1\0\1\56\3\0\3\56\1\0\7\56\3\0\4\56\2\0\6\56"+
    "\4\0\15\56\5\0\3\56\1\0\7\56\164\0\1\56\15\0\1\56"+
    "\20\0\15\56\145\0\1\56\4\0\1\56\2\0\12\56\1\0\1\56"+
    "\3\0\5\56\6\0\1\56\1\0\1\56\1\0\1\56\1\0\4\56"+
    "\1\0\13\56\2\0\4\56\5\0\5\56\4\0\1\56\64\0\2\56"+
    "\u0a7b\0\57\56\1\0\57\56\1\0\205\56\6\0\4\56\21\0\46\56"+
    "\12\0\66\56\11\0\1\56\20\0\27\56\11\0\7\56\1\0\7\56"+
    "\1\0\7\56\1\0\7\56\1\0\7\56\1\0\7\56\1\0\7\56"+
    "\1\0\7\56\120\0\1\56\u01d5\0\2\56\52\0\5\56\5\0\2\56"+
    "\3\0\1\57\126\57\6\57\3\57\1\57\132\57\1\57\4\57\5\57"+
    "\51\57\2\57\1\0\136\56\21\0\33\56\65\0\20\57\u0100\0\200\57"+
    "\200\0\u19b6\57\12\57\100\0\u51cc\57\64\57\u048d\56\103\0\56\56\2\0"+
    "\u010d\56\3\0\20\56\12\2\2\56\24\0\57\56\20\0\31\56\10\0"+
    "\106\56\61\0\11\56\2\0\147\56\2\0\4\56\1\0\2\56\16\0"+
    "\12\56\120\0\10\56\1\0\3\56\1\0\4\56\1\0\27\56\35\0"+
    "\64\56\16\0\62\56\34\0\12\2\30\0\6\56\3\0\1\56\4\0"+
    "\12\2\34\56\12\0\27\56\31\0\35\56\7\0\57\56\34\0\1\56"+
    "\12\2\46\0\51\56\27\0\3\56\1\0\10\56\4\0\12\2\6\0"+
    "\27\56\3\0\1\56\5\0\60\56\1\0\1\56\3\0\2\56\2\0"+
    "\5\56\2\0\1\56\1\0\1\56\30\0\3\56\43\0\6\56\2\0"+
    "\6\56\2\0\6\56\11\0\7\56\1\0\7\56\221\0\43\56\15\0"+
    "\12\2\6\0\u2ba4\56\14\0\27\56\4\0\61\56\u2104\0\u012e\57\2\57"+
    "\76\57\2\57\152\57\46\57\7\56\14\0\5\56\5\0\1\56\1\0"+
    "\12\56\1\0\15\56\1\0\5\56\1\0\1\56\1\0\2\56\1\0"+
    "\2\56\1\0\154\56\41\0\u016b\56\22\0\100\56\2\0\66\56\50\0"+
    "\14\56\164\0\5\56\1\0\207\56\23\0\12\2\7\0\32\56\6\0"+
    "\32\56\12\0\1\57\72\57\37\56\3\0\6\56\2\0\6\56\2\0"+
    "\6\56\2\0\3\56\43\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\4\0\1\1\2\2\1\3\1\4\1\5\1\2\2\5"+
    "\2\6\1\7\1\10\1\11\23\2\1\12\1\1\12\0"+
    "\1\2\5\0\1\13\2\0\1\2\6\0\27\2\1\0"+
    "\1\2\4\0\1\14\3\15\1\0\1\14\1\0\1\16"+
    "\3\0\3\14\1\17\3\15\3\14\1\20\2\13\4\21"+
    "\1\22\1\23\2\24\1\25\1\2\1\0\3\2\1\15"+
    "\1\2\6\0\1\26\1\27\1\30\2\27\3\0\1\26"+
    "\1\0\1\31\3\0\1\32\1\0\1\15\2\27\1\33"+
    "\3\15\1\22\1\23\1\34\1\30\2\27";

  private static int [] zzUnpackAction() {
    int [] result = new int[169];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\64\0\150\0\234\0\320\0\u0104\0\u0138\0\320"+
    "\0\320\0\u016c\0\u01a0\0\u01d4\0\320\0\u0208\0\320\0\320"+
    "\0\320\0\320\0\u023c\0\u0270\0\u02a4\0\u02d8\0\u030c\0\u0340"+
    "\0\u0374\0\u03a8\0\u03dc\0\u0410\0\u0444\0\u0478\0\u04ac\0\u04e0"+
    "\0\u0514\0\u0548\0\u057c\0\u05b0\0\u05e4\0\320\0\u0618\0\u064c"+
    "\0\u0680\0\u06b4\0\u06e8\0\u071c\0\u0750\0\u0784\0\u07b8\0\u07ec"+
    "\0\u0820\0\u0854\0\u0888\0\u08bc\0\u08f0\0\u0924\0\u0958\0\u098c"+
    "\0\u09c0\0\u09f4\0\u0a28\0\u0a5c\0\u0a90\0\u0ac4\0\u0af8\0\u0b2c"+
    "\0\u0b60\0\u0b94\0\u0bc8\0\u0bfc\0\u0c30\0\u0c64\0\u0c98\0\u0ccc"+
    "\0\u0d00\0\u0d34\0\u0d68\0\u0d9c\0\u0dd0\0\u0e04\0\u0e38\0\u0e6c"+
    "\0\u0ea0\0\u0ed4\0\u0f08\0\u0f3c\0\u0f70\0\u0fa4\0\u0fd8\0\u100c"+
    "\0\u1040\0\u1074\0\320\0\u0680\0\u10a8\0\u10dc\0\u1110\0\u1144"+
    "\0\u1178\0\u11ac\0\u11e0\0\u1214\0\u1248\0\320\0\u127c\0\u12b0"+
    "\0\u12e4\0\u1318\0\u134c\0\u1380\0\320\0\u13b4\0\u13e8\0\u141c"+
    "\0\u1450\0\u1484\0\u14b8\0\320\0\u14ec\0\u1520\0\320\0\u1144"+
    "\0\u1178\0\u1554\0\u1588\0\u15bc\0\u0ac4\0\u15f0\0\320\0\u1624"+
    "\0\u1658\0\u168c\0\u16c0\0\u16f4\0\u1554\0\u1728\0\u175c\0\u1790"+
    "\0\u17c4\0\u17f8\0\u182c\0\u1860\0\u1894\0\u18c8\0\320\0\u18fc"+
    "\0\u1930\0\u1964\0\u1998\0\u19cc\0\u0784\0\u098c\0\u1a00\0\u1a34"+
    "\0\u1a68\0\u1a9c\0\320\0\u1ad0\0\u1b04\0\u1b38\0\u1b6c\0\u1ba0"+
    "\0\u1bd4\0\u1c08\0\u1c3c\0\u1c70\0\u1ca4\0\320\0\u18c8\0\u1cd8"+
    "\0\u1d0c";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[169];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\2\5\1\16\1\7\1\17\1\11\1\20\1\21"+
    "\1\22\1\15\1\23\2\13\1\24\1\13\1\25\1\26"+
    "\1\27\1\30\1\31\1\32\1\33\2\13\1\34\1\35"+
    "\1\36\1\37\1\13\1\40\1\41\1\42\1\43\1\44"+
    "\1\45\1\30\1\13\1\44\1\46\1\47\3\5\1\0"+
    "\2\50\3\0\1\50\5\0\1\50\6\0\34\50\27\0"+
    "\1\51\2\0\1\52\2\0\1\53\34\0\2\54\3\0"+
    "\1\54\5\0\1\54\6\0\34\54\72\0\2\6\1\0"+
    "\1\55\1\56\1\6\1\57\1\60\1\0\1\61\1\0"+
    "\1\6\5\0\1\55\34\6\6\0\1\6\1\62\1\0"+
    "\1\63\1\64\1\62\1\65\1\66\1\0\1\61\1\0"+
    "\1\62\5\0\1\63\34\62\12\0\1\67\57\0\1\6"+
    "\1\62\1\70\1\71\1\72\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\34\73\7\0\1\100"+
    "\11\0\1\100\55\0\1\101\5\0\1\101\6\0\27\101"+
    "\1\0\3\101\7\0\1\6\1\62\1\70\1\71\1\72"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\21\73\1\102\12\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\12\73\1\103\21\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\72\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\3\73\1\104\30\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\72\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\1\103"+
    "\33\73\6\0\1\6\1\62\1\70\1\71\1\72\1\73"+
    "\1\74\1\75\1\76\1\77\1\0\1\62\5\0\1\55"+
    "\1\103\2\73\1\105\4\73\1\106\23\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\72\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\1\107\33\73\6\0"+
    "\1\6\1\62\1\70\1\71\1\72\1\73\1\74\1\75"+
    "\1\76\1\77\1\0\1\62\5\0\1\55\3\73\1\110"+
    "\10\73\1\111\17\73\6\0\1\6\1\62\1\70\1\71"+
    "\1\72\1\73\1\74\1\75\1\76\1\77\1\0\1\62"+
    "\5\0\1\55\3\73\1\107\1\73\1\112\26\73\6\0"+
    "\1\6\1\62\1\70\1\71\1\72\1\73\1\74\1\75"+
    "\1\76\1\77\1\0\1\62\5\0\1\55\3\73\1\113"+
    "\7\73\1\112\1\114\17\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\1\115\16\73\1\116\2\73\1\117"+
    "\11\73\6\0\1\6\1\62\1\70\1\71\1\72\1\73"+
    "\1\74\1\75\1\76\1\77\1\0\1\62\5\0\1\55"+
    "\20\73\1\120\1\73\1\121\11\73\6\0\1\6\1\62"+
    "\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77"+
    "\1\0\1\62\5\0\1\55\5\73\1\122\26\73\6\0"+
    "\1\6\1\62\1\70\1\71\1\72\1\73\1\74\1\75"+
    "\1\76\1\77\1\0\1\62\5\0\1\55\4\73\1\112"+
    "\12\73\1\116\2\73\1\117\11\73\6\0\1\6\1\62"+
    "\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77"+
    "\1\0\1\62\5\0\1\55\1\123\33\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\72\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\14\73\1\124\2\73"+
    "\1\125\7\73\1\126\4\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\17\73\1\127\2\73\1\130\11\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\72\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\1\106"+
    "\11\73\1\112\21\73\6\0\1\6\1\62\1\70\1\71"+
    "\1\131\1\73\1\74\1\75\1\76\1\77\1\0\1\62"+
    "\5\0\1\55\34\73\6\0\1\6\1\62\1\70\1\71"+
    "\1\72\1\73\1\74\1\75\1\76\1\77\1\0\1\62"+
    "\5\0\1\55\5\73\1\132\26\73\66\0\1\5\3\0"+
    "\2\50\3\0\1\50\1\133\4\0\1\50\6\0\34\50"+
    "\65\0\4\134\24\0\1\135\65\0\1\136\36\0\2\54"+
    "\3\0\1\54\5\0\1\54\6\0\34\54\1\0\4\134"+
    "\2\0\1\137\3\0\1\55\5\0\1\137\6\0\34\55"+
    "\6\0\1\140\1\141\3\0\1\142\5\0\1\141\6\0"+
    "\34\142\6\0\1\143\1\144\3\0\1\145\4\0\1\146"+
    "\1\144\1\146\5\0\34\145\1\0\4\147\1\0\1\143"+
    "\1\144\3\0\1\145\5\0\1\144\6\0\34\145\6\0"+
    "\2\150\3\0\1\150\5\0\1\150\6\0\34\150\6\0"+
    "\1\6\1\62\1\0\1\63\1\151\1\62\1\65\1\66"+
    "\1\0\1\61\1\0\1\62\5\0\1\63\34\62\6\0"+
    "\1\152\1\153\3\0\1\154\5\0\1\153\6\0\34\154"+
    "\5\0\1\155\1\156\1\157\3\155\1\160\5\155\1\157"+
    "\1\0\5\155\34\160\1\155\2\0\1\155\2\0\1\161"+
    "\1\162\3\0\1\163\4\0\1\146\1\162\1\146\5\0"+
    "\34\163\1\0\4\147\1\0\1\161\1\162\3\0\1\163"+
    "\5\0\1\162\6\0\34\163\12\0\1\164\64\0\1\165"+
    "\14\0\34\165\7\0\1\137\3\0\1\166\5\0\1\137"+
    "\6\0\34\166\5\0\1\167\1\170\1\171\3\167\1\172"+
    "\5\167\1\171\1\0\5\167\34\172\1\167\2\0\1\167"+
    "\2\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\34\73"+
    "\6\0\1\143\1\144\3\0\1\173\4\0\1\146\1\144"+
    "\1\146\5\0\34\173\1\0\4\147\1\0\1\143\1\144"+
    "\3\0\1\174\5\0\1\144\6\0\34\174\13\0\1\175"+
    "\14\0\34\175\6\0\2\150\3\0\1\176\5\0\1\150"+
    "\6\0\34\176\7\0\1\177\11\0\1\177\64\0\1\155"+
    "\47\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\12\73"+
    "\1\200\21\73\6\0\1\6\1\62\1\70\1\71\1\56"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\1\73\1\112\32\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\11\73\1\116\22\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\56\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\1\73\1\200\32\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\11\73"+
    "\1\112\22\73\6\0\1\6\1\62\1\70\1\71\1\56"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\4\73\1\112\27\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\20\73\1\200\13\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\56\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\7\73\1\112\24\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\201\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\34\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\32\73"+
    "\1\200\1\73\6\0\1\6\1\62\1\70\1\71\1\56"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\15\73\1\112\16\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\5\73\1\112\26\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\56\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\20\73\1\112\13\73"+
    "\6\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\23\73"+
    "\1\202\10\73\6\0\1\6\1\62\1\70\1\71\1\56"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\5\73\1\200\26\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\15\73\1\200\16\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\56\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\1\203\33\73\6\0"+
    "\1\6\1\62\1\70\1\71\1\56\1\73\1\74\1\75"+
    "\1\76\1\77\1\0\1\62\5\0\1\55\10\73\1\200"+
    "\23\73\6\0\1\6\1\62\1\70\1\71\1\56\1\73"+
    "\1\74\1\75\1\76\1\77\1\0\1\62\5\0\1\55"+
    "\4\73\1\204\27\73\6\0\1\6\1\62\1\70\1\71"+
    "\1\56\1\73\1\74\1\75\1\76\1\77\1\0\1\62"+
    "\5\0\1\55\5\73\1\200\17\73\1\200\6\73\6\0"+
    "\1\6\1\62\1\70\1\71\1\56\1\73\1\74\1\75"+
    "\1\76\1\77\1\0\1\62\5\0\1\55\23\73\1\112"+
    "\10\73\6\0\1\6\1\62\1\70\1\71\1\56\1\73"+
    "\1\74\1\75\1\76\1\77\1\0\1\62\5\0\1\55"+
    "\23\73\1\200\10\73\6\0\1\6\1\62\1\70\1\71"+
    "\1\56\1\73\1\74\1\75\1\76\1\77\1\0\1\62"+
    "\5\0\1\55\4\73\1\200\16\73\1\200\10\73\6\0"+
    "\1\140\1\141\3\0\1\205\5\0\1\141\6\0\34\205"+
    "\6\0\1\6\1\62\1\70\1\71\1\56\1\73\1\74"+
    "\1\75\1\76\1\77\1\0\1\62\5\0\1\55\1\206"+
    "\33\73\30\0\1\51\67\0\1\207\36\0\1\137\1\0"+
    "\2\210\1\137\2\210\3\0\1\137\5\0\1\210\34\137"+
    "\6\0\2\140\2\0\1\211\1\140\2\212\1\0\1\61"+
    "\1\0\1\140\6\0\34\140\6\0\1\140\1\141\1\0"+
    "\1\210\1\213\1\141\2\214\1\0\1\61\1\0\1\141"+
    "\5\0\1\210\34\141\6\0\1\140\1\141\2\0\1\215"+
    "\1\142\2\212\1\0\1\61\1\0\1\141\6\0\34\142"+
    "\6\0\2\143\2\0\1\212\1\143\2\212\1\0\1\61"+
    "\1\0\1\143\6\0\34\143\6\0\1\143\1\144\1\0"+
    "\1\210\1\214\1\144\2\214\1\0\1\61\1\0\1\144"+
    "\5\0\1\210\34\144\6\0\1\143\1\144\2\0\1\212"+
    "\1\145\2\212\1\0\1\61\1\0\1\144\6\0\34\145"+
    "\6\0\2\216\3\0\1\216\5\0\1\216\5\0\1\217"+
    "\1\220\2\216\1\221\30\216\1\0\4\147\1\0\2\150"+
    "\2\0\1\222\1\150\1\222\4\0\1\150\6\0\34\150"+
    "\6\0\1\156\1\157\3\0\1\160\5\0\1\157\6\0"+
    "\34\160\6\0\2\152\1\0\2\55\1\152\2\55\3\0"+
    "\1\152\5\0\1\55\34\152\6\0\1\152\1\153\1\0"+
    "\2\63\1\153\2\63\3\0\1\153\5\0\1\63\34\153"+
    "\6\0\1\152\1\153\1\0\2\55\1\154\2\55\3\0"+
    "\1\153\5\0\1\55\34\154\6\0\2\156\1\0\1\55"+
    "\1\223\1\156\2\60\1\0\1\61\1\0\1\156\5\0"+
    "\1\55\34\156\6\0\1\156\1\157\1\0\1\63\1\224"+
    "\1\157\2\66\1\0\1\61\1\0\1\157\5\0\1\63"+
    "\34\157\6\0\1\156\1\157\1\0\1\55\1\225\1\160"+
    "\2\60\1\0\1\61\1\0\1\157\5\0\1\55\34\160"+
    "\6\0\2\161\1\0\1\55\1\60\1\161\2\60\1\0"+
    "\1\61\1\0\1\161\5\0\1\55\34\161\6\0\1\161"+
    "\1\162\1\0\1\63\1\66\1\162\2\66\1\0\1\61"+
    "\1\0\1\162\5\0\1\63\34\162\6\0\1\161\1\162"+
    "\1\0\1\55\1\60\1\163\2\60\1\0\1\61\1\0"+
    "\1\162\5\0\1\55\34\163\10\0\2\226\1\0\1\165"+
    "\14\0\34\165\7\0\1\137\2\226\1\0\1\166\5\0"+
    "\1\137\6\0\34\166\6\0\1\140\1\141\2\0\1\227"+
    "\1\142\2\212\1\0\1\61\1\0\1\141\6\0\34\142"+
    "\6\0\1\143\1\144\2\0\1\212\1\173\1\230\1\212"+
    "\1\0\1\61\1\0\1\144\6\0\34\173\6\0\1\143"+
    "\1\144\2\0\1\212\1\174\1\212\1\231\1\0\1\61"+
    "\1\0\1\144\6\0\34\174\6\0\2\150\2\0\1\222"+
    "\1\176\1\222\4\0\1\150\6\0\34\176\6\0\1\6"+
    "\1\62\1\70\1\71\1\232\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\34\73\5\0\1\233"+
    "\1\140\1\141\3\233\1\142\5\233\1\141\1\0\5\233"+
    "\34\142\1\233\2\0\1\233\2\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\15\73\1\107\16\73\6\0\1\6"+
    "\1\62\1\70\1\71\1\56\1\73\1\74\1\75\1\76"+
    "\1\77\1\0\1\62\5\0\1\55\1\73\1\112\11\73"+
    "\1\112\20\73\6\0\1\6\1\62\1\70\1\71\1\56"+
    "\1\73\1\74\1\75\1\76\1\77\1\0\1\62\5\0"+
    "\1\55\25\73\1\112\6\73\6\0\1\6\1\62\1\70"+
    "\1\71\1\56\1\73\1\74\1\75\1\76\1\77\1\0"+
    "\1\62\5\0\1\55\13\73\1\112\20\73\34\0\1\234"+
    "\35\0\2\152\3\0\1\152\5\0\1\152\6\0\34\152"+
    "\6\0\2\140\3\0\1\140\5\0\1\140\6\0\34\140"+
    "\6\0\2\143\3\0\1\143\5\0\1\143\6\0\34\143"+
    "\6\0\2\156\3\0\1\156\5\0\1\156\6\0\34\156"+
    "\6\0\2\161\3\0\1\161\5\0\1\161\6\0\34\161"+
    "\6\0\2\140\3\0\1\235\5\0\1\140\6\0\34\235"+
    "\6\0\2\216\3\0\1\216\5\0\1\216\6\0\34\216"+
    "\6\0\2\216\3\0\1\216\5\0\1\216\6\0\1\216"+
    "\1\236\32\216\6\0\2\216\3\0\1\216\5\0\1\216"+
    "\6\0\4\216\1\237\27\216\6\0\2\240\3\0\1\240"+
    "\5\0\1\240\6\0\34\240\6\0\1\140\1\141\3\0"+
    "\1\241\5\0\1\141\6\0\34\241\6\0\1\156\1\157"+
    "\3\0\1\242\5\0\1\157\6\0\34\242\6\0\2\140"+
    "\3\0\1\243\5\0\1\140\6\0\34\243\6\0\2\143"+
    "\3\0\1\244\5\0\1\143\6\0\34\244\6\0\2\143"+
    "\3\0\1\245\5\0\1\143\6\0\34\245\5\0\1\246"+
    "\1\140\1\141\3\246\1\142\5\246\1\141\1\0\5\246"+
    "\34\142\1\246\2\0\1\246\27\0\1\51\36\0\2\140"+
    "\2\0\1\215\1\235\2\212\1\0\1\61\1\0\1\140"+
    "\6\0\34\235\6\0\2\216\3\0\1\216\5\0\1\216"+
    "\6\0\2\216\1\247\31\216\6\0\2\216\3\0\1\216"+
    "\5\0\1\216\6\0\4\216\1\250\27\216\6\0\2\240"+
    "\2\0\1\222\1\240\1\222\4\0\1\240\6\0\34\240"+
    "\6\0\1\140\1\141\2\0\1\211\1\241\2\212\1\0"+
    "\1\61\1\0\1\141\6\0\34\241\6\0\1\156\1\157"+
    "\1\0\1\55\1\223\1\242\2\60\1\0\1\61\1\0"+
    "\1\157\5\0\1\55\34\242\6\0\2\140\2\0\1\227"+
    "\1\235\2\212\1\0\1\61\1\0\1\140\6\0\34\235"+
    "\6\0\2\143\2\0\1\212\1\244\1\230\1\212\1\0"+
    "\1\61\1\0\1\143\6\0\34\244\6\0\2\143\2\0"+
    "\1\212\1\245\1\212\1\231\1\0\1\61\1\0\1\143"+
    "\6\0\34\245\6\0\2\216\3\0\1\216\5\0\1\216"+
    "\6\0\3\216\1\251\30\216\6\0\2\216\3\0\1\216"+
    "\5\0\1\216\6\0\5\216\1\247\26\216\5\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[7488];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\4\0\1\11\2\1\2\11\3\1\1\11\1\1\4\11"+
    "\23\1\1\11\1\1\12\0\1\1\5\0\1\1\2\0"+
    "\1\1\6\0\27\1\1\0\1\1\1\11\1\1\2\0"+
    "\4\1\1\0\1\1\1\0\1\11\3\0\3\1\1\11"+
    "\6\1\1\11\2\1\1\11\7\1\1\11\1\1\1\0"+
    "\5\1\6\0\2\1\1\11\2\1\3\0\1\1\1\0"+
    "\1\1\3\0\1\11\1\0\11\1\1\11\3\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[169];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /** For the backwards DFA of general lookahead statements */
  private boolean [] zzFin = new boolean [ZZ_BUFFERSIZE+1];

  /* user code: */

public static final int ALPHANUM = 0;
public static final int APOSTROPHE = 1;
public static final int ACRONYM = 2;
public static final int COMPANY = 3;
public static final int EMAIL = 4;
public static final int HOST = 5;
public static final int NUM = 6;
public static final int CJ = 7;
public static final int ACRONYM_DEP = 8;
public static final int PUNCTUATION = 9;
public static final int PARENTHESIS = 10;
public static final int COMPOSITE_WORD = 11;
public static final int COLON = 12;
public static final int MATH_SYMBOL = 13;
public static final int ABBREVIATION = 14;
public static final int ENDING = 15;
public static final int SINGLE_QUOTE = 16;
public static final int SPLIT_WORD = 17;
public static final int CONJUNCTION = 18;
public static final int DOTDOTDOT = 19;
public static final int ABBREVIATION_SWEDISH = 20;
public static final int ABBREVIATEDYEAR = 21;
public static final int LISTITEM = 22;
public static final int PARAGRAPH = 23;
public static final int ACRONYM_SINGLE = 24;
public static final int ABBREVIATION_SWEDISH_MONTHS = 25;



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Tokenizer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  Tokenizer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1708) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int getNextToken() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 15: 
          { return LISTITEM;
          }
        case 29: break;
        case 20: 
          { return COMPANY;
          }
        case 30: break;
        case 13: 
          { return HOST;
          }
        case 31: break;
        case 3: 
          { return SINGLE_QUOTE;
          }
        case 32: break;
        case 10: 
          { return CJ;
          }
        case 33: break;
        case 1: 
          { /* Break so we don't hit fall-through warning: */ break;/* ignore */
          }
        case 34: break;
        case 9: 
          { return ENDING;
          }
        case 35: break;
        case 25: 
          { return ACRONYM;
          }
        case 36: break;
        case 12: 
          { return NUM;
          }
        case 37: break;
        case 19: 
          { return ABBREVIATION;
          }
        case 38: break;
        case 5: 
          { return PUNCTUATION;
          }
        case 39: break;
        case 11: 
          { return APOSTROPHE;
          }
        case 40: break;
        case 4: 
          { return COLON;
          }
        case 41: break;
        case 28: 
          // lookahead expression with fixed base length
          zzMarkedPos = zzStartRead + 4;
          { return ABBREVIATION_SWEDISH_MONTHS;
          }
        case 42: break;
        case 21: 
          { return ABBREVIATEDYEAR;
          }
        case 43: break;
        case 16: 
          { return DOTDOTDOT;
          }
        case 44: break;
        case 26: 
          // lookahead expression with fixed lookahead length
          yypushback(1);
          { return ABBREVIATION_SWEDISH;
          }
        case 45: break;
        case 17: 
          // lookahead expression with fixed base length
          zzMarkedPos = zzStartRead + 2;
          { return ACRONYM_SINGLE;
          }
        case 46: break;
        case 7: 
          { return MATH_SYMBOL;
          }
        case 47: break;
        case 8: 
          { return PARAGRAPH;
          }
        case 48: break;
        case 18: 
          { return COMPOSITE_WORD;
          }
        case 49: break;
        case 27: 
          { return EMAIL;
          }
        case 50: break;
        case 6: 
          { return PARENTHESIS;
          }
        case 51: break;
        case 24: 
          // general lookahead, find correct zzMarkedPos
          { int zzFState = 1;
            int zzFPos = zzStartRead;
            if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
            boolean zzFinL[] = zzFin;
            while (zzFState != -1 && zzFPos < zzMarkedPos) {
              if ((zzAttrL[zzFState] & 1) == 1) { zzFinL[zzFPos] = true; } 
              zzInput = zzBufferL[zzFPos++];
              zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
            }
            if (zzFState != -1 && (zzAttrL[zzFState] & 1) == 1) { zzFinL[zzFPos] = true; } 

            zzFState = 2;
            zzFPos = zzMarkedPos;
            while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
              zzInput = zzBufferL[--zzFPos];
              zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
            };
            zzMarkedPos = zzFPos;
          }
          { return SPLIT_WORD;
          }
        case 52: break;
        case 23: 
          // general lookahead, find correct zzMarkedPos
          { int zzFState = 1;
            int zzFPos = zzStartRead;
            if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
            boolean zzFinL[] = zzFin;
            while (zzFState != -1 && zzFPos < zzMarkedPos) {
              if ((zzAttrL[zzFState] & 1) == 1) { zzFinL[zzFPos] = true; } 
              zzInput = zzBufferL[zzFPos++];
              zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
            }
            if (zzFState != -1 && (zzAttrL[zzFState] & 1) == 1) { zzFinL[zzFPos] = true; } 

            zzFState = 3;
            zzFPos = zzMarkedPos;
            while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
              zzInput = zzBufferL[--zzFPos];
              zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
            };
            zzMarkedPos = zzFPos;
          }
          { return SPLIT_WORD;
          }
        case 53: break;
        case 22: 
          { return ACRONYM_DEP;
          }
        case 54: break;
        case 2: 
          { return ALPHANUM;
          }
        case 55: break;
        case 14: 
          // lookahead expression with fixed lookahead length
          yypushback(1);
          { return SPLIT_WORD;
          }
        case 56: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return YYEOF;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
