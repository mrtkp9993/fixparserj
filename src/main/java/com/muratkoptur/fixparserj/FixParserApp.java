package com.muratkoptur.fixparserj;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class FixParserApp extends JFrame {
    
    private JTextArea inputTextArea;
    private JTable fieldsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public FixParserApp() {
        initializeGUI();
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
        
        tableModel = new DefaultTableModel(new String[]{"Message #", "Tag", "Field Name", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fieldsTable = new JTable(tableModel);
        fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        fieldsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        fieldsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        fieldsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        
        JScrollPane tableScrollPane = new JScrollPane(fieldsTable);
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
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
        tableModel.setRowCount(0);
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
                tableModel.setRowCount(0);
                
                int messageCount = 0;
                int totalFields = 0;
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
                        totalFields += result.getFields().size();
                    } else {
                        errorCount++;
                        // Add error row to table
                        tableModel.addRow(new Object[]{messageCount, "ERROR", "Parse Error", result.getError()});
                    }
                }
                
                if (errorCount == 0) {
                    statusLabel.setText(String.format("Successfully parsed %d messages - %d total fields found", messageCount, totalFields));
                } else {
                    statusLabel.setText(String.format("Parsed %d messages (%d errors) - %d total fields found", messageCount, errorCount, totalFields));
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
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String tag = entry.getKey();
            String value = entry.getValue();
            String fieldName = Parser.getFieldName(tag);
            String displayValue = Parser.getFieldValueWithEnum(tag, value);
            
            tableModel.addRow(new Object[]{messageNumber, tag, fieldName, displayValue});
        }
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
