/**
 * Copyright 2007 Charlie Hubbard and Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this content except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson.transformer;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class provided out of the box to encode characters that HTML can't support
 * natively like &lt;, &gt;, &amp;, or &quot;.  This will scan the value passed to the transform
 * method and replace any of these special characters with the HTML encoded equivalent.  This
 * method will NOT work for HTML text because it will blindly encode all characters it sees which
 * means it will strip out any HTML tags.
 */
public class HtmlEncoderTransformer extends AbstractTransformer {

    private static final Map<Integer, String> htmlEntities = new HashMap<Integer, String>();

    public HtmlEncoderTransformer() {
        if (htmlEntities.isEmpty()) {
            htmlEntities.put(34, "&quot;");       // " - double-quote
            htmlEntities.put(38, "&amp;");        // & - ampersand
//            htmlEntities.put( 39, "&apos;");        // ' - apostrophe
            htmlEntities.put(60, "&lt;");         // < - less-than
            htmlEntities.put(62, "&gt;");         // > - greater-than
            htmlEntities.put(160, "&nbsp;");      // non-breaking space
            htmlEntities.put(169, "&copy;");      // © - copyright
            htmlEntities.put(174, "&reg;");       // ® - registered trademark
            htmlEntities.put(192, "&Agrave;");    // А - uppercase A, grave accent
            htmlEntities.put(193, "&Aacute;");    // Б - uppercase A, acute accent
            htmlEntities.put(194, "&Acirc;");      // В - uppercase A, circumflex accent
            htmlEntities.put(195, "&Atilde;");    // Г - uppercase A, tilde
            htmlEntities.put(196, "&Auml;");      // Д - uppercase A, umlaut
            htmlEntities.put(197, "&Aring;");     // Е - uppercase A, ring
            htmlEntities.put(198, "&AElig;");     // Ж - uppercase AE
            htmlEntities.put(199, "&Ccedil;");    // З - uppercase C, cedilla
            htmlEntities.put(200, "&Egrave;");     // И - uppercase E, grave accent
            htmlEntities.put(201, "&Eacute;");     // Й - uppercase E, acute accent
            htmlEntities.put(202, "&Ecirc;");     // К - uppercase E, circumflex accent
            htmlEntities.put(203, "&Euml;");      // Л - uppercase E, umlaut
            htmlEntities.put(204, "&Igrave;");    // М - uppercase I, grave accent
            htmlEntities.put(205, "&Iacute;");    // Н - uppercase I, acute accent
            htmlEntities.put(206, "&Icirc;");     // О - uppercase I, circumflex accent
            htmlEntities.put(207, "&Iuml;");      // П - uppercase I, umlaut
            htmlEntities.put(208, "&ETH;");       // Р - uppercase Eth, Icelandic
            htmlEntities.put(209, "&Ntilde;");     // С - uppercase N, tilde
            htmlEntities.put(210, "&Ograve;");     // Т - uppercase O, grave accent
            htmlEntities.put(211, "&Oacute;");     // У - uppercase O, acute accent
            htmlEntities.put(212, "&Ocirc;");     // Ф - uppercase O, circumflex accent
            htmlEntities.put(213, "&Otilde;");     // Х - uppercase O, tilde
            htmlEntities.put(214, "&Ouml;");       // Ц - uppercase O, umlaut
            htmlEntities.put(216, "&Oslash;");     // Ш - uppercase O, slash
            htmlEntities.put(217, "&Ugrave;");     // Щ - uppercase U, grave accent
            htmlEntities.put(218, "&Uacute;");     // Ъ - uppercase U, acute accent
            htmlEntities.put(219, "&Ucirc;");     // Ы - uppercase U, circumflex accent
            htmlEntities.put(220, "&Uuml;");      // Ь - uppercase U, umlaut
            htmlEntities.put(221, "&Yacute;");     // Э - uppercase Y, acute accent
            htmlEntities.put(222, "&THORN;");      // Ю - uppercase THORN, Icelandic
            htmlEntities.put(223, "&szlig;");      // Я - lowercase sharps, German
            htmlEntities.put(224, "&agrave;");     // а - lowercase a, grave accent
            htmlEntities.put(225, "&aacute;");     // б - lowercase a, acute accent
            htmlEntities.put(226, "&acirc;");      // в - lowercase a, circumflex accent
            htmlEntities.put(227, "&atilde;");     // г - lowercase a, tilde
            htmlEntities.put(228, "&auml;");       // д - lowercase a, umlaut
            htmlEntities.put(229, "&aring;");      // е - lowercase a, ring
            htmlEntities.put(230, "&aelig;");      // ж - lowercase ae
            htmlEntities.put(231, "&ccedil;");    // з - lowercase c, cedilla
            htmlEntities.put(232, "&egrave;");     // и - lowercase e, grave accent
            htmlEntities.put(233, "&eacute;");     // й - lowercase e, acute accent
            htmlEntities.put(234, "&ecirc;");      // к - lowercase e, circumflex accent
            htmlEntities.put(235, "&euml;");       // л - lowercase e, umlaut
            htmlEntities.put(236, "&igrave;");    // м - lowercase i, grave accent
            htmlEntities.put(237, "&iacute;");     // н - lowercase i, acute accent
            htmlEntities.put(238, "&icirc;");      // о - lowercase i, circumflex accent
            htmlEntities.put(239, "&iuml;");       // п - lowercase i, umlaut
            htmlEntities.put(240, "&eth;");        // р - lowercase eth, Icelandic
            htmlEntities.put(241, "&ntilde;");     // с - lowercase n, tilde
            htmlEntities.put(242, "&ograve;");     // т - lowercase o, grave accent
            htmlEntities.put(243, "&oacute;");     // у - lowercase o, acute accent
            htmlEntities.put(244, "&ocirc;");      // ф - lowercase o, circumflex accent
            htmlEntities.put(245, "&otilde;");     // х - lowercase o, tilde
            htmlEntities.put(246, "&ouml;");       // ц - lowercase o, umlaut
            htmlEntities.put(248, "&oslash;");     // ш - lowercase o, slash
            htmlEntities.put(249, "&ugrave;");     // щ - lowercase u, grave accent
            htmlEntities.put(250, "&uacute;");     // ъ - lowercase u, acute accent
            htmlEntities.put(251, "&ucirc;");      // ы - lowercase u, circumflex accent
            htmlEntities.put(252, "&uuml;");       // ь - lowercase u, umlaut
            htmlEntities.put(253, "&yacute;");     // э - lowercase y, acute accent
            htmlEntities.put(254, "&thorn;");      // ю - lowercase thorn, Icelandic
            htmlEntities.put(255, "&yuml;");       // я - lowercase y, umlaut
            htmlEntities.put(8364, "&euro;");      // Euro symbol
        }
    }

    public void transform(Object value) {

        String val = value.toString();
        getContext().write("\"");
        for (int i = 0; i < val.length(); ++i) {
            int intVal = (int) val.charAt(i);
            if (htmlEntities.containsKey(intVal)) {
                getContext().write(htmlEntities.get(intVal));
            } else if (intVal > 128) {
                getContext().write("&#");
                getContext().write(String.valueOf(intVal));
                getContext().write(";");
            } else {
                getContext().write(String.valueOf(val.charAt(i)));
            }
        }
        getContext().write("\"");

    }
}
