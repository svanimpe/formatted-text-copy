package be.hogent.svanimpe.rtf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RtfTransferableTest {

    private RtfTransferable rtfTransferable;

    @BeforeEach
    void setUp() {
        rtfTransferable = new RtfTransferable("Hello world");
    }

    @Test
    void getTransferDataFlavors() {
        DataFlavor[] flavors = rtfTransferable.getTransferDataFlavors();
        assertNotNull(flavors);
        assertEquals(1, flavors.length);
        assertEquals(new DataFlavor("text/rtf; charset=ISO-8859-1", "Rich Text Format"), flavors[0]);
    }

    @Test
    void isDataFlavorSupported() {
        assertFalse(rtfTransferable.isDataFlavorSupported(DataFlavor.imageFlavor));
        assertFalse(rtfTransferable.isDataFlavorSupported(DataFlavor.stringFlavor));
        assertFalse(rtfTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
        assertFalse(rtfTransferable.isDataFlavorSupported(DataFlavor.allHtmlFlavor));
        assertTrue(rtfTransferable.isDataFlavorSupported(new DataFlavor("text/rtf; charset=ISO-8859-1", "Rich Text Format")));
        assertTrue(rtfTransferable.isDataFlavorSupported(new DataFlavor("text/rtf; charset=UTF-8", "Rich Text Format")));
    }

    @Test
    void getTransferDataUnsupportedFlavor() {
        Executable executable = () -> rtfTransferable.getTransferData(DataFlavor.stringFlavor);
        assertThrows(UnsupportedFlavorException.class, executable);
    }

    @Test
    void getTransferData() throws IOException, UnsupportedFlavorException {
        Object result = rtfTransferable.getTransferData(
                new DataFlavor("text/rtf; charset=UTF-8", "Rich Text Format")
        );
        assertEquals(ByteArrayInputStream.class, result.getClass());
    }
}
