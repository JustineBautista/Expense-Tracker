import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Enhanced ExpenseTracker - Premium design with advanced features
 */
public class ExpenseTracker extends JFrame {

    // UI Components
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private JTextField amountField, descriptionField, searchField;
    private JComboBox<String> categoryCombo, filterCombo;
    private JLabel totalLabel, budgetStatusLabel, monthLabel, weekLabel, todayLabel;
    private JProgressBar budgetBar;
    private JPanel statsPanel;
    private JButton prevMonthBtn, nextMonthBtn;
    private LocalDate currentMonth;

    // Data
    private List<Expense> expenses;
    private List<Expense> filteredExpenses;
    private double monthlyBudget = 0;

    // Constants
    private static final String DATA_FILE = "data/expenses.csv";
    private static final String CONFIG_FILE = "data/config.txt";
    private static final String[] CATEGORIES = {
        "Food", "Transport", "Entertainment", "Bills",
        "Shopping", "Healthcare", "Education", "Work",
        "Travel", "Housing", "Technology", "Other"
    };
    
    // Modern Color Palette - Enhanced
    private static final Color BG_PRIMARY = new Color(248, 249, 250);
    private static final Color BG_SECONDARY = Color.WHITE;
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color ACCENT_GREEN = new Color(5, 150, 105);
    private static final Color ACCENT_RED = new Color(220, 38, 38);
    private static final Color ACCENT_ORANGE = new Color(234, 88, 12);
    private static final Color ACCENT_PURPLE = new Color(147, 51, 234);
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HOVER_BG = new Color(241, 245, 249);

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        filteredExpenses = new ArrayList<>();
        currentMonth = LocalDate.now();
        setTitle("Expense Tracker Pro");
        setSize(1400, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_PRIMARY);

        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBackground(BG_PRIMARY);
        mainContainer.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top bar with month navigation
        JPanel topBar = createTopBar();
        mainContainer.add(topBar, BorderLayout.NORTH);

        // Left side - Stats and Input
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BG_PRIMARY);
        leftPanel.setPreferredSize(new Dimension(380, 0));
        
        leftPanel.add(createQuickStatsCard());
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createStatsCard());
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createInputCard());
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(createQuickActionsCard());

        // Right side - Table
        JPanel rightPanel = createTableCard();

        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_PRIMARY);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(BG_PRIMARY);
        
        JLabel titleLabel = new JLabel("Expense Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(titleLabel);

        // Month navigation
        JPanel monthNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        monthNav.setBackground(BG_PRIMARY);

        prevMonthBtn = createNavButton("◀");
        prevMonthBtn.addActionListener(e -> changeMonth(-1));
        
        monthLabel = new JLabel(getCurrentMonthLabel());
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthLabel.setForeground(TEXT_PRIMARY);
        monthLabel.setBorder(new EmptyBorder(0, 15, 0, 15));

        nextMonthBtn = createNavButton("▶");
        nextMonthBtn.addActionListener(e -> changeMonth(1));

        JButton todayBtn = createSmallButton("Today");
        todayBtn.addActionListener(e -> {
            currentMonth = LocalDate.now();
            monthLabel.setText(getCurrentMonthLabel());
            filterByMonth();
        });

        monthNav.add(prevMonthBtn);
        monthNav.add(monthLabel);
        monthNav.add(nextMonthBtn);
        monthNav.add(Box.createHorizontalStrut(10));
        monthNav.add(todayBtn);

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(monthNav, BorderLayout.EAST);

        return topBar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_SECONDARY);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_BG); }
            public void mouseExited(MouseEvent e) { btn.setBackground(BG_SECONDARY); }
        });
        
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_BLUE);
        btn.setPreferredSize(new Dimension(70, 40));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_BLUE.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(ACCENT_BLUE); }
        });
        
        return btn;
    }

    private String getCurrentMonthLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return currentMonth.format(formatter);
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        monthLabel.setText(getCurrentMonthLabel());
        filterByMonth();
    }

    private JPanel createQuickStatsCard() {
        JPanel card = createCard();
        card.setLayout(new GridLayout(1, 3, 12, 0));
        card.setPreferredSize(new Dimension(380, 100));
        card.setMaximumSize(new Dimension(380, 100));

        // Today
        JPanel todayPanel = createMiniStatPanel("Today", "$0.00", ACCENT_BLUE);
        todayLabel = (JLabel) todayPanel.getComponent(2);
        card.add(todayPanel);

        // This Week
        JPanel weekPanel = createMiniStatPanel("This Week", "$0.00", ACCENT_PURPLE);
        weekLabel = (JLabel) weekPanel.getComponent(2);
        card.add(weekPanel);

        // This Month
        JPanel monthPanel = createMiniStatPanel("Month", "$0.00", ACCENT_GREEN);
        totalLabel = (JLabel) monthPanel.getComponent(2);
        card.add(monthPanel);

        return card;
    }

    private JPanel createMiniStatPanel(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECONDARY);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(valueLabel);

        return panel;
    }

    private JPanel createStatsCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 210));
        card.setMaximumSize(new Dimension(380, 210));

        // Header
        JLabel header = new JLabel("Budget Overview");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(15));

        // Budget Section
        JPanel budgetPanel = new JPanel();
        budgetPanel.setLayout(new BoxLayout(budgetPanel, BoxLayout.Y_AXIS));
        budgetPanel.setBackground(BG_SECONDARY);
        budgetPanel.setMaximumSize(new Dimension(400, 120));
        budgetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel budgetHeader = new JPanel(new BorderLayout());
        budgetHeader.setBackground(BG_SECONDARY);
        budgetHeader.setMaximumSize(new Dimension(400, 25));
        
        JLabel budgetTitle = new JLabel("Monthly Budget");
        budgetTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        budgetTitle.setForeground(TEXT_SECONDARY);
        budgetHeader.add(budgetTitle, BorderLayout.WEST);
        
        JButton setBudgetBtn = new JButton("Set Budget");
        setBudgetBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        setBudgetBtn.setForeground(Color.WHITE);
        setBudgetBtn.setBackground(ACCENT_BLUE);
        setBudgetBtn.setBorderPainted(false);
        setBudgetBtn.setFocusPainted(false);
        setBudgetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBudgetBtn.addActionListener(e -> setBudget());
        budgetHeader.add(setBudgetBtn, BorderLayout.EAST);
        
        budgetPanel.add(budgetHeader);
        budgetPanel.add(Box.createVerticalStrut(10));

        budgetBar = new JProgressBar(0, 100);
        budgetBar.setStringPainted(true);
        budgetBar.setPreferredSize(new Dimension(350, 32));
        budgetBar.setMaximumSize(new Dimension(400, 32));
        budgetBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetBar.setForeground(ACCENT_GREEN);
        budgetBar.setBackground(new Color(243, 244, 246));
        budgetBar.setBorderPainted(false);
        budgetPanel.add(budgetBar);
        budgetPanel.add(Box.createVerticalStrut(10));

        budgetStatusLabel = new JLabel("No budget set");
        budgetStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        budgetStatusLabel.setForeground(TEXT_SECONDARY);
        budgetStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetPanel.add(budgetStatusLabel);

        card.add(budgetPanel);
        
        statsPanel = card;
        return card;
    }

    private JPanel createInputCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(380, 380));

        // Header
        JLabel header = new JLabel("Add New Expense");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(18));

        // Amount Input
        card.add(createLabel("Amount ($)"));
        card.add(Box.createVerticalStrut(6));
        amountField = createTextField("0.00");
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(amountField);
        card.add(Box.createVerticalStrut(14));

        // Category Input
        card.add(createLabel("Category"));
        card.add(Box.createVerticalStrut(6));
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setMaximumSize(new Dimension(400, 42));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(categoryCombo);
        card.add(Box.createVerticalStrut(14));

        // Description Input
        card.add(createLabel("Description"));
        card.add(Box.createVerticalStrut(6));
        descriptionField = createTextField("What did you buy?");
        card.add(descriptionField);
        card.add(Box.createVerticalStrut(20));

        // Add Button
        JButton addBtn = createPrimaryButton("💾 Add Expense");
        addBtn.addActionListener(e -> addExpense());
        card.add(addBtn);

        // Enter key support
        amountField.addActionListener(e -> addExpense());
        descriptionField.addActionListener(e -> addExpense());

        return card;
    }

    private JPanel createQuickActionsCard() {
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(380, 170));

        JLabel header = new JLabel("Quick Actions");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(12));

        JButton analyticsBtn = createSecondaryButton("📊 View Analytics");
        analyticsBtn.addActionListener(e -> showAnalytics());
        card.add(analyticsBtn);
        card.add(Box.createVerticalStrut(8));

        JButton exportBtn = createSecondaryButton("📤 Export Data");
        exportBtn.addActionListener(e -> exportToCSV());
        card.add(exportBtn);
        card.add(Box.createVerticalStrut(8));

        JButton categoryBtn = createSecondaryButton("📈 Category Report");
        categoryBtn.addActionListener(e -> showCategoryReport());
        card.add(categoryBtn);

        return card;
    }

    private JPanel createTableCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 15));

        // Header with search and filter
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(BG_SECONDARY);

        JLabel header = new JLabel("Expense History");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(TEXT_PRIMARY);
        headerPanel.add(header, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BG_SECONDARY);
        
        // Filter dropdown
        String[] filters = {"All Categories", "🍔 Food", "🚗 Transport", "🎬 Entertainment", 
                           "💡 Bills", "🛍️ Shopping", "🏥 Healthcare", "📚 Education", 
                           "💼 Work", "✈️ Travel", "🏠 Housing", "📱 Technology", "📦 Other"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterCombo.addActionListener(e -> filterExpenses());
        
        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterExpenses();
            }
        });
        
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 8));
        
        searchPanel.add(filterCombo);
        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Category", "Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expenseTable.setRowHeight(52);
        expenseTable.setShowVerticalLines(false);
        expenseTable.setShowHorizontalLines(true);
        expenseTable.setGridColor(BORDER_COLOR);
        expenseTable.setSelectionBackground(new Color(219, 234, 254));
        expenseTable.setSelectionForeground(TEXT_PRIMARY);
        expenseTable.setIntercellSpacing(new Dimension(10, 0));
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        expenseTable.getTableHeader().setBackground(new Color(249, 250, 251));
        expenseTable.getTableHeader().setForeground(TEXT_SECONDARY);
        expenseTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        rightRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(350);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // Double-click to edit
        expenseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editExpense();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(BG_SECONDARY);

        JButton editBtn = createActionButton("✏️ Edit", ACCENT_BLUE);
        editBtn.addActionListener(e -> editExpense());
        actionPanel.add(editBtn);

        JButton deleteBtn = createActionButton("🗑️ Delete", ACCENT_RED);
        deleteBtn.addActionListener(e -> deleteExpense());
        actionPanel.add(deleteBtn);

        JButton clearBtn = createActionButton("Clear All", TEXT_SECONDARY);
        clearBtn.addActionListener(e -> clearAllExpenses());
        actionPanel.add(clearBtn);

        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    // Helper methods for UI components
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(22, 22, 22, 22)
        ));
        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(400, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(11, 13, 11, 13)
        ));
        field.setForeground(TEXT_SECONDARY);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                    new EmptyBorder(10, 12, 10, 12)
                ));
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(11, 13, 11, 13)
                ));
            }
        });
        
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_BLUE);
        button.setMaximumSize(new Dimension(400, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_BLUE.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_BLUE);
            }
        });
        
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(new Color(249, 250, 251));
        button.setMaximumSize(new Dimension(400, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(9, 13, 9, 13)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_BG);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(249, 250, 251));
            }
        });
        
        return button;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            new EmptyBorder(9, 17, 9, 17)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(color);
            }
        });
        
        return button;
    }

    // Core functionality methods
    private void addExpense() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.equals("0.00") || amountText.isEmpty()) {
                showError("Please enter an amount");
                return;
            }

            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showError("Amount must be positive");
                return;
            }

            String category = (String) categoryCombo.getSelectedItem();
            String description = descriptionField.getText().trim();
            if (description.isEmpty() || description.equals("What did you buy?")) {
                description = "No description";
            }

            Expense expense = new Expense(amount, category, description);
            expenses.add(0, expense);
            
            if (isInCurrentMonth(expense)) {
                filteredExpenses.add(0, expense);
                addExpenseToTable(expense, 0);
            }
            
            updateSummary();
            clearInputFields();
            saveExpenses();

            // Success animation
            amountField.setBackground(new Color(240, 253, 244));
            javax.swing.Timer timer = new javax.swing.Timer(500, evt -> amountField.setBackground(Color.WHITE));
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount");
        }
    }

    private boolean isInCurrentMonth(Expense expense) {
        LocalDate expenseDate = expense.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return expenseDate.getYear() == currentMonth.getYear() && 
               expenseDate.getMonth() == currentMonth.getMonth();
    }

    private void filterByMonth() {
        filteredExpenses.clear();
        tableModel.setRowCount(0);
        
        for (Expense expense : expenses) {
            if (isInCurrentMonth(expense)) {
                filteredExpenses.add(expense);
            }
        }
        
        for (int i = 0; i < filteredExpenses.size(); i++) {
            addExpenseToTable(filteredExpenses.get(i), i);
        }
        
        updateSummary();
    }

    private void addExpenseToTable(Expense expense, int position) {
        Object[] row = {
            expense.getFormattedDate(),
            expense.getCategory(),
            expense.getDescription(),
            expense.getFormattedAmount()
        };
        tableModel.insertRow(position, row);
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                Expense toRemove = filteredExpenses.get(selectedRow);
                expenses.remove(toRemove);
                filteredExpenses.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                updateSummary();
                saveExpenses();
            }
        } else {
            showError("Please select an expense to delete");
        }
    }

    private void editExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow >= 0) {
            Expense expense = filteredExpenses.get(selectedRow);
            
            JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 15));
            editPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JTextField editAmount = new JTextField(String.valueOf(expense.getAmount()));
            editAmount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JComboBox<String> editCategory = new JComboBox<>(CATEGORIES);
            editCategory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            editCategory.setSelectedItem(expense.getCategory());
            
            JTextField editDescription = new JTextField(expense.getDescription());
            editDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JLabel amtLabel = new JLabel("Amount:");
            amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            editPanel.add(amtLabel);
            editPanel.add(editAmount);
            
            JLabel catLabel = new JLabel("Category:");
            catLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            editPanel.add(catLabel);
            editPanel.add(editCategory);
            
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            editPanel.add(descLabel);
            editPanel.add(editDescription);
            
            int result = JOptionPane.showConfirmDialog(this, editPanel, 
                "Edit Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double newAmount = Double.parseDouble(editAmount.getText().trim());
                    if (newAmount <= 0) {
                        showError("Amount must be positive");
                        return;
                    }
                    
                    expense.setAmount(newAmount);
                    expense.setCategory((String) editCategory.getSelectedItem());
                    expense.setDescription(editDescription.getText().trim());
                    
                    tableModel.setValueAt(expense.getCategory(), selectedRow, 1);
                    tableModel.setValueAt(expense.getDescription(), selectedRow, 2);
                    tableModel.setValueAt(expense.getFormattedAmount(), selectedRow, 3);
                    
                    updateSummary();
                    saveExpenses();
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid amount");
                }
            }
        } else {
            showError("Please select an expense to edit");
        }
    }

    private void clearAllExpenses() {
        if (expenses.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete all " + expenses.size() + " expenses?\nThis cannot be undone.",
            "Confirm Clear All",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            expenses.clear();
            filteredExpenses.clear();
            tableModel.setRowCount(0);
            updateSummary();
            saveExpenses();
        }
    }

    private void setBudget() {
        JPanel budgetPanel = new JPanel(new BorderLayout(10, 10));
        budgetPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel label = new JLabel("Enter monthly budget:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JTextField budgetField = new JTextField(monthlyBudget > 0 ? String.valueOf(monthlyBudget) : "");
        budgetField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        budgetPanel.add(label, BorderLayout.NORTH);
        budgetPanel.add(budgetField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, budgetPanel, 
            "Set Budget", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION && !budgetField.getText().trim().isEmpty()) {
            try {
                monthlyBudget = Double.parseDouble(budgetField.getText().trim());
                if (monthlyBudget < 0) {
                    showError("Budget cannot be negative");
                    monthlyBudget = 0;
                } else {
                    updateSummary();
                    saveConfig();
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number");
            }
        }
    }

    private void filterExpenses() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedFilter = (String) filterCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        for (Expense expense : filteredExpenses) {
            boolean matchesSearch = searchText.isEmpty() || 
                expense.getDescription().toLowerCase().contains(searchText) ||
                expense.getCategory().toLowerCase().contains(searchText);
            
            boolean matchesCategory = selectedFilter.equals("All Categories") ||
                expense.getCategory().equals(selectedFilter);
                
            if (matchesSearch && matchesCategory) {
                addExpenseToTable(expense, tableModel.getRowCount());
            }
        }
    }

    private void exportToCSV() {
        if (expenses.isEmpty()) {
            showError("No expenses to export");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Expenses");
        fileChooser.setSelectedFile(new File("expenses_" + 
            new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("Date,Category,Description,Amount");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Expense expense : expenses) {
                    writer.printf("%s,%s,\"%s\",%.2f%n",
                        sdf.format(expense.getDate()),
                        expense.getCategory(),
                        expense.getDescription().replace("\"", "\"\""),
                        expense.getAmount());
                }
                JOptionPane.showMessageDialog(this, 
                    "✅ Expenses exported successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                showError("Error exporting: " + e.getMessage());
            }
        }
    }

    private void showCategoryReport() {
        if (filteredExpenses.isEmpty()) {
            showError("No expenses in current month");
            return;
        }
        
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        
        for (Expense expense : filteredExpenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            categoryCount.merge(expense.getCategory(), 1, Integer::sum);
        }
        
        StringBuilder report = new StringBuilder();
        report.append("<html><body style='font-family: Segoe UI; padding: 20px; width: 600px;'>");
        report.append("<h2 style='color: #111827; margin-bottom: 10px;'>📊 Category Report</h2>");
        report.append("<p style='color: #6B7280; margin-bottom: 25px;'>").append(getCurrentMonthLabel()).append("</p>");
        
        double total = filteredExpenses.stream().mapToDouble(Expense::getAmount).sum();
        
        report.append("<table style='width: 100%; border-collapse: collapse;'>");
        report.append("<tr style='background: #F9FAFB; border-bottom: 2px solid #E5E7EB;'>");
        report.append("<th style='padding: 12px; text-align: left; font-size: 12px; color: #6B7280;'>CATEGORY</th>");
        report.append("<th style='padding: 12px; text-align: center; font-size: 12px; color: #6B7280;'>COUNT</th>");
        report.append("<th style='padding: 12px; text-align: right; font-size: 12px; color: #6B7280;'>AMOUNT</th>");
        report.append("<th style='padding: 12px; text-align: right; font-size: 12px; color: #6B7280;'>% OF TOTAL</th>");
        report.append("</tr>");
        
        categoryTotals.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                double percentage = (entry.getValue() / total) * 100;
                int count = categoryCount.get(entry.getKey());
                report.append(String.format(
                    "<tr style='border-bottom: 1px solid #F3F4F6;'>" +
                    "<td style='padding: 14px; font-size: 14px; color: #111827;'>%s</td>" +
                    "<td style='padding: 14px; text-align: center; font-size: 14px; color: #6B7280;'>%d</td>" +
                    "<td style='padding: 14px; text-align: right; font-size: 14px; font-weight: bold; color: #111827;'>$%.2f</td>" +
                    "<td style='padding: 14px; text-align: right; font-size: 14px; color: #2563EB;'>%.1f%%</td>" +
                    "</tr>",
                    entry.getKey(), count, entry.getValue(), percentage));
            });
        
        report.append("<tr style='border-top: 2px solid #E5E7EB; background: #F9FAFB;'>");
        report.append("<td style='padding: 14px; font-weight: bold; color: #111827;'>TOTAL</td>");
        report.append(String.format("<td style='padding: 14px; text-align: center; font-weight: bold; color: #111827;'>%d</td>", filteredExpenses.size()));
        report.append(String.format("<td style='padding: 14px; text-align: right; font-weight: bold; color: #111827; font-size: 16px;'>$%.2f</td>", total));
        report.append("<td style='padding: 14px; text-align: right; font-weight: bold; color: #111827;'>100%</td>");
        report.append("</tr>");
        
        report.append("</table>");
        report.append("</body></html>");
        
        JLabel reportLabel = new JLabel(report.toString());
        JScrollPane scrollPane = new JScrollPane(reportLabel);
        scrollPane.setPreferredSize(new Dimension(700, 550));
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Category Report", JOptionPane.PLAIN_MESSAGE);
    }

    private void showAnalytics() {
        if (expenses.isEmpty()) {
            showError("No expenses to analyze");
            return;
        }
        
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        
        StringBuilder analytics = new StringBuilder();
        analytics.append("<html><body style='font-family: Segoe UI; padding: 20px; width: 600px;'>");
        analytics.append("<h2 style='color: #111827; margin-bottom: 20px;'>📈 Overall Analytics</h2>");
        
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double avgExpense = total / expenses.size();
        
        // Find highest expense
        Expense maxExpense = expenses.stream()
            .max(Comparator.comparingDouble(Expense::getAmount))
            .orElse(null);
        
        analytics.append("<div style='background: #F9FAFB; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>");
        analytics.append(String.format("<p style='font-size: 14px; color: #6B7280; margin: 5px 0;'>Total Expenses: <strong style='color: #111827; font-size: 18px;'>$%.2f</strong></p>", total));
        analytics.append(String.format("<p style='font-size: 14px; color: #6B7280; margin: 5px 0;'>Number of Transactions: <strong style='color: #111827;'>%d</strong></p>", expenses.size()));
        analytics.append(String.format("<p style='font-size: 14px; color: #6B7280; margin: 5px 0;'>Average Expense: <strong style='color: #111827;'>$%.2f</strong></p>", avgExpense));
        if (maxExpense != null) {
            analytics.append(String.format("<p style='font-size: 14px; color: #6B7280; margin: 5px 0;'>Largest Expense: <strong style='color: #DC2626;'>$%.2f</strong> (%s)</p>", 
                maxExpense.getAmount(), maxExpense.getCategory()));
        }
        analytics.append("</div>");
        
        analytics.append("<h3 style='color: #111827; margin-top: 25px; margin-bottom: 15px;'>Spending by Category</h3>");
        analytics.append("<table style='width: 100%; border-collapse: collapse;'>");
        
        categoryTotals.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                double percentage = (entry.getValue() / total) * 100;
                String barWidth = String.format("%.0f%%", percentage);
                analytics.append(String.format(
                    "<tr style='border-bottom: 1px solid #E5E7EB;'>" +
                    "<td style='padding: 12px 0; font-size: 14px; color: #111827; width: 150px;'>%s</td>" +
                    "<td style='text-align: right; padding: 12px 0;'>" +
                    "<div style='background: #F3F4F6; border-radius: 6px; height: 28px; position: relative; width: 220px; display: inline-block;'>" +
                    "<div style='background: linear-gradient(90deg, #3B82F6, #2563EB); border-radius: 6px; height: 28px; width: %s;'></div>" +
                    "<span style='position: absolute; right: 10px; top: 5px; font-size: 11px; font-weight: bold; color: #111827;'>%.1f%%</span>" +
                    "</div></td>" +
                    "<td style='text-align: right; padding: 12px 0 12px 15px; font-size: 15px; font-weight: bold; color: #111827;'>$%.2f</td>" +
                    "</tr>",
                    entry.getKey(), barWidth, percentage, entry.getValue()));
            });
        
        analytics.append("</table>");
        analytics.append("</body></html>");
        
        JLabel analyticsLabel = new JLabel(analytics.toString());
        JScrollPane scrollPane = new JScrollPane(analyticsLabel);
        scrollPane.setPreferredSize(new Dimension(650, 550));
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Analytics", JOptionPane.PLAIN_MESSAGE);
    }

    private void updateSummary() {
        // Month total
        double monthTotal = filteredExpenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText(String.format("$%.2f", monthTotal));
        
        // Today total
        LocalDate today = LocalDate.now();
        double todayTotal = filteredExpenses.stream()
            .filter(e -> {
                LocalDate expDate = e.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return expDate.equals(today);
            })
            .mapToDouble(Expense::getAmount)
            .sum();
        todayLabel.setText(String.format("$%.2f", todayTotal));
        
        // Week total
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        double weekTotal = filteredExpenses.stream()
            .filter(e -> {
                LocalDate expDate = e.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return !expDate.isBefore(weekStart) && !expDate.isAfter(today);
            })
            .mapToDouble(Expense::getAmount)
            .sum();
        weekLabel.setText(String.format("$%.2f", weekTotal));

        // Budget tracking
        if (monthlyBudget > 0) {
            double remaining = monthlyBudget - monthTotal;
            double percentUsed = (monthTotal / monthlyBudget) * 100;
            
            budgetBar.setValue((int) Math.min(percentUsed, 100));
            budgetBar.setString(String.format("%.0f%% used", percentUsed));
            
            if (remaining >= 0) {
                budgetStatusLabel.setText(String.format("$%.2f of $%.2f remaining", remaining, monthlyBudget));
                
                if (percentUsed >= 90) {
                    budgetBar.setForeground(ACCENT_RED);
                    budgetStatusLabel.setForeground(ACCENT_RED);
                } else if (percentUsed >= 75) {
                    budgetBar.setForeground(ACCENT_ORANGE);
                    budgetStatusLabel.setForeground(ACCENT_ORANGE);
                } else {
                    budgetBar.setForeground(ACCENT_GREEN);
                    budgetStatusLabel.setForeground(ACCENT_GREEN);
                }
            } else {
                budgetStatusLabel.setText(String.format("⚠️ Over budget by $%.2f", Math.abs(remaining)));
                budgetStatusLabel.setForeground(ACCENT_RED);
                budgetBar.setForeground(ACCENT_RED);
            }
        } else {
            budgetStatusLabel.setText("No budget set");
            budgetStatusLabel.setForeground(TEXT_SECONDARY);
            budgetBar.setValue(0);
            budgetBar.setString("Set a budget to track spending");
            budgetBar.setForeground(new Color(209, 213, 219));
        }
    }

    private void clearInputFields() {
        amountField.setText("0.00");
        amountField.setForeground(TEXT_SECONDARY);
        descriptionField.setText("What did you buy?");
        descriptionField.setForeground(TEXT_SECONDARY);
        categoryCombo.setSelectedIndex(0);
        amountField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void loadExpenses() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Expense expense = Expense.fromString(line);
                if (expense != null) {
                    expenses.add(expense);
                }
            }
            
            filterByMonth();
            
        } catch (IOException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }
    }

    private void saveExpenses() {
        try {
            File dataFile = new File(DATA_FILE);
            dataFile.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
                for (Expense expense : expenses) {
                    writer.println(expense.toString());
                }
            }
        } catch (IOException e) {
            showError("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                monthlyBudget = Double.parseDouble(line);
                updateSummary();
            }
        } catch (IOException | NumberFormatException e) {
            // Config doesn't exist or invalid, use default
        }
    }

    private void saveConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            configFile.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
                writer.println(monthlyBudget);
            }
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseTracker tracker = new ExpenseTracker();
            tracker.loadConfig();
            tracker.loadExpenses();
            tracker.setVisible(true);
        });
    }
}


