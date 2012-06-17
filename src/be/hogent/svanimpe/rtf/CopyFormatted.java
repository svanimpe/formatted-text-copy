package be.hogent.svanimpe.rtf;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "be.hogent.svanimpe.rtf.CopyFormatted")
@ActionRegistration(displayName = "#CTL_CopyFormatted")
@ActionReferences({
    @ActionReference(path = "Menu/Edit", position = 1150)
})
@Messages("CTL_CopyFormatted=Copy Formatted")
public final class CopyFormatted implements ActionListener {

    private final EditorCookie context;

    public CopyFormatted(EditorCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        try {
            // Make sure an editor pane is open and that it has some selected content
            JEditorPane[] panes = context.getOpenedPanes();
            if (panes == null || panes[0].getSelectionStart() == panes[0].getSelectionEnd()) {
                Util.println("No Selection Found");
            } else {
                // Initialize the converter and ask it to convert the selected content
                RtfConverter converter = new RtfConverter(panes[0]);
                String rtf = converter.convertSelection();
                
                // Print the result to an output window for reference
                Util.println("RTF Output:\n" + rtf);
                
                // Wrap the result in a Transferable object and paste it on the clipboard
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new RtfTransferable(rtf), null);
            }
        } catch (Exception ex) {
            // Catch every exception and write a stack trace to the output window, otherwise the plugin appears to do nothing
            Util.printException(ex);
        }
    }
}
