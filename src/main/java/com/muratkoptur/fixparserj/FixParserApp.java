package com.muratkoptur.fixparserj;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class FixParserApp extends JFrame {

    private JTextArea inputTextArea;
    private JTree resultsTree;
    private DefaultTreeModel treeModel;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private List<Map<String, String>> parsedMessages;
    private JLabel statusLabel;

    public FixParserApp() {
        initializeGUI();
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

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
        inputTextArea.setText(
                "8=FIX.4.4|9=148|35=D|34=1080|49=TESTBUY1|52=20180920-18:14:19.508|56=TESTSELL1|11=636730640278898634|15=USD|21=2|38=7000|40=1|54=1|55=MSFT|60=20180920-18:14:19.492|10=092|\n"
                        + "8=FIX.4.4|9=289|35=8|34=1090|49=TESTSELL1|52=20180920-18:23:53.671|56=TESTBUY1|6=113.35|11=636730640278898634|14=3500.0000000000|15=USD|17=20636730646335310000|21=2|31=113.35|32=3500|37=20636730646335310000|38=7000|39=1|40=1|54=1|55=MSFT|60=20180920-18:23:53.531|150=F|151=3500|453=1|448=BRK2|447=D|452=1|10=151|\n"
                        + "8=FIX.4.4|9=75|35=A|34=1092|49=TESTBUY1|52=20180920-18:24:59.643|56=TESTSELL1|98=0|108=60|10=178|\n"
                        + "8=FIX.4.4|9=63|35=5|34=1091|49=TESTBUY1|52=20180920-18:24:58.675|56=TESTSELL1|10=138|\n"
                        + "8=FIX.4.2|9=163|35=D|34=972|49=TESTBUY3|52=20190206-16:25:10.403|56=TESTSELL3|11=141636850670842269979|21=2|38=100|40=1|54=1|55=AAPL|60=20190206-16:25:08.968|207=TO|6000=TEST1234|10=106|\n"
                        + "8=FIX.4.2|9=271|35=8|34=974|49=TESTSELL3|52=20190206-16:26:09.059|56=TESTBUY3|6=174.51|11=141636850670842269979|14=100.0000000000|17=3636850671684357979|20=0|21=2|31=174.51|32=100.0000000000|37=1005448|38=100|39=2|40=1|54=1|55=AAPL|60=20190206-16:26:08.435|150=2|151=0.0000000000|10=194|\n"
                        + "8=FIX.4.2|9=74|35=A|34=978|49=TESTSELL3|52=20190206-16:29:19.208|56=TESTBUY3|98=0|108=60|10=137|\n"
                        + "8=FIX.4.2|9=62|35=5|34=977|49=TESTSELL3|52=20190206-16:28:51.518|56=TESTBUY3|10=092|");

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

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tree View tab
        JPanel treePanel = new JPanel(new BorderLayout());

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("FIX Messages");
        treeModel = new DefaultTreeModel(rootNode);
        resultsTree = new JTree(treeModel);

        resultsTree.setRootVisible(false);
        resultsTree.setShowsRootHandles(true);
        resultsTree.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        JPanel treeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton expandAllButton = new JButton("Expand All");
        JButton collapseAllButton = new JButton("Collapse All");

        expandAllButton.addActionListener(e -> expandAllNodes());
        collapseAllButton.addActionListener(e -> collapseAllNodes());

        treeButtonPanel.add(expandAllButton);
        treeButtonPanel.add(collapseAllButton);

        JScrollPane treeScrollPane = new JScrollPane(resultsTree);

        treePanel.add(treeButtonPanel, BorderLayout.NORTH);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        // Table View tab
        JPanel tablePanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        resultsTable.setRowHeight(22);
        resultsTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(resultsTable);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Tree View", treePanel);
        tabbedPane.addTab("Table View", tablePanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

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
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        if (parsedMessages != null)
            parsedMessages.clear();
        statusLabel.setText("Ready to parse FIX messages");
    }

    private void loadSampleMessage() {
        String samples = "8=FIX.4.4|9=148|35=D|34=1080|49=TESTBUY1|52=20180920-18:14:19.508|56=TESTSELL1|11=636730640278898634|15=USD|21=2|38=7000|40=1|54=1|55=MSFT|60=20180920-18:14:19.492|10=092|\n"
                + "8=FIX.4.4|9=289|35=8|34=1090|49=TESTSELL1|52=20180920-18:23:53.671|56=TESTBUY1|6=113.35|11=636730640278898634|14=3500.0000000000|15=USD|17=20636730646335310000|21=2|31=113.35|32=3500|37=20636730646335310000|38=7000|39=1|40=1|54=1|55=MSFT|60=20180920-18:23:53.531|150=F|151=3500|453=1|448=BRK2|447=D|452=1|10=151|\n"
                + "8=FIX.4.4|9=75|35=A|34=1092|49=TESTBUY1|52=20180920-18:24:59.643|56=TESTSELL1|98=0|108=60|10=178|\n"
                + "8=FIX.4.4|9=63|35=5|34=1091|49=TESTBUY1|52=20180920-18:24:58.675|56=TESTSELL1|10=138|\n"
                + "8=FIX.4.2|9=163|35=D|34=972|49=TESTBUY3|52=20190206-16:25:10.403|56=TESTSELL3|11=141636850670842269979|21=2|38=100|40=1|54=1|55=AAPL|60=20190206-16:25:08.968|207=TO|6000=TEST1234|10=106|\n"
                + "8=FIX.4.2|9=271|35=8|34=974|49=TESTSELL3|52=20190206-16:26:09.059|56=TESTBUY3|6=174.51|11=141636850670842269979|14=100.0000000000|17=3636850671684357979|20=0|21=2|31=174.51|32=100.0000000000|37=1005448|38=100|39=2|40=1|54=1|55=AAPL|60=20190206-16:26:08.435|150=2|151=0.0000000000|10=194|\n"
                + "8=FIX.4.2|9=74|35=A|34=978|49=TESTSELL3|52=20190206-16:29:19.208|56=TESTBUY3|98=0|108=60|10=137|\n"
                + "8=FIX.4.2|9=62|35=5|34=977|49=TESTSELL3|52=20190206-16:28:51.518|56=TESTBUY3|10=092|";
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
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0);
                parsedMessages = new ArrayList<>();

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
                        parsedMessages.add(result.getFields());
                    } else {
                        errorCount++;
                        DefaultMutableTreeNode errorNode = new DefaultMutableTreeNode(
                                "Message " + messageCount + " (ERROR)");
                        DefaultMutableTreeNode errorDetailNode = new DefaultMutableTreeNode(
                                "Parse Error: " + result.getError());
                        errorNode.add(errorDetailNode);
                        rootNode.add(errorNode);
                    }
                }

                buildTableView();
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

    private void buildTableView() {
        if (parsedMessages == null || parsedMessages.isEmpty())
            return;

        LinkedHashSet<String> allTags = new LinkedHashSet<>();
        for (Map<String, String> fields : parsedMessages) {
            allTags.addAll(fields.keySet());
        }

        List<String> tagList = new ArrayList<>(allTags);
        String[] columnNames = new String[tagList.size() + 1];
        columnNames[0] = "Message #";
        for (int i = 0; i < tagList.size(); i++) {
            String tag = tagList.get(i);
            String fieldName = Parser.getFieldName(tag);
            columnNames[i + 1] = fieldName + " (" + tag + ")";
        }

        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        for (String col : columnNames) {
            tableModel.addColumn(col);
        }

        // Add one row per message
        for (int m = 0; m < parsedMessages.size(); m++) {
            Map<String, String> fields = parsedMessages.get(m);
            Object[] row = new Object[tagList.size() + 1];
            row[0] = m + 1;
            for (int i = 0; i < tagList.size(); i++) {
                String tag = tagList.get(i);
                String value = fields.get(tag);
                if (value != null) {
                    row[i + 1] = Parser.getFieldValueWithEnum(tag, value);
                } else {
                    row[i + 1] = "";
                }
            }
            tableModel.addRow(row);
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        resultsTable.setRowSorter(sorter);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        for (int i = 1; i < resultsTable.getColumnCount(); i++) {
            resultsTable.getColumnModel().getColumn(i).setPreferredWidth(140);
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
