package com.germaniumhq.spark;

import com.germaniumhq.spark.ui.DocumentChangeListener;
import com.germaniumhq.spark.voice.*;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<VoiceProvider> voiceProviders = new ArrayList<>();
    private static VoiceProvider selectedVoiceProvider;
    private static VoiceCharacter selectedVoiceCharacter;
    private static VoiceSentiment selectedVoiceSentiment;

    static {
        voiceProviders.add(new IbmVoiceProvider());
        voiceProviders.add(new AzureVoiceProvider());
    }

    public static void main(String[] args) {
        JFrame sectionFrame = new JFrame();
        sectionFrame.setSize(600, 600);
        BorderLayout borderLayout = new BorderLayout();
        sectionFrame.setLayout(borderLayout);
        sectionFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("voice", createVoicePanel());
        tabs.add("settings", createSettingsPanel());

        sectionFrame.getContentPane().add(tabs, BorderLayout.CENTER);

        sectionFrame.setVisible(true); // frame visible
    }

    private static JComponent createVoicePanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        JComboBox<VoiceProvider> voiceProviderComboBox = new JComboBox<>();
        JComboBox<VoiceLanguage> voiceLanguageComboBox = new JComboBox<>();
        JComboBox<VoiceCharacter> voiceCharacterComboBox = new JComboBox<>();
        JComboBox<VoiceSentiment> voiceSentimentComboBox = new JComboBox<>();
        JLabel voiceLabel = new JLabel();
        JButton playSaveButton = new JButton("▶️& \uD83D\uDCBE");
        JTextArea textTextArea = new JTextArea();

        voiceProviders.forEach(voiceProviderComboBox::addItem);
        voiceProviderComboBox.addActionListener(e -> {
            selectedVoiceProvider = (VoiceProvider) voiceProviderComboBox.getSelectedItem();
            selectedVoiceProvider.refresh();

            voiceLanguageComboBox.removeAllItems();
            selectedVoiceProvider.getAvailableLanguages().forEach(voiceLanguageComboBox::addItem);
            voiceLanguageComboBox.invalidate();
        });

        voiceLanguageComboBox.addActionListener(e -> {
            VoiceLanguage voiceLanguage = (VoiceLanguage) voiceLanguageComboBox.getSelectedItem();
            List<VoiceCharacter> characters  = selectedVoiceProvider.getAvailableCharacters(voiceLanguage);

            voiceCharacterComboBox.removeAllItems();
            characters.forEach(voiceCharacterComboBox::addItem);
            voiceCharacterComboBox.invalidate();
        });

        voiceCharacterComboBox.addActionListener(e -> {
            selectedVoiceCharacter = (VoiceCharacter) voiceCharacterComboBox.getSelectedItem();
            List<VoiceSentiment> sentiments = selectedVoiceProvider.getAvailableSentiments(selectedVoiceCharacter);

            voiceSentimentComboBox.removeAllItems();
            sentiments.forEach(voiceSentimentComboBox::addItem);
            voiceSentimentComboBox.invalidate();

            if (selectedVoiceCharacter != null) {
                voiceLabel.setText(selectedVoiceCharacter.getId());
            }
        });

        voiceSentimentComboBox.addActionListener(e -> {
            selectedVoiceSentiment = (VoiceSentiment) voiceSentimentComboBox.getSelectedItem();
        });

        playSaveButton.addActionListener(e -> {
            try (InputStream inputStream = selectedVoiceProvider.renderVoice(selectedVoiceCharacter, selectedVoiceSentiment, 1f, textTextArea.getText())) {
                SoundMediaPlayer soundMediaplayer = new SoundMediaPlayer();
                byte[] data = inputStream.readAllBytes();

                Path targetFolder = Paths.get(System.getProperty("user.home"));
                Path targetPath = targetFolder.resolve("tts-to-voice.mp3");
                Files.write(targetPath, data, StandardOpenOption.CREATE);

                System.out.println("wrote: " + targetPath);

                soundMediaplayer.play(data);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        });

        // layout things in the page
        result.add(new JLabel("Provider:"), labelColumn(0, 0));
        result.add(voiceProviderComboBox, valueColumn(1, 0, 3));

        result.add(new JLabel("Character:"), labelColumn(0, 1));
        result.add(voiceCharacterComboBox, valueColumn(1, 1));

        result.add(new JLabel("Language"), labelColumn(2, 1));
        result.add(voiceLanguageComboBox, labelColumn(3, 1));

        result.add(new JLabel("Sentiment:"), labelColumn(0, 2));
        result.add(voiceSentimentComboBox, valueColumn(1, 2, 3));

        result.add(new JLabel("Pitch:"), labelColumn(0, 3));
        result.add(new JTextField(), valueColumn(1, 3, 3));

        result.add(new JLabel("Text:"), labelColumn(0, 4));
        GridBagConstraints constraints = valueColumn(1, 4, 3);
        constraints.weighty = 1;

        textTextArea.setLineWrap(true);
        result.add(textTextArea, constraints);

        result.add(playSaveButton, labelColumn(0, 5));
        result.add(voiceLabel, labelColumn(1, 5));

        return result;
    }

    private static JComponent createSettingsPanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        // IBM URL:
        result.add(new JLabel("IBM Endpoint:"), labelColumn(0, 0));

        // update the settings whenever the value change
        JTextField ibmEndpointTextField = new JTextField();

        ibmEndpointTextField.setText(Settings.INSTANCE.getIbmEndpoint());
        ibmEndpointTextField.getDocument().addDocumentListener(new DocumentChangeListener(newText -> {
            Settings.INSTANCE.setIbmEndpoint(newText);
        }));
        result.add(ibmEndpointTextField, valueColumn(1, 0));

        // IBM Token
        result.add(new JLabel("IBM Token:"), labelColumn(0, 1));
        JTextField ibmTokenTextField = new JTextField();
        ibmTokenTextField.setText(Settings.INSTANCE.getIbmToken());
        ibmTokenTextField.getDocument().addDocumentListener(new DocumentChangeListener(newText -> {
            Settings.INSTANCE.setIbmToken(newText);
        }));

        result.add(ibmTokenTextField, valueColumn(1, 1));

        result.add(new JLabel("Azure Endpoint:"), labelColumn(0, 2));
        JTextField azureEndpointTextField = new JTextField();
        azureEndpointTextField.setText(Settings.INSTANCE.getAzureEndpoint());
        azureEndpointTextField.getDocument().addDocumentListener(new DocumentChangeListener(newText -> {
            Settings.INSTANCE.setAzureEndpoint(newText);
        }));

        result.add(azureEndpointTextField, valueColumn(1, 2));

        result.add(new JLabel("Azure Token:"), labelColumn(0, 3));
        JTextField azureTokenTextField = new JTextField();
        azureTokenTextField.setText(Settings.INSTANCE.getAzureToken());
        azureTokenTextField.getDocument().addDocumentListener(new DocumentChangeListener(newText -> {
            Settings.INSTANCE.setAzureToken(newText);
        }));
        result.add(azureTokenTextField, valueColumn(1, 3));

        GridBagConstraints constraints = labelColumn(0, 4);
        constraints.weighty = 1;
        JPanel filler = new JPanel();
        result.add(filler, constraints);

        JButton saveButton = new JButton("save");
        saveButton.addActionListener((e) -> {
            System.out.println("settings saved");
            Settings.saveToFile(Settings.SETTINGS_FILE_NAME, Settings.INSTANCE);
        });
        result.add(saveButton, labelColumn(0, 5));

        return result;
    }

    private static GridBagConstraints valueColumn(int gridx, int gridy) {
        return valueColumn(gridx, gridy, 1);
    }

    private static GridBagConstraints valueColumn(int gridx, int gridy, int colspan) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.weightx = 1;
        constraints.fill = 1;
        constraints.gridwidth = colspan;
        constraints.ipadx = 4;
        constraints.ipady = 2;

        return constraints;
    }

    private static GridBagConstraints labelColumn(int gridx, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.weightx = 0;
        constraints.gridx = gridx;
        constraints.gridy = gridy;

        return constraints;
    }

}