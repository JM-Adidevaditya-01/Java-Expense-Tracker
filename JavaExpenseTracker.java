import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class JavaExpenseTracker extends JFrame {

    // =========================
    // Expense class
    // =========================
    static class Expense {
        String date;
        double amount;
        String category;
        String description;

        Expense(String date, double amount, String category, String description) {
            this.date = date;
            this.amount = amount;
            this.category = category;
            this.description = description;
        }

        @Override
        public String toString() {
            return date + " | Rs." + amount + " | " + category + " | " + description;
        }
    }

    // =========================
    // Variables
    // =========================
    private ArrayList<Expense> expenses = new ArrayList<>();

    private JTextField amountField;
    private JComboBox<String> categoryBox;
    private JTextField descriptionField;

    private JTextField searchField;
    private JComboBox<String> filterCategoryBox;
    private JTextField dateFilterField;

    private JTextArea expenseArea;
    private JLabel totalLabel;

    private int selectedExpenseIndex = -1;

    private final String FILE_NAME = "expenses.txt";

    // =========================
    // Constructor
    // =========================
    public JavaExpenseTracker() {

        setTitle("Java Expense Tracker");
        setSize(1000, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadExpenses();

        createGUI();

        refreshExpenseArea();
    }

    // =========================
    // GUI
    // =========================
    private void createGUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // =========================
        // Title
        // =========================
        JLabel titleLabel = new JLabel("JAVA EXPENSE TRACKER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // =========================
        // Center panel
        // =========================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // =========================
        // Input panel
        // =========================
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 15));

        amountField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 15));

        String[] categories = {
                "Food",
                "Shopping",
                "Education",
                "Transport",
                "Entertainment",
                "Bills",
                "Health",
                "Other"
        };

        categoryBox = new JComboBox<>(categories);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 15));

        descriptionField = new JTextField();

        JButton addButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Expense");

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        inputPanel.add(categoryLabel);
        inputPanel.add(categoryBox);

        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);

        inputPanel.add(addButton);
        inputPanel.add(editButton);

        centerPanel.add(inputPanel);

        // =========================
        // Delete button
        // =========================
        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton deleteButton = new JButton("Delete Expense");
        JButton clearButton = new JButton("Clear Selection");

        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);

        centerPanel.add(actionPanel);

        // =========================
        // Search / Filter panel
        // =========================
        JPanel filterPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        filterPanel.setBorder(
                BorderFactory.createTitledBorder("Search & Filter")
        );

        JLabel searchLabel = new JLabel("Search Description:");
        JLabel categoryFilterLabel = new JLabel("Category:");
        JLabel dateFilterLabel = new JLabel("Date (dd-MM-yyyy):");

        searchField = new JTextField();

        String[] filterCategories = {
                "All Categories",
                "Food",
                "Shopping",
                "Education",
                "Transport",
                "Entertainment",
                "Bills",
                "Health",
                "Other"
        };

        filterCategoryBox = new JComboBox<>(filterCategories);

        dateFilterField = new JTextField();

        filterPanel.add(searchLabel);
        filterPanel.add(categoryFilterLabel);
        filterPanel.add(dateFilterLabel);

        filterPanel.add(searchField);
        filterPanel.add(filterCategoryBox);
        filterPanel.add(dateFilterField);

        centerPanel.add(filterPanel);

        // =========================
        // Expense area
        // =========================
        expenseArea = new JTextArea();
        expenseArea.setEditable(false);
        expenseArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        expenseArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(expenseArea);

        JPanel expensePanel = new JPanel(new BorderLayout());
        expensePanel.setBorder(
                BorderFactory.createTitledBorder("Expenses")
        );

        expensePanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(expensePanel);

        // =========================
        // Total
        // =========================
        totalLabel = new JLabel("Total Expense: Rs.0.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        centerPanel.add(totalLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // =========================
        // Button actions
        // =========================

        addButton.addActionListener(e -> addExpense());

        editButton.addActionListener(e -> editExpense());

        deleteButton.addActionListener(e -> deleteExpense());

        clearButton.addActionListener(e -> clearFields());

        // Search while typing
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }

                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }

                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }
                }
        );

        filterCategoryBox.addActionListener(
                e -> refreshExpenseArea()
        );

        dateFilterField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }

                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }

                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        refreshExpenseArea();
                    }
                }
        );

        // =========================
        // Select expense by clicking
        // =========================
        expenseArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int line;

                try {
                    line = expenseArea.getLineOfOffset(
                            expenseArea.viewToModel2D(e.getPoint())
                    );
                } catch (Exception ex) {
                    return;
                }

                String selectedLine;

                try {
                    int start = expenseArea.getLineStartOffset(line);
                    int end = expenseArea.getLineEndOffset(line);

                    selectedLine = expenseArea
                            .getText(start, end - start)
                            .trim();

                } catch (Exception ex) {
                    return;
                }

                selectExpense(selectedLine);
            }
        });
    }

    // =========================
    // ADD EXPENSE
    // =========================
    private void addExpense() {

        try {

            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();

            if (amountText.isEmpty() || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter amount and description.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be greater than 0.",
                        "Invalid Amount",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String date = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm"
            ).format(new Date());

            Expense expense = new Expense(
                    date,
                    amount,
                    category,
                    description
            );

            expenses.add(expense);

            saveExpenses();

            clearFields();

            refreshExpenseArea();

            JOptionPane.showMessageDialog(
                    this,
                    "Expense added successfully!"
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid amount.",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // EDIT EXPENSE
    // =========================
    private void editExpense() {

        if (selectedExpenseIndex == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please click an expense first.",
                    "No Expense Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();

            if (amountText.isEmpty() || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter amount and description."
                );

                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be greater than 0."
                );

                return;
            }

            Expense oldExpense = expenses.get(selectedExpenseIndex);

            oldExpense.amount = amount;
            oldExpense.category = category;
            oldExpense.description = description;

            saveExpenses();

            selectedExpenseIndex = -1;

            clearFields();

            refreshExpenseArea();

            JOptionPane.showMessageDialog(
                    this,
                    "Expense edited successfully!"
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid amount."
            );
        }
    }

    // =========================
    // DELETE EXPENSE
    // =========================
    private void deleteExpense() {

        if (selectedExpenseIndex == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please click an expense first.",
                    "No Expense Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {

            expenses.remove(selectedExpenseIndex);

            saveExpenses();

            selectedExpenseIndex = -1;

            clearFields();

            refreshExpenseArea();

            JOptionPane.showMessageDialog(
                    this,
                    "Expense deleted successfully!"
            );
        }
    }

    // =========================
    // SELECT EXPENSE
    // =========================
    private void selectExpense(String selectedLine) {

        for (int i = 0; i < expenses.size(); i++) {

            Expense expense = expenses.get(i);

            if (expense.toString().equals(selectedLine)) {

                selectedExpenseIndex = i;

                amountField.setText(
                        String.valueOf(expense.amount)
                );

                categoryBox.setSelectedItem(
                        expense.category
                );

                descriptionField.setText(
                        expense.description
                );

                break;
            }
        }
    }

    // =========================
    // CLEAR FIELDS
    // =========================
    private void clearFields() {

        amountField.setText("");
        descriptionField.setText("");

        categoryBox.setSelectedIndex(0);

        selectedExpenseIndex = -1;
    }

    // =========================
    // REFRESH EXPENSE DISPLAY
    // =========================
    private void refreshExpenseArea() {

        expenseArea.setText("");

        String searchText =
                searchField == null
                        ? ""
                        : searchField.getText()
                                .trim()
                                .toLowerCase();

        String selectedCategory =
                filterCategoryBox == null
                        ? "All Categories"
                        : (String) filterCategoryBox
                                .getSelectedItem();

        String dateFilter =
                dateFilterField == null
                        ? ""
                        : dateFilterField.getText()
                                .trim();

        double total = 0;

        for (Expense expense : expenses) {

            // =========================
            // Search filter
            // =========================
            if (!searchText.isEmpty()) {

                if (!expense.description
                        .toLowerCase()
                        .contains(searchText)) {

                    continue;
                }
            }

            // =========================
            // Category filter
            // =========================
            if (!selectedCategory.equals("All Categories")) {

                if (!expense.category.equals(
                        selectedCategory)) {

                    continue;
                }
            }

            // =========================
            // Date filter
            // =========================
            if (!dateFilter.isEmpty()) {

                if (!expense.date.startsWith(dateFilter)) {

                    continue;
                }
            }

            expenseArea.append(
                    expense.toString() + "\n"
            );

            total += expense.amount;
        }

        totalLabel.setText(
                "Total Expense: Rs." + total
        );
    }

    // =========================
    // SAVE EXPENSES
    // =========================
    private void saveExpenses() {

        try (PrintWriter writer = new PrintWriter(
                new FileWriter(FILE_NAME))) {

            for (Expense expense : expenses) {

                writer.println(
                        expense.toString()
                );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving expenses:\n"
                            + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LOAD EXPENSES
    // =========================
    private void loadExpenses() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                parseExpense(line);
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading expenses:\n"
                            + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // PARSE OLD + NEW FORMAT
    // =========================
    private void parseExpense(String line) {

        try {

            String[] parts = line.split("\\|");

            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            // New format:
            // date | Rs.amount | category | description
            if (parts.length == 4) {

                String date = parts[0];

                String amountText = parts[1]
                        .replace("Rs.", "")
                        .trim();

                double amount =
                        Double.parseDouble(amountText);

                String category = parts[2];

                String description = parts[3];

                expenses.add(
                        new Expense(
                                date,
                                amount,
                                category,
                                description
                        )
                );

                return;
            }

            // Old format:
            // Rs.amount | category | description
            if (parts.length == 3) {

                String amountText = parts[0]
                        .replace("Rs.", "")
                        .trim();

                double amount =
                        Double.parseDouble(amountText);

                String category = parts[1];

                String description = parts[2];

                String date = "Old";

                expenses.add(
                        new Expense(
                                date,
                                amount,
                                category,
                                description
                        )
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not read expense: " + line
            );
        }
    }

    // =========================
    // MAIN
    // =========================
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JavaExpenseTracker app =
                    new JavaExpenseTracker();

            app.setVisible(true);
        });
    }
}