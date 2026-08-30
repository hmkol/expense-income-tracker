import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrackerApp {

    // Variables for the main frame and UI components
    private JFrame frame;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JLabel closeLabel;
    private JLabel minimizeLabel;
    private JPanel dashboardPanel;
    private JPanel buttonsPanel;
    private JButton addTransactionButton;
    private JButton removeTransactionButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    // Variable to store the total amount
    private double totalAmount = 0.0;

    // ArrayList to store data panel values
    private ArrayList<String> dataPanelValues = new ArrayList<>();

    // Variables for form dragging
    private boolean isDragging = false;
    private Point mouseOffset;

    // Constructor
    public TrackerApp() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true); // Remove form border and default close and minimize buttons
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5,5,5,5, new Color(52,73,94)));

        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(52,73,94));
        titleBar.setPreferredSize(new Dimension(frame.getWidth(),30));
        frame.add(titleBar,BorderLayout.NORTH);

        titleLabel = new JLabel("Expense and Income Tracker");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setBounds(10,0,250,30);
        titleBar.add(titleLabel);

        closeLabel = new JLabel("x");
        closeLabel.setForeground(Color.WHITE);
        closeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setBounds(frame.getWidth() - 50, 0, 30,30);
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleBar.add(closeLabel);

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(Color.red);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(Color.white);
            }
        });

        minimizeLabel = new JLabel("–");
        minimizeLabel.setForeground(Color.WHITE);
        minimizeLabel.setFont(new Font("Arial", Font.BOLD, 17));
        minimizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minimizeLabel.setBounds(frame.getWidth() - 80, 0, 30,30);
        minimizeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleBar.add(minimizeLabel);

        minimizeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setState(JFrame.ICONIFIED);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeLabel.setForeground(Color.white);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(Color.white);
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDragging = true;
                mouseOffset = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    Point newLocation = e.getLocationOnScreen();
                    newLocation.translate(-mouseOffset.x, -mouseOffset.y);
                    frame.setLocation(newLocation);
                }
            }
        });

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        dashboardPanel.setBackground(new Color(236,240,241));
        frame.add(dashboardPanel,BorderLayout.CENTER);

        dataPanelValues.add("₹0.00");
        dataPanelValues.add("₹0.00");
        dataPanelValues.add("₹0.00");

        addDataPanel("Expense",0);
        addDataPanel("Income",1);
        addDataPanel("Total",2);

        updateDashboardSummary();

        addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setBackground(new Color(41,128,185));
        addTransactionButton.setForeground(Color.WHITE);
        addTransactionButton.setFocusPainted(false);
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setFont(new Font("Arial", Font.BOLD, 14));
        addTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTransactionButton.addActionListener(e -> {showAddTransactionDialog();});

        removeTransactionButton = new JButton("Remove Transaction");
        removeTransactionButton.setBackground(new Color(231,76,60));
        removeTransactionButton.setForeground(Color.WHITE);
        removeTransactionButton.setFocusPainted(false);
        removeTransactionButton.setBorderPainted(false);
        removeTransactionButton.setFont(new Font("Arial", Font.BOLD, 14));
        removeTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeTransactionButton.addActionListener((e) -> {
            removeSelectedTransaction();
        });

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BorderLayout(10,5));
        buttonsPanel.add(addTransactionButton, BorderLayout.NORTH);
        buttonsPanel.add(removeTransactionButton, BorderLayout.SOUTH);
        dashboardPanel.add(buttonsPanel);

        String[] columnNames = {"#", "Type", "Description", "Amount", "DB_ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        configureTransactionTable();
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        configureScrollPane(scrollPane);
        dashboardPanel.add(scrollPane);

        frame.setVisible(true);
    }

    private void updateDashboardSummary() {
        java.util.List<Transaction> transactions = TransactionDAO.getAllTransaction();
        totalAmount = TransactionValuesCalculation.getTotalValue(transactions);
        dataPanelValues.set(0, String.format("₹%,.2f", TransactionValuesCalculation.getTotalExpenses(transactions)));
        dataPanelValues.set(1, String.format("₹%,.2f", TransactionValuesCalculation.getTotalIncomes(transactions)));
        dashboardPanel.repaint();
    }

    private String fixNegativeValueDisplay(double value) {
        String newVal = String.format("₹%.2f", value);
        if (newVal.startsWith("₹-")) {
            String numericPart = newVal.substring(2);
            newVal = "-₹" + numericPart;
        }

        return newVal;
    }

    private void removeSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow != -1) {
            int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
            int transactionId = (int) tableModel.getValueAt(modelRow, 4);

            removeTransactionFromDatabase(transactionId);
            updateDashboardSummary();
            populateTableTransactions();
        }
    }

    private void removeTransactionFromDatabase(int transactionId) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `transaction_table` WHERE `id` = ?");

            ps.setInt(1, transactionId);
            ps.executeLargeUpdate();
            System.out.println("Transaction removed");
        } catch (SQLException ex) {
            Logger.getLogger(TrackerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(frame, "Add Transaction", true);
        dialog.setSize(400,250);
        dialog.setLocationRelativeTo(frame);

        JPanel dialogPanel = new JPanel(new GridLayout(4,0,10,10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        dialogPanel.setBackground(Color.LIGHT_GRAY);
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombobox = new JComboBox<>(new String[] {"Expense", "Income"});
        typeCombobox.setBackground(Color.WHITE);
        typeCombobox.setBorder(BorderFactory.createLineBorder(Color.yellow));
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        descriptionField.setBorder(BorderFactory.createLineBorder(Color.yellow));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createLineBorder(Color.yellow));

        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(41,128,185));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener((e) -> {
            boolean success = addTransaction(typeCombobox, descriptionField, amountField);
            if (success) {
                dialog.dispose();
            }
        });

        dialogPanel.add(typeLabel);
        dialogPanel.add(typeCombobox);
        dialogPanel.add(descriptionLabel);
        dialogPanel.add(descriptionField);
        dialogPanel.add(amountLabel);
        dialogPanel.add(amountField);
        dialogPanel.add(new JLabel());
        dialogPanel.add(addButton);

        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private boolean addTransaction(JComboBox<String> typeCombobox, JTextField descriptionField, JTextField amountField) {
        String type = (String) typeCombobox.getSelectedItem();
        String description = descriptionField.getText().trim();
        String amount = amountField.getText().trim();

        if (description.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both Description and Amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double newAmount;
        try {
            newAmount = Double.parseDouble(amount.replace("₹", "").replace(" ", "").replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Connection connection = DatabaseConnection.getConnection();
            String insertQuery = "INSERT INTO `transaction_table`(`transaction_type`, `description`, `amount`) VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);

            ps.setString(1, type);
            ps.setString(2, description);
            ps.setDouble(3, newAmount);
            ps.executeUpdate();
            System.out.println("Data inserted successfully");
            updateDashboardSummary();
            populateTableTransactions();
            return true;

        } catch (SQLException ex) {
            System.out.println("Error - Data not inserted: " + ex.getMessage());
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Populate table transactions
    private void populateTableTransactions() {
        tableModel.setRowCount(0);
        java.util.List<Transaction> transactions = TransactionDAO.getAllTransaction();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            Object[] rowData = {
                i + 1,
                transaction.getType(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getId()
            };
            tableModel.addRow(rowData);
        }
    }

    // Configure the appearance and behaviour of the table
    private void configureTransactionTable() {
        transactionTable.setBackground(new Color(236,240,241));
        transactionTable.setRowHeight(30);
        transactionTable.setShowGrid(false);
        transactionTable.setBorder(null);
        transactionTable.setDefaultRenderer(Object.class, new TransactionTableCellRenderer());
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        populateTableTransactions();
        // Hide the database ID column from UI display
        if (transactionTable.getColumnModel().getColumnCount() > 4) {
            transactionTable.removeColumn(transactionTable.getColumnModel().getColumn(4));
        }
        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setForeground(Color.red);
        tableHeader.setFont(new Font("Arial", Font.BOLD,18));
        tableHeader.setDefaultRenderer(new GradientHeaderRenderer());
    }

    private void configureScrollPane(JScrollPane scrollPane) {
        scrollPane.setPreferredSize(new Dimension(750,300));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    // Add a data panel to the dashboard
    private void addDataPanel(String title, int index) {
        JPanel dataPanel = new JPanel() {
            @Override
            protected  void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(title.equals("Total")) {
                    // drawDataPanel(g2d, title, String.format("₹%,.2f", totalAmount), getWidth(), getHeight());
                    drawDataPanel(g2d, title, fixNegativeValueDisplay(totalAmount), getWidth(), getHeight());
                } else {
                    drawDataPanel(g2d, title, dataPanelValues.get(index), getWidth(), getHeight());
                }
            }
        };

        dataPanel.setLayout(new GridLayout(2,1));
        dataPanel.setPreferredSize(new Dimension(170,100));
        dataPanel.setBackground(new Color(255,255,255));
        dataPanel.setBorder(new LineBorder(new Color(149,165,166),2));
        dashboardPanel.add(dataPanel);
    }

    // Draw a data panel with specified title and value
    private void drawDataPanel(Graphics g, String title, String value, int width, int height) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(new Color(255,255,255));
        g2d.fillRoundRect(0,0,width,height,20,20);
        g2d.setColor(new Color(236,240,241));
        g2d.fillRect(0,0,width,40);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(title,20,30);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN,16));
        g2d.drawString(value,20,75);
    }

    public static void main(String[] args) {
        new TrackerApp();
    }
}

class GradientHeaderRenderer extends JLabel implements TableCellRenderer {
    private final Color startColor = new Color(192,192,192);
    private final Color endColor = new Color(50,50,50);

    public GradientHeaderRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
        setFont(new Font("Arial",Font.BOLD,22));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,1,Color.YELLOW),BorderFactory.createEmptyBorder(2,5,2,5)));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gradientPaint = new GradientPaint(0,0,startColor,width,0,endColor);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0,0,width,height);
        super.paintComponent(g);
    }
}

class CustomScrollBarUI extends BasicScrollBarUI {
    private Color thumbColor = new Color(189,195,199);
    private Color trackColor = new Color(236,240,241);

    @Override
    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.setColor(thumbColor);
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    private JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0,0));
        button.setMaximumSize(new Dimension(0,0));
        button.setMinimumSize(new Dimension(0,0));
        return button;
    }
}

class TransactionTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String type = (String) table.getValueAt(row, 1);
        if (isSelected) {
            c.setForeground(Color.BLACK);
            c.setBackground(Color.ORANGE);
        } else {
            if("Income".equals(type)) {
                c.setBackground(new Color(144,238,144));
            } else {
                c.setBackground(new Color(255,99,71));
            }
        }

        return c;
    }
}