package be.hogent.svanimpe;

import org.openide.windows.IOProvider;

public class Util {
    
    /* 
     * Print a message to the output window.
     * This will open a window if necessary.
     */
    public static void println(String message) {
        IOProvider.getDefault().getIO("Formatted Text Copy", false).getOut().println(message);
    }
    
    /* 
     * Print a stack trace to the output window.
     * This will open a window if necessary.
     */
    public static void printException(Exception ex) {
        ex.printStackTrace(IOProvider.getDefault().getIO("Formatted Text Copy", false).getOut());
    }
}
