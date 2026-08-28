import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JavaExpenseTracker extends JFrame {

    // =====================================================
    // EXPENSE CLASS
    // =====================================================

    static class Expense {

        String date;
        double amount;
        String category;
        String description;

        Expense(String date, double amount,
                String category, String description) {

            this.date = date;
            this.amount = amount;
            this.category = category;
            this.description = description;
        }

        @Override
        public String toString() {

            return date
                    + " | Rs."
                    + amount
                    + " | "
                    + category
                    + " | "
                    + description;
        }
    }


    // =====================================================
    // RECURRING EXPENSE CLASS
    // =====================================================

    static class RecurringExpense {

        double amount;
        String category;
        String description;
        String frequency;
        LocalDate nextDate;

        RecurringExpense(
                double amount,
                String category,
                String description,
                String frequency,
                LocalDate nextDate) {

            this.amount = amount;
            this.category = category;
            this.description = description;
            this.frequency = frequency;
            this.nextDate = nextDate;
        }

        String saveFormat() {

            return amount
                    + "|"
                    + category
                    + "|"
                    + description.replace("|", "/")
                    + "|"
                    + frequency
                    + "|"
                    + nextDate;
        }
    }


    // =====================================================
    // VARIABLES
    // =====================================================

    private ArrayList<Expense> expenses =
            new ArrayList<>();

    private ArrayList<RecurringExpense> recurringExpenses =
            new ArrayList<>();

    private JTextField amountField;
    private JComboBox<String> categoryBox;
    private JTextField descriptionField;

    private JCheckBox recurringCheckBox;
    private JComboBox<String> recurringFrequencyBox;

    private JTextField searchField;
    private JComboBox<String> filterCategoryBox;
    private JTextField dateFilterField;

    private JComboBox<String> sortBox;

    private JTextArea expenseArea;

    private JLabel totalLabel;
    private JLabel countLabel;
    private JLabel highestLabel;

    private JTextArea categorySummaryArea;

    private JLabel budgetLabel;
    private JLabel spentLabel;
    private JLabel remainingLabel;

    private int selectedExpenseIndex = -1;

    private final String FILE_NAME =
            "expenses.txt";

    private final String BUDGET_FILE =
            "budget.txt";

    private final String RECURRING_FILE =
            "recurring.txt";

    private double monthlyBudget = 0;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public JavaExpenseTracker() {

        setTitle("Java Expense Tracker");

        setSize(1200, 1000);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        loadExpenses();

        loadBudget();

        loadRecurringExpenses();

        processRecurringExpenses();

        createGUI();

        refreshExpenseArea();

        updateDashboard();
    }


    // =====================================================
    // GUI
    // =====================================================

    private void createGUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );


        // =================================================
        // TITLE
        // =================================================

        JLabel titleLabel =
                new JLabel(
                        "JAVA EXPENSE TRACKER"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );


        // =================================================
        // CENTER PANEL
        // =================================================

        JPanel centerPanel =
                new JPanel();

        centerPanel.setLayout(
                new BoxLayout(
                        centerPanel,
                        BoxLayout.Y_AXIS
                )
        );


        // =================================================
        // INPUT PANEL
        // =================================================

        JPanel inputPanel =
                new JPanel(
                        new GridLayout(
                                5,
                                2,
                                10,
                                10
                        )
                );

        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        30,
                        10,
                        30
                )
        );


        JLabel amountLabel =
                new JLabel("Amount:");

        amountLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        amountField =
                new JTextField();


        JLabel categoryLabel =
                new JLabel("Category:");

        categoryLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );


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


        categoryBox =
                new JComboBox<>(
                        categories
                );


        JLabel descriptionLabel =
                new JLabel("Description:");

        descriptionLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );


        descriptionField =
                new JTextField();


        JLabel recurringLabel =
                new JLabel("Recurring:");

        recurringLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );


        JPanel recurringPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );


        recurringCheckBox =
                new JCheckBox(
                        "Recurring Expense"
                );


        recurringFrequencyBox =
                new JComboBox<>(
                        new String[]{
                                "Daily",
                                "Weekly",
                                "Monthly"
                        }
                );


        recurringFrequencyBox.setEnabled(
                false
        );


        recurringCheckBox.addActionListener(
                e ->
                        recurringFrequencyBox
                                .setEnabled(
                                        recurringCheckBox
                                                .isSelected()
                                )
        );


        recurringPanel.add(
                recurringCheckBox
        );

        recurringPanel.add(
                recurringFrequencyBox
        );


        JButton addButton =
                new JButton(
                        "Add Expense"
                );


        JButton editButton =
                new JButton(
                        "Edit Expense"
                );


        inputPanel.add(
                amountLabel
        );

        inputPanel.add(
                amountField
        );

        inputPanel.add(
                categoryLabel
        );

        inputPanel.add(
                categoryBox
        );

        inputPanel.add(
                descriptionLabel
        );

        inputPanel.add(
                descriptionField
        );

        inputPanel.add(
                recurringLabel
        );

        inputPanel.add(
                recurringPanel
        );

        inputPanel.add(
                new JLabel("")
        );

        inputPanel.add(
                addButton
        );


        centerPanel.add(
                inputPanel
        );


        JPanel editPanel =
                new JPanel(
                        new FlowLayout()
                );

        editPanel.add(
                editButton
        );

        centerPanel.add(
                editPanel
        );


        // =================================================
        // ACTION BUTTONS
        // =================================================

        JPanel actionPanel =
                new JPanel(
                        new FlowLayout()
                );


        JButton deleteButton =
                new JButton(
                        "Delete Expense"
                );


        JButton clearButton =
                new JButton(
                        "Clear Selection"
                );


        JButton budgetButton =
                new JButton(
                        "Set Monthly Budget"
                );


        JButton exportButton =
                new JButton(
                        "Export Expenses"
                );


        JButton chartsButton =
                new JButton(
                        "View Charts"
                );


        JButton recurringButton =
                new JButton(
                        "Manage Recurring"
                );


        actionPanel.add(
                deleteButton
        );

        actionPanel.add(
                clearButton
        );

        actionPanel.add(
                budgetButton
        );

        actionPanel.add(
                exportButton
        );

        actionPanel.add(
                chartsButton
        );

        actionPanel.add(
                recurringButton
        );


        centerPanel.add(
                actionPanel
        );


        // =================================================
        // SEARCH & FILTER
        // =================================================

        JPanel filterPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                3,
                                10,
                                5
                        )
                );


        filterPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Search & Filter"
                )
        );


        JLabel searchLabel =
                new JLabel(
                        "Search Description:"
                );


        JLabel categoryFilterLabel =
                new JLabel(
                        "Category:"
                );


        JLabel dateFilterLabel =
                new JLabel(
                        "Date (dd-MM-yyyy):"
                );


        searchField =
                new JTextField();


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


        filterCategoryBox =
                new JComboBox<>(
                        filterCategories
                );


        dateFilterField =
                new JTextField();


        filterPanel.add(
                searchLabel
        );

        filterPanel.add(
                categoryFilterLabel
        );

        filterPanel.add(
                dateFilterLabel
        );


        filterPanel.add(
                searchField
        );

        filterPanel.add(
                filterCategoryBox
        );

        filterPanel.add(
                dateFilterField
        );


        centerPanel.add(
                filterPanel
        );


        // =================================================
        // SORT PANEL
        // =================================================

        JPanel sortPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );


        JLabel sortLabel =
                new JLabel(
                        "Sort Expenses:"
                );


        sortLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );


        String[] sortOptions = {

                "Newest First",
                "Oldest First",
                "Highest Amount",
                "Lowest Amount",
                "Description A-Z"
        };


        sortBox =
                new JComboBox<>(
                        sortOptions
                );


        sortPanel.add(
                sortLabel
        );

        sortPanel.add(
                sortBox
        );


        centerPanel.add(
                sortPanel
        );


        // =================================================
        // EXPENSE LIST
        // =================================================

        expenseArea =
                new JTextArea();


        expenseArea.setEditable(
                false
        );


        expenseArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );


        expenseArea.setLineWrap(
                false
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        expenseArea
                );


        scrollPane.setPreferredSize(
                new Dimension(
                        900,
                        260
                )
        );


        JPanel expensePanel =
                new JPanel(
                        new BorderLayout()
                );


        expensePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Expenses"
                )
        );


        expensePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        centerPanel.add(
                expensePanel
        );


        // =================================================
        // DASHBOARD
        // =================================================

        JPanel dashboardPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        dashboardPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Spending Dashboard"
                )
        );


        JPanel statsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                10,
                                10
                        )
                );


        totalLabel =
                new JLabel(
                        "Total Spending: Rs.0.0"
                );


        countLabel =
                new JLabel(
                        "Expenses: 0"
                );


        highestLabel =
                new JLabel(
                        "Highest Expense: Rs.0.0"
                );


        totalLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        countLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        highestLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        Font statFont =
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                );


        totalLabel.setFont(
                statFont
        );

        countLabel.setFont(
                statFont
        );

        highestLabel.setFont(
                statFont
        );


        statsPanel.add(
                totalLabel
        );

        statsPanel.add(
                countLabel
        );

        statsPanel.add(
                highestLabel
        );


        dashboardPanel.add(
                statsPanel,
                BorderLayout.NORTH
        );


        // =================================================
        // CATEGORY SUMMARY
        // =================================================

        categorySummaryArea =
                new JTextArea();


        categorySummaryArea.setEditable(
                false
        );


        categorySummaryArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );


        JScrollPane summaryScrollPane =
                new JScrollPane(
                        categorySummaryArea
                );


        summaryScrollPane.setPreferredSize(
                new Dimension(
                        900,
                        130
                )
        );


        JPanel summaryPanel =
                new JPanel(
                        new BorderLayout()
                );


        summaryPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Category-wise Spending"
                )
        );


        summaryPanel.add(
                summaryScrollPane,
                BorderLayout.CENTER
        );


        dashboardPanel.add(
                summaryPanel,
                BorderLayout.CENTER
        );


        centerPanel.add(
                dashboardPanel
        );


        // =================================================
        // BUDGET PANEL
        // =================================================

        JPanel budgetPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                10,
                                10
                        )
                );


        budgetPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Monthly Budget"
                )
        );


        budgetLabel =
                new JLabel(
                        "Budget: Rs.0.0"
                );


        spentLabel =
                new JLabel(
                        "This Month: Rs.0.0"
                );


        remainingLabel =
                new JLabel(
                        "Remaining: Rs.0.0"
                );


        budgetLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        spentLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        remainingLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        budgetLabel.setFont(
                statFont
        );

        spentLabel.setFont(
                statFont
        );

        remainingLabel.setFont(
                statFont
        );


        budgetPanel.add(
                budgetLabel
        );

        budgetPanel.add(
                spentLabel
        );

        budgetPanel.add(
                remainingLabel
        );


        centerPanel.add(
                budgetPanel
        );


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        setContentPane(
                mainPanel
        );


        // =================================================
        // BUTTON ACTIONS
        // =================================================

        addButton.addActionListener(
                e -> addExpense()
        );


        editButton.addActionListener(
                e -> editExpense()
        );


        deleteButton.addActionListener(
                e -> deleteExpense()
        );


        clearButton.addActionListener(
                e -> clearFields()
        );


        budgetButton.addActionListener(
                e -> setMonthlyBudget()
        );


        exportButton.addActionListener(
                e -> exportExpenses()
        );


        chartsButton.addActionListener(
                e -> showCharts()
        );


        recurringButton.addActionListener(
                e -> manageRecurringExpenses()
        );


        // =================================================
        // SEARCH
        // =================================================

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            public void insertUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }


                            public void removeUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }


                            public void changedUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }
                        }
                );


        // =================================================
        // CATEGORY FILTER
        // =================================================

        filterCategoryBox.addActionListener(
                e -> refreshExpenseArea()
        );


        // =================================================
        // DATE FILTER
        // =================================================

        dateFilterField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            public void insertUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }


                            public void removeUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }


                            public void changedUpdate(
                                    DocumentEvent e) {

                                refreshExpenseArea();
                            }
                        }
                );


        // =================================================
        // SORT
        // =================================================

        sortBox.addActionListener(
                e -> {

                    sortExpenses();

                    refreshExpenseArea();
                }
        );


        // =================================================
        // CLICK EXPENSE
        // =================================================

        expenseArea.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        int position =
                                expenseArea
                                        .viewToModel2D(
                                                e.getPoint()
                                        );

                        try {

                            int line =
                                    expenseArea
                                            .getLineOfOffset(
                                                    position
                                            );

                            int start =
                                    expenseArea
                                            .getLineStartOffset(
                                                    line
                                            );

                            int end =
                                    expenseArea
                                            .getLineEndOffset(
                                                    line
                                            );

                            String selectedLine =
                                    expenseArea
                                            .getText(
                                                    start,
                                                    end - start
                                            )
                                            .trim();

                            selectExpense(
                                    selectedLine
                            );

                        } catch (Exception ex) {

                            // Ignore invalid clicks
                        }
                    }
                }
        );
    }


    // =====================================================
    // ADD EXPENSE
    // =====================================================

    private void addExpense() {

        try {

            String amountText =
                    amountField
                            .getText()
                            .trim();

            String description =
                    descriptionField
                            .getText()
                            .trim();

            String category =
                    (String)
                            categoryBox
                                    .getSelectedItem();


            if (amountText.isEmpty()
                    || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter amount and description.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }


            double amount =
                    Double.parseDouble(
                            amountText
                    );


            if (amount <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Amount must be greater than 0.",
                        "Invalid Amount",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }


            String date =
                    new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm"
                    ).format(
                            new Date()
                    );


            expenses.add(
                    new Expense(
                            date,
                            amount,
                            category,
                            description
                    )
            );


            // =================================================
            // RECURRING EXPENSE
            // =================================================

            if (recurringCheckBox.isSelected()) {

                String frequency =
                        (String)
                                recurringFrequencyBox
                                        .getSelectedItem();


                LocalDate nextDate =
                        LocalDate.now();


                if (frequency.equals("Daily")) {

                    nextDate =
                            nextDate.plusDays(1);

                } else if (
                        frequency.equals("Weekly")) {

                    nextDate =
                            nextDate.plusWeeks(1);

                } else if (
                        frequency.equals("Monthly")) {

                    nextDate =
                            nextDate.plusMonths(1);
                }


                recurringExpenses.add(
                        new RecurringExpense(
                                amount,
                                category,
                                description,
                                frequency,
                                nextDate
                        )
                );


                saveRecurringExpenses();
            }


            saveExpenses();

            clearFields();

            refreshExpenseArea();

            updateDashboard();


            JOptionPane.showMessageDialog(
                    this,
                    recurringCheckBox.isSelected()
                            ? "Expense added and recurring expense created!"
                            : "Expense added successfully!"
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


    // =====================================================
    // PROCESS RECURRING EXPENSES
    // =====================================================

    private void processRecurringExpenses() {

        if (recurringExpenses.isEmpty()) {
            return;
        }


        LocalDate today =
                LocalDate.now();


        boolean changed = false;


        for (RecurringExpense recurring :
                recurringExpenses) {

            while (!recurring.nextDate.isAfter(today)) {

                String date =
                        recurring.nextDate
                                .format(
                                        DateTimeFormatter
                                                .ofPattern(
                                                        "dd-MM-yyyy"
                                                )
                                )
                                + " 09:00";


                expenses.add(
                        new Expense(
                                date,
                                recurring.amount,
                                recurring.category,
                                recurring.description
                                        + " [Recurring]"
                        )
                );


                if (recurring.frequency.equals(
                        "Daily")) {

                    recurring.nextDate =
                            recurring.nextDate
                                    .plusDays(1);

                } else if (
                        recurring.frequency.equals(
                                "Weekly")) {

                    recurring.nextDate =
                            recurring.nextDate
                                    .plusWeeks(1);

                } else {

                    recurring.nextDate =
                            recurring.nextDate
                                    .plusMonths(1);
                }


                changed = true;
            }
        }


        if (changed) {

            saveExpenses();

            saveRecurringExpenses();
        }
    }


    // =====================================================
    // EDIT EXPENSE
    // =====================================================

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

            double amount =
                    Double.parseDouble(
                            amountField
                                    .getText()
                                    .trim()
                    );


            String category =
                    (String)
                            categoryBox
                                    .getSelectedItem();


            String description =
                    descriptionField
                            .getText()
                            .trim();


            if (amount <= 0
                    || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter a valid amount and description."
                );

                return;
            }


            Expense expense =
                    expenses.get(
                            selectedExpenseIndex
                    );


            expense.amount =
                    amount;

            expense.category =
                    category;

            expense.description =
                    description;


            saveExpenses();

            selectedExpenseIndex = -1;

            clearFields();

            refreshExpenseArea();

            updateDashboard();


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


    // =====================================================
    // DELETE EXPENSE
    // =====================================================

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


        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this expense?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (answer ==
                JOptionPane.YES_OPTION) {

            expenses.remove(
                    selectedExpenseIndex
            );


            saveExpenses();

            selectedExpenseIndex = -1;

            clearFields();

            refreshExpenseArea();

            updateDashboard();


            JOptionPane.showMessageDialog(
                    this,
                    "Expense deleted successfully!"
            );
        }
    }


    // =====================================================
    // SELECT EXPENSE
    // =====================================================

    private void selectExpense(
            String selectedLine) {

        for (int i = 0;
             i < expenses.size();
             i++) {

            Expense expense =
                    expenses.get(i);


            if (expense.toString()
                    .equals(selectedLine)) {

                selectedExpenseIndex =
                        i;


                amountField.setText(
                        String.valueOf(
                                expense.amount
                        )
                );


                categoryBox.setSelectedItem(
                        expense.category
                );


                descriptionField.setText(
                        expense.description
                );


                recurringCheckBox.setSelected(
                        false
                );


                recurringFrequencyBox.setEnabled(
                        false
                );


                break;
            }
        }
    }


    // =====================================================
    // CLEAR FIELDS
    // =====================================================

    private void clearFields() {

        amountField.setText("");

        descriptionField.setText("");

        categoryBox.setSelectedIndex(0);

        recurringCheckBox.setSelected(
                false
        );

        recurringFrequencyBox.setEnabled(
                false
        );

        selectedExpenseIndex = -1;
    }


    // =====================================================
    // SEARCH / FILTER
    // =====================================================

    private boolean matchesFilters(
            Expense expense) {

        String search =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase();


        if (!search.isEmpty()
                && !expense.description
                .toLowerCase()
                .contains(search)) {

            return false;
        }


        String category =
                (String)
                        filterCategoryBox
                                .getSelectedItem();


        if (!category.equals(
                "All Categories")
                && !expense.category.equals(
                        category)) {

            return false;
        }


        String date =
                dateFilterField
                        .getText()
                        .trim();


        if (!date.isEmpty()
                && !expense.date.startsWith(
                        date)) {

            return false;
        }


        return true;
    }


    // =====================================================
    // REFRESH EXPENSE AREA
    // =====================================================

    private void refreshExpenseArea() {

        if (expenseArea == null) {
            return;
        }


        expenseArea.setText("");


        for (Expense expense :
                expenses) {

            if (matchesFilters(
                    expense)) {

                expenseArea.append(
                        expense.toString()
                                + "\n"
                );
            }
        }


        updateDashboard();
    }


    // =====================================================
    // SORT EXPENSES
    // =====================================================

    private void sortExpenses() {

        String option =
                (String)
                        sortBox
                                .getSelectedItem();


        if (option == null) {
            return;
        }


        switch (option) {

            case "Newest First":

                expenses.sort(
                        (a, b) ->
                                compareDates(
                                        b.date,
                                        a.date
                                )
                );

                break;


            case "Oldest First":

                expenses.sort(
                        (a, b) ->
                                compareDates(
                                        a.date,
                                        b.date
                                )
                );

                break;


            case "Highest Amount":

                expenses.sort(
                        (a, b) ->
                                Double.compare(
                                        b.amount,
                                        a.amount
                                )
                );

                break;


            case "Lowest Amount":

                expenses.sort(
                        (a, b) ->
                                Double.compare(
                                        a.amount,
                                        b.amount
                                )
                );

                break;


            case "Description A-Z":

                expenses.sort(
                        (a, b) ->
                                a.description
                                        .compareToIgnoreCase(
                                                b.description
                                        )
                );

                break;
        }


        selectedExpenseIndex = -1;
    }


    // =====================================================
    // COMPARE DATES
    // =====================================================

    private int compareDates(
            String date1,
            String date2) {

        try {

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm"
                    );


            Date d1 =
                    formatter.parse(date1);

            Date d2 =
                    formatter.parse(date2);


            return d1.compareTo(d2);

        } catch (Exception e) {

            return date1.compareTo(
                    date2
            );
        }
    }


    // =====================================================
    // DASHBOARD
    // =====================================================

    private void updateDashboard() {

        if (totalLabel == null) {
            return;
        }


        double total = 0;

        double highest = 0;

        int count = 0;


        HashMap<String, Double>
                categoryTotals =
                new HashMap<>();


        for (Expense expense :
                expenses) {

            if (!matchesFilters(
                    expense)) {

                continue;
            }


            total +=
                    expense.amount;


            count++;


            highest =
                    Math.max(
                            highest,
                            expense.amount
                    );


            categoryTotals.put(
                    expense.category,
                    categoryTotals.getOrDefault(
                            expense.category,
                            0.0
                    )
                    + expense.amount
            );
        }


        totalLabel.setText(
                "Total Spending: Rs."
                        + total
        );


        countLabel.setText(
                "Expenses: "
                        + count
        );


        highestLabel.setText(
                "Highest Expense: Rs."
                        + highest
        );


        categorySummaryArea.setText("");


        ArrayList<String> categories =
                new ArrayList<>(
                        categoryTotals.keySet()
                );


        Collections.sort(
                categories
        );


        for (String category :
                categories) {

            double value =
                    categoryTotals.get(
                            category
                    );


            double percentage =
                    total == 0
                            ? 0
                            : value / total * 100;


            categorySummaryArea.append(
                    String.format(
                            "%-18s Rs.%-10.2f %.1f%%\n",
                            category,
                            value,
                            percentage
                    )
            );
        }


        if (categories.isEmpty()) {

            categorySummaryArea.append(
                    "No expenses to display."
            );
        }


        double currentMonth =
                calculateCurrentMonthSpending();


        budgetLabel.setText(
                "Budget: Rs."
                        + monthlyBudget
        );


        spentLabel.setText(
                "This Month: Rs."
                        + currentMonth
        );


        if (monthlyBudget <= 0) {

            remainingLabel.setText(
                    "Remaining: Set a budget"
            );

        } else {

            double remaining =
                    monthlyBudget
                            - currentMonth;


            if (remaining < 0) {

                remainingLabel.setText(
                        "OVER BUDGET: Rs."
                                + Math.abs(
                                remaining
                        )
                );

            } else {

                remainingLabel.setText(
                        "Remaining: Rs."
                                + remaining
                );
            }
        }
    }


    // =====================================================
    // CURRENT MONTH SPENDING
    // =====================================================

    private double
    calculateCurrentMonthSpending() {

        String currentMonth =
                new SimpleDateFormat(
                        "MM-yyyy"
                ).format(
                        new Date()
                );


        double total = 0;


        for (Expense expense :
                expenses) {

            if (expense.date.contains(
                    "-" + currentMonth)) {

                total +=
                        expense.amount;
            }
        }


        return total;
    }


    // =====================================================
    // MONTHLY BUDGET
    // =====================================================

    private void setMonthlyBudget() {

        String input =
                JOptionPane.showInputDialog(
                        this,
                        "Enter your monthly budget:",
                        monthlyBudget
                );


        if (input == null) {
            return;
        }


        try {

            double budget =
                    Double.parseDouble(
                            input.trim()
                    );


            if (budget <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Budget must be greater than 0."
                );

                return;
            }


            monthlyBudget =
                    budget;


            saveBudget();

            updateDashboard();


            JOptionPane.showMessageDialog(
                    this,
                    "Monthly budget set to Rs."
                            + monthlyBudget
            );


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number."
            );
        }
    }


    // =====================================================
    // MANAGE RECURRING EXPENSES
    // =====================================================

    private void manageRecurringExpenses() {

        if (recurringExpenses.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No recurring expenses created yet."
            );

            return;
        }


        StringBuilder message =
                new StringBuilder();


        for (int i = 0;
             i < recurringExpenses.size();
             i++) {

            RecurringExpense r =
                    recurringExpenses.get(i);


            message.append(
                    i + 1
            )
            .append(". Rs.")
            .append(r.amount)
            .append(" | ")
            .append(r.category)
            .append(" | ")
            .append(r.description)
            .append(" | ")
            .append(r.frequency)
            .append(" | Next: ")
            .append(r.nextDate)
            .append("\n");
        }


        JTextArea area =
                new JTextArea(
                        message.toString()
                );


        area.setEditable(false);

        area.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );


        JScrollPane scroll =
                new JScrollPane(area);


        scroll.setPreferredSize(
                new Dimension(
                        700,
                        300
                )
        );


        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Recurring Expenses",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    // =====================================================
    // SAVE RECURRING
    // =====================================================

    private void saveRecurringExpenses() {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        RECURRING_FILE
                                )
                        )
        ) {

            for (RecurringExpense r :
                    recurringExpenses) {

                writer.println(
                        r.saveFormat()
                );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving recurring expenses:\n"
                            + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // LOAD RECURRING
    // =====================================================

    private void loadRecurringExpenses() {

        File file =
                new File(
                        RECURRING_FILE
                );


        if (!file.exists()) {
            return;
        }


        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(file)
                        )
        ) {

            String line;


            while (
                    (line =
                            reader.readLine())
                            != null
            ) {

                String[] parts =
                        line.split(
                                "\\|"
                        );


                if (parts.length != 5) {
                    continue;
                }


                try {

                    double amount =
                            Double.parseDouble(
                                    parts[0]
                            );


                    String category =
                            parts[1];


                    String description =
                            parts[2];


                    String frequency =
                            parts[3];


                    LocalDate nextDate =
                            LocalDate.parse(
                                    parts[4]
                            );


                    recurringExpenses.add(
                            new RecurringExpense(
                                    amount,
                                    category,
                                    description,
                                    frequency,
                                    nextDate
                            )
                    );

                } catch (Exception ignored) {

                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading recurring expenses:\n"
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // EXPORT CSV
    // =====================================================

    private void exportExpenses() {

        JFileChooser chooser =
                new JFileChooser();


        chooser.setDialogTitle(
                "Export Expenses"
        );


        chooser.setSelectedFile(
                new File(
                        "expenses_export.csv"
                )
        );


        int result =
                chooser.showSaveDialog(
                        this
                );


        if (result !=
                JFileChooser.APPROVE_OPTION) {

            return;
        }


        File file =
                chooser.getSelectedFile();


        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        file
                                )
                        )
        ) {

            writer.println(
                    "Date,Amount,Category,Description"
            );


            for (Expense expense :
                    expenses) {

                writer.println(
                        csvEscape(
                                expense.date
                        )
                        + ","
                        + expense.amount
                        + ","
                        + csvEscape(
                                expense.category
                        )
                        + ","
                        + csvEscape(
                                expense.description
                        )
                );
            }


            JOptionPane.showMessageDialog(
                    this,
                    "Expenses exported successfully!"
            );


        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error exporting expenses:\n"
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // CSV ESCAPE
    // =====================================================

    private String csvEscape(
            String text) {

        if (text == null) {
            return "";
        }


        if (text.contains(",")
                || text.contains("\"")
                || text.contains("\n")) {

            return "\""
                    + text.replace(
                            "\"",
                            "\"\""
                    )
                    + "\"";
        }


        return text;
    }


    // =====================================================
    // SAVE EXPENSES
    // =====================================================

    private void saveExpenses() {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        FILE_NAME
                                )
                        )
        ) {

            for (Expense expense :
                    expenses) {

                writer.println(
                        expense.toString()
                );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving expenses:\n"
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // LOAD EXPENSES
    // =====================================================

    private void loadExpenses() {

        File file =
                new File(
                        FILE_NAME
                );


        if (!file.exists()) {
            return;
        }


        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(
                                        file
                                )
                        )
        ) {

            String line;


            while (
                    (line =
                            reader.readLine())
                            != null
            ) {

                line =
                        line.trim();


                if (!line.isEmpty()) {

                    parseExpense(
                            line
                    );
                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading expenses:\n"
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // PARSE EXPENSE
    // =====================================================

    private void parseExpense(
            String line) {

        try {

            String[] parts =
                    line.split(
                            "\\|"
                    );


            for (int i = 0;
                 i < parts.length;
                 i++) {

                parts[i] =
                        parts[i].trim();
            }


            // NEW FORMAT

            if (parts.length == 4) {

                String date =
                        parts[0];


                double amount =
                        Double.parseDouble(
                                parts[1]
                                        .replace(
                                                "Rs.",
                                                ""
                                        )
                                        .trim()
                        );


                String category =
                        parts[2];


                String description =
                        parts[3];


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


            // OLD FORMAT

            if (parts.length == 3) {

                double amount =
                        Double.parseDouble(
                                parts[0]
                                        .replace(
                                                "Rs.",
                                                ""
                                        )
                                        .trim()
                        );


                String category =
                        parts[1];


                String description =
                        parts[2];


                expenses.add(
                        new Expense(
                                "Old",
                                amount,
                                category,
                                description
                        )
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not read expense: "
                            + line
            );
        }
    }


    // =====================================================
    // SAVE BUDGET
    // =====================================================

    private void saveBudget() {

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        BUDGET_FILE
                                )
                        )
        ) {

            writer.println(
                    monthlyBudget
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving budget:\n"
                            + e.getMessage()
            );
        }
    }


    // =====================================================
    // LOAD BUDGET
    // =====================================================

    private void loadBudget() {

        File file =
                new File(
                        BUDGET_FILE
                );


        if (!file.exists()) {
            return;
        }


        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(
                                        file
                                )
                        )
        ) {

            String line =
                    reader.readLine();


            if (line != null) {

                monthlyBudget =
                        Double.parseDouble(
                                line.trim()
                        );
            }

        } catch (Exception e) {

            monthlyBudget = 0;
        }
    }


    // =====================================================
    // CHARTS
    // =====================================================

    private void showCharts() {

        if (expenses.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Add some expenses first."
            );

            return;
        }


        Map<String, Double>
                categoryTotals =
                new LinkedHashMap<>();


        for (Expense expense :
                expenses) {

            categoryTotals.put(
                    expense.category,
                    categoryTotals.getOrDefault(
                            expense.category,
                            0.0
                    )
                    + expense.amount
            );
        }


        JFrame chartFrame =
                new JFrame(
                        "Expense Analytics"
                );


        chartFrame.setSize(
                1050,
                620
        );


        chartFrame.setLocationRelativeTo(
                this
        );


        chartFrame.setLayout(
                new GridLayout(
                        1,
                        2,
                        10,
                        10
                )
        );


        chartFrame.add(
                new ChartPanel(
                        categoryTotals,
                        false
                )
        );


        chartFrame.add(
                new ChartPanel(
                        categoryTotals,
                        true
                )
        );


        chartFrame.setVisible(
                true
        );
    }


    // =====================================================
    // CHART PANEL
    // =====================================================

    class ChartPanel extends JPanel {

        private Map<String, Double> data;

        private boolean pie;


        ChartPanel(
                Map<String, Double> data,
                boolean pie) {

            this.data = data;

            this.pie = pie;

            setBorder(
                    BorderFactory.createTitledBorder(
                            pie
                                    ? "Spending Distribution"
                                    : "Category Spending"
                    )
            );
        }


        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);


            Graphics2D g2 =
                    (Graphics2D) g;


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            double total = 0;

            double max = 0;


            for (double value :
                    data.values()) {

                total += value;

                max =
                        Math.max(
                                max,
                                value
                        );
            }


            if (pie) {

                drawPie(
                        g2,
                        total
                );

            } else {

                drawBars(
                        g2,
                        max
                );
            }
        }


        private void drawBars(
                Graphics2D g2,
                double max) {

            int left = 70;

            int bottom = 70;

            int top = 50;

            int width =
                    getWidth()
                            - left
                            - 30;

            int height =
                    getHeight()
                            - top
                            - bottom;


            g2.drawLine(
                    left,
                    top,
                    left,
                    getHeight()
                            - bottom
            );


            g2.drawLine(
                    left,
                    getHeight()
                            - bottom,
                    getWidth()
                            - 30,
                    getHeight()
                            - bottom
            );


            int count =
                    Math.max(
                            1,
                            data.size()
                    );


            int slot =
                    width / count;


            int barWidth =
                    Math.max(
                            25,
                            slot - 20
                    );


            int x =
                    left + 10;


            for (Map.Entry<String, Double> entry :
                    data.entrySet()) {

                int barHeight =
                        max == 0
                                ? 0
                                : (int)
                                ((entry.getValue()
                                        / max)
                                        * (height - 20));


                int y =
                        getHeight()
                                - bottom
                                - barHeight;


                g2.fillRect(
                        x,
                        y,
                        barWidth,
                        barHeight
                );


                g2.drawString(
                        String.format(
                                "%.0f",
                                entry.getValue()
                        ),
                        x,
                        y - 5
                );


                String label =
                        entry.getKey();


                if (label.length() > 10) {

                    label =
                            label.substring(
                                    0,
                                    10
                            );
                }


                g2.drawString(
                        label,
                        x,
                        getHeight()
                                - bottom
                                + 20
                );


                x += slot;
            }
        }


        private void drawPie(
                Graphics2D g2,
                double total) {

            int size =
                    Math.min(
                            getWidth(),
                            getHeight()
                    )
                    - 150;


            int x =
                    (getWidth() - size)
                            / 2;


            int y = 50;

            int startAngle = 0;

            int legendY =
                    y + size + 30;


            int index = 0;


            for (Map.Entry<String, Double> entry :
                    data.entrySet()) {

                int angle =
                        (int)
                                Math.round(
                                        entry.getValue()
                                                / total
                                                * 360
                                );


                g2.setColor(
                        new Color(
                                70 + (index * 37) % 160,
                                70 + (index * 53) % 150,
                                70 + (index * 71) % 150
                        )
                );


                g2.fillArc(
                        x,
                        y,
                        size,
                        size,
                        startAngle,
                        angle
                );


                g2.setColor(
                        Color.BLACK
                );


                g2.drawString(
                        entry.getKey()
                                + " ("
                                + String.format(
                                "%.1f%%",
                                entry.getValue()
                                        / total
                                        * 100
                        )
                                + ")",
                        20,
                        legendY
                                + index * 22
                );


                startAngle += angle;

                index++;
            }
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    JavaExpenseTracker app =
                            new JavaExpenseTracker();

                    app.setVisible(
                            true
                    );
                }
        );
    }
}