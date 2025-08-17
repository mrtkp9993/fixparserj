package com.muratkoptur.fixparserj;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class FixParserApp extends JFrame {
    
    private JTextArea inputTextArea;
    private JTree resultsTree;
    private DefaultTreeModel treeModel;
    private JLabel statusLabel;
    
    public FixParserApp() {
        initializeGUI();
        createMenuBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem openMenuItem = new JMenuItem("Open...", 'O');
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        openMenuItem.addActionListener(e -> openFile());
        
        JMenuItem exitMenuItem = new JMenuItem("Exit", 'X');
        exitMenuItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // About Menu
        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('A');
        
        JMenuItem aboutMenuItem = new JMenuItem("About FIXParserJ", 'A');
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        
        aboutMenu.add(aboutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                inputTextArea.setText(content.toString());
                statusLabel.setText("File loaded: " + selectedFile.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error reading file: " + ex.getMessage(), 
                    "File Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About FIXParserJ", true);
        aboutDialog.setLayout(new BorderLayout());
        aboutDialog.setSize(400, 300);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setResizable(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("FIXParserJ");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel linkLabel = new JLabel("muratkoptur.com");
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLabel.setFont(linkLabel.getFont().deriveFont(Font.PLAIN));
        
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("https://muratkoptur.com"));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(aboutDialog, 
                        "Could not open website: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(websiteLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(linkLabel);
        
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> aboutDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        
        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        aboutDialog.setVisible(true);
    }
    
    private void initializeGUI() {
        setTitle("FIXParserJ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = createInputPanel();
        JPanel centerPanel = createResultsPanel();
        JPanel bottomPanel = createStatusPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input (Multiple messages supported - one per line)"));
        
        inputTextArea = new JTextArea(10, 50);
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(false);
        inputTextArea.setWrapStyleWord(false);
        inputTextArea.setText("8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|\n8=FIX.4.2|9=178|35=D|49=SENDER|56=TARGET|34=123|52=20240101-12:30:45|11=ORDER123|21=1|55=MSFT|54=1|60=20240101-12:30:45|38=1000|40=2|44=150.50|10=127|\n8=FIX.4.2|9=89|35=8|49=TARGET|56=SENDER|34=124|52=20240101-12:31:00|11=ORDER123|37=EXEC123|17=EXEC123|150=F|39=2|55=MSFT|54=1|14=1000|31=150.50|32=1000|10=173|");
        
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton parseButton = new JButton("Parse FIX Messages");
        JButton clearButton = new JButton("Clear");
        JButton sampleButton = new JButton("Load Sample");
        
        parseButton.setPreferredSize(new Dimension(150, 30));
        clearButton.setPreferredSize(new Dimension(100, 30));
        sampleButton.setPreferredSize(new Dimension(120, 30));
        
        parseButton.addActionListener(new ParseButtonListener());
        clearButton.addActionListener(e -> clearAll());
        sampleButton.addActionListener(e -> loadSampleMessage());
        
        buttonPanel.add(parseButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(sampleButton);
        
        panel.add(inputScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Parse Results"));
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("FIX Messages");
        treeModel = new DefaultTreeModel(rootNode);
        resultsTree = new JTree(treeModel);
        
        resultsTree.setRootVisible(false);
        resultsTree.setShowsRootHandles(true);
        resultsTree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton expandAllButton = new JButton("Expand All");
        JButton collapseAllButton = new JButton("Collapse All");
        
        expandAllButton.addActionListener(e -> expandAllNodes());
        collapseAllButton.addActionListener(e -> collapseAllNodes());
        
        buttonPanel.add(expandAllButton);
        buttonPanel.add(collapseAllButton);
        
        JScrollPane treeScrollPane = new JScrollPane(resultsTree);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(treeScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void expandAllNodes() {
        for (int i = 0; i < resultsTree.getRowCount(); i++) {
            resultsTree.expandRow(i);
        }
    }
    
    private void collapseAllNodes() {
        for (int i = resultsTree.getRowCount() - 1; i >= 0; i--) {
            resultsTree.collapseRow(i);
        }
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready to parse FIX messages");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(statusLabel);
        return panel;
    }
    
    private void clearAll() {
        inputTextArea.setText("");
        treeModel.setRoot((DefaultMutableTreeNode) treeModel.getRoot());
        ((DefaultMutableTreeNode) treeModel.getRoot()).removeAllChildren();
        treeModel.reload();
        statusLabel.setText("Ready to parse FIX messages");
    }
    
    private void loadSampleMessage() {
        String samples = "8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|\n" +
                        "8=FIX.4.2|9=178|35=D|49=SENDER|56=TARGET|34=123|52=20240101-12:30:45|11=ORDER123|21=1|55=MSFT|54=1|60=20240101-12:30:45|38=1000|40=2|44=150.50|10=127|\n" +
                        "8=FIX.4.2|9=89|35=8|49=TARGET|56=SENDER|34=124|52=20240101-12:31:00|11=ORDER123|37=EXEC123|17=EXEC123|150=F|39=2|55=MSFT|54=1|14=1000|31=150.50|32=1000|10=173|";
        inputTextArea.setText(samples);
        statusLabel.setText("Sample messages loaded");
    }
    
    private class ParseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputText = inputTextArea.getText().trim();
            
            if (inputText.isEmpty()) {
                JOptionPane.showMessageDialog(FixParserApp.this, 
                    "Please enter FIX messages to parse", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                statusLabel.setText("Parsing messages...");
                
                String[] lines = inputText.split("\n");
                
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                rootNode.removeAllChildren();
                
                int messageCount = 0;
                int errorCount = 0;
                
                for (int i = 0; i < lines.length; i++) {
                    String fixMessage = lines[i].trim();
                    
                    if (fixMessage.isEmpty()) {
                        continue;
                    }
                    
                    messageCount++;
                    Parser.ParseResult result = Parser.parse(fixMessage);
                    
                    if (result.isSuccess()) {
                        displayResults(result.getFields(), messageCount);
                    } else {
                        errorCount++;
                        DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode("Message " + messageCount + " (ERROR)");
                        DefaultMutableTreeNode errorDetailNode = new DefaultMutableTreeNode("Parse Error: " + result.getError());
                        errorNode.add(errorDetailNode);
                        rootNode.add(errorNode);
                    }
                }
                
                treeModel.reload();
                
                if (errorCount == 0) {
                    statusLabel.setText(String.format("Successfully parsed %d messages", messageCount));
                } else {
                    statusLabel.setText(String.format("Parsed %d messages (%d errors)", messageCount, errorCount));
                }
                
            } catch (Exception ex) {
                String errorMsg = "Error parsing messages: " + ex.getMessage();
                statusLabel.setText("Parse failed");
                
                JOptionPane.showMessageDialog(FixParserApp.this, 
                    errorMsg, 
                    "Parse Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void displayResults(Map<String, String> fields, int messageNumber) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode messageNode = new DefaultMutableTreeNode("Message " + messageNumber);
        
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String tag = entry.getKey();
            String value = entry.getValue();
            String fieldName = Parser.getFieldName(tag);
            String displayValue = Parser.getFieldValueWithEnum(tag, value);
            
            String fieldText = String.format("Tag %s (%s): %s", tag, fieldName, displayValue);
            DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(fieldText);
            messageNode.add(fieldNode);
        }
        
        rootNode.add(messageNode);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new FixParserApp().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
