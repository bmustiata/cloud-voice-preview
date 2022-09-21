package com.germaniumhq.spark.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.util.function.Consumer;

public class DocumentChangeListener implements DocumentListener {
    private final Consumer<String> listener;

    public DocumentChangeListener(Consumer<String> listener) {
        this.listener = listener;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.processChange(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.processChange(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.processChange(e);
    }

    private void processChange(DocumentEvent e) {
        try {
            String newDocumentText = e.getDocument().getText(0, e.getDocument().getLength());
            this.listener.accept(newDocumentText);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

}
