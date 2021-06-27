package be.hogent.svanimpe.rtf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;

/*
 * Converter for the Rich Text Format.
 * Converts an editor pane's selected content to RTF.
 *
 * For more information on RTF, see the RTF specification at:
 * http://www.microsoft.com/en-us/download/details.aspx?id=10725
 */
public class RtfConverter {

    private JEditorPane pane;

    /*
     * List of fonts used in the document.
     * This list will be filled as the content is built.
     */
    private List<String> fonts;

    /*
     * List of colors used in the document.
     * This list will be filled as the content is built.
     */
    private List<Color> colors;

    public RtfConverter(JEditorPane pane) {
        if (null == pane) {
            throw new NullPointerException("Pane cannot be null");
        } else {
            this.pane = pane;
            fonts = new ArrayList<>();
            colors = new ArrayList<>();
        }
    }

    /*
     * Convert the pane's selected content to RTF.
     * This will first convert the content and then wrap it in a document.
     */
    public String convertSelection() {
        return buildDocument(buildContent());
    }

    /*
     * Build an RTF document using the given RTF content.
     * This method will use the filled lists to build the font and color tables.
     */
    private String buildDocument(String content) {
        StringBuilder document = new StringBuilder();

        // Add the header and start the font table
        document.append("{\\rtf1\\ansi{\\fonttbl");

        // Add every used font to the table
        for (int i = 0; i < fonts.size(); i++) {
            document.append("{\\f");
            document.append(i);
            document.append(" ");
            document.append(fonts.get(i));
            document.append(";}");
        }

        // Close the font table and start the color table
        document.append("}{\\colortbl");

        // Add every used color to the table
        for (Color color : colors) {
            document.append("\\red");
            document.append(color.getRed());
            document.append("\\green");
            document.append(color.getGreen());
            document.append("\\blue");
            document.append(color.getBlue());
            document.append(";");
        }

        // Close the color table
        document.append("}");

        // Add the content
        document.append(content);

        // Close the document
        document.append("}");

        return document.toString();
    }

    /*
     * Build the RTF content based on the pane's selected content.
     */
    private String buildContent() {

        final FontColorSettings fcs = MimeLookup.getLookup(
                MimePath.get(pane.getContentType())
        ).lookup(FontColorSettings.class);
        final Document document = pane.getDocument();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument) document).readLock();
        }
        final TokenHierarchy<Document> documentHierarchy = TokenHierarchy.get(document);

        final TokenSequence<?> ts = documentHierarchy.
                tokenSequence();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument) document).readUnlock();
        }

                // Forward to start of selected text
        ts.move(pane.getSelectionStart());

        StringBuilder content = new StringBuilder();
        // Process each token between start and end of selection
        while (ts.moveNext() && ts.offset() < pane.getSelectionEnd()) {
            processToken(ts.token(), fcs, content);
        }



        // Get both the bottom and top highlights from the manager
        // Note that HighlightingManager is not in the public API, we have an implementation dependency here
//        Optional<HighlightsSequence> bottom = Optional.ofNullable(HighlightingManager.getInstance(pane).getBottomHighlights().getHighlights(pane.getSelectionStart(), pane.getSelectionEnd()));
//        Optional<HighlightsSequence> top = Optional.ofNullable(HighlightingManager.getInstance(pane).getTopHighlights().getHighlights(pane.getSelectionStart(), pane.getSelectionEnd()));

        // Merge all highlights together using an OffsetsBag
        //OffsetsBag bag = new OffsetsBag(pane.getDocument(), true);

//        bottom.ifPresent(highlightsSequence -> bag.addAllHighlights(highlightsSequence));
//        top.ifPresent(highlightsSequence -> bag.addAllHighlights(highlightsSequence));

        // Retrieve the resulting highlights
//        HighlightsSequence hs = bag.getHighlights(pane.getSelectionStart(), pane.getSelectionEnd());

        // Iterate over the highlights (tokens)
//        while (hs.moveNext()) {

            // Try to get the text for this token
//            String text = "";
//            try {
//                text = pane.getText(hs.getStartOffset(), hs.getEndOffset() - hs.getStartOffset());
//            } catch (BadLocationException ex) {
//                Util.printException(ex);
//            }

            // Convert this token to RTF and add it to the content
//            buildToken(text, hs.getAttributes(), content);
//        }

        return content.toString();
    }

    /*
     * Convert a token to RTF and add it to the given StringBuilder.
     * This method will also collect the fonts and colors used in the document.
     * These will later be used to build the font and color tables.
     */
    private void buildToken(String text, AttributeSet as, StringBuilder sb) {

        // Find the color used to display this token
        // Note that we don't use StyleConstants.getForeground(AttributeSet) here
        // This would replace a missing color with a default value
        Color color = (Color) as.getAttribute(StyleConstants.Foreground);

        // If no color is found, use the pane's default foreground color
        if (null == color) {
            color = pane.getForeground();
        }

        // Add the color to the collection if it's not already present
        if (!colors.contains(color)) {
            colors.add(color);
        }

        // Find the font used to display this token
        // Note that we don't use StyleConstants.getFontFamily(AttributeSet) here
        // This would replace a missing font with a default value
        // Also note that only the font family is preserved
        // The font face (bold, italic) is lost, but separate bold and italic formatting is preserved (see below)
        String font = (String) as.getAttribute(StyleConstants.FontFamily);

        // If no font is found, use the pane's default font
        if (null == font) {
            font = pane.getFont().getFamily();
        }

        // Add the font to the collection if it's not already present
        if (!fonts.contains(font)) {
            fonts.add(font);
        }

        // Find the font size used to display this token
        // Note that we don't use StyleConstants.getFontSize(AttributeSet) here
        // This would replace a missing font size with a default value
        Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);

        // If no font size is found, use the pane's default font size
        if (null == fontSize) {
            fontSize = pane.getFont().getSize();
        }

        // Check to see if bold or italic formatting is used
        boolean bold = StyleConstants.isBold(as);
        boolean italic = StyleConstants.isItalic(as);

        // Check to see if underline or strikethrough formatting is used
        // Note that we don't use StyleConstants.isStrikeThrough(AttributeSet) or StyleConstants.isUnderline(AttributeSet) here
        // These methodes expect an attribute of type Boolean, whereas in this case they are of type Color
        boolean underline = null != as.getAttribute(StyleConstants.Underline);
        boolean strikethrough = null != as.getAttribute(StyleConstants.StrikeThrough);

        // Start a new group for this token, lookup the font number and set it
        sb.append("{\\f");
        sb.append(fonts.indexOf(font));

        // Set the font size
        // Note that RTF uses half points instead of points by default
        sb.append("\\fs");
        sb.append(fontSize << 1); // double the size

        // Lookup the color number and set it
        sb.append("\\cf");
        sb.append(colors.indexOf(color));

        // Set bold, italic, underline and strikethrough formatting
        if (bold) {
            sb.append("\\b");
        }
        if (italic) {
            sb.append("\\i");
        }
        if (underline) {
            sb.append("\\ul");
        }
        if (strikethrough) {
            sb.append("\\strike");
        }

        // Add a space as an extra delimiter for the control words above
        sb.append(" ");

        // Add the token itself
        charsToRTF(text, sb);

        // Close the group
        sb.append("}");

    }

    /*
     * Convert a String to RTF and add it to the given StringBuilder.
     * Unicode characters above 8-bit are escaped as backslash-u followed by a 16-bit signed integer.
     * This integer is found by converting the char value to a short value.
     * Characters that have a special meaning in RTF (backslash and curly brackets) are escaped by putting an extra backslash in front of them.
     * The newline and tab characters are replaced by their RTF equivalents.
     */
    private static void charsToRTF(String chars, StringBuilder sb) {
        int length = chars.length();
        for (int i = 0; i < length; i++) {
            char charAt = chars.charAt(i);
            if (0xFF < charAt) {
                sb.append("{\\u");
                sb.append((short) charAt);
                sb.append("}");
            } else if (charAt == '\\') {
                sb.append("\\\\");
            } else if (charAt == '{') {
                sb.append("\\{");
            } else if (charAt == '}') {
                sb.append("\\}");
            } else if (charAt == '\n') {
                sb.append("{\\line}");
            } else if (charAt == '\t') {
                sb.append("{\\tab}");
            } else {
                sb.append(charAt);
            }
        }
    }

    private void processToken(Token token, FontColorSettings fcs, StringBuilder sb) {
        final String tokenText = token.text().toString();

        // Get fontcolors for token
        AttributeSet as = fcs.getTokenFontColors(token.id().name());

        // If non-existing...
        if (null == as) {
            // ...try to get from its category
            as = fcs.getTokenFontColors(token.id().primaryCategory());
        }

        if (null == as){// || token.id().primaryCategory().equals("whitespace")) {
            emit(sb, tokenText);
        } else {
            styleAndEmit(sb, tokenText, as);
        }

    }

    private final void styleAndEmit(final StringBuilder sb,
            final String tokenText, final AttributeSet as) {
        //final StringBuilder styleString = new StringBuilder();
        buildToken(tokenText,as,sb);

//        final Color fg = (Color) as.getAttribute(StyleConstants.Foreground);
//        if (fg != null) {
//            emit(styleString, "color:", getHtmlColorString(fg), ";");
//        }
//
//        final Color bg = (Color) as.getAttribute(StyleConstants.Background);
//
//        if (bg != null) {
//            emit(styleString, "background:", getHtmlColorString(bg), ";");
//        }
//
//        // Only wrap style tag if we have a style to apply
//        if (styleString.length() > 0) {
//            emit(sb, "<span style=\"", styleString.toString(), "\">", tokenText,
//                    "</span>");
//        } else {
//            emit(sb, tokenText);
//        }
    }


    private static void emit(final StringBuilder sb, final String... tokenTexts) {
        for (String token : tokenTexts) {
            sb.append(token);
        }
    }

}
