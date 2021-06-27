package be.hogent.svanimpe.rtf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JEditorPane;

import static org.junit.jupiter.api.Assertions.*;

class RtfConverterTest {

    private RtfConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RtfConverter(new JEditorPane());
    }

    @Test
    void convertSelection() {
//        converter.convertSelection();
    }
}
