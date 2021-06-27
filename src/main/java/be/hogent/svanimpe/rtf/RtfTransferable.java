package be.hogent.svanimpe.rtf;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/*
 * Transferable object for RTF data.
 */
public class RtfTransferable implements Transferable {

    /*
     * DataFlavor for the Rich Text Format.
     * This DataFlavor has a MIME type of text/rtf and uses an 8-bit character set.
     * The representation class will default to java.io.InputStream.
     */
    private final DataFlavor rtfFlavor = new DataFlavor("text/rtf; charset=ISO-8859-1", "Rich Text Format");

    private final String data;

    public RtfTransferable(String data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {rtfFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(rtfFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(rtfFlavor)) {
            // Transform the String to a byte[] based on the character set, and return it as an InputStream
            return new ByteArrayInputStream(data.getBytes(StandardCharsets.ISO_8859_1));
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
