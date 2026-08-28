import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaExpenseTracker {

    static final String FILE_NAME = "expenses.txt";

    public static void main(String[] args) {

        // =========================
        // MAIN WINDOW
        // =========================

        JFrame frame = new JFrame("Java Expense Tracker");

        frame.setSize(800, 600);

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setLayout(
                new BorderLayout(10, 10)
        );

        // =========================
        // TITLE
        // =========================

        JLabel title = new JLabel(
                "JAVA EXPENSE TRACKER",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 10, 10, 10
                )
        );

        frame.add(
                title,
                BorderLayout.NORTH
        );

        // =========================
        // INPUT PANEL
        // =========================

        JPanel inputPanel = new JPanel(
                new GridLayout(4, 2, 10, 10)
        );

        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 30, 10, 30
                )
        );

        JLabel amountLabel =
                new JLabel("Amount:");

        JTextField amountField =
                new JTextField();

        JLabel categoryLabel =
                new JLabel("Category:");

        String[] categories = {
                "Food",
                "Travel",
                "Shopping",
                "Education",
                "Other"
        };

        JComboBox<String> categoryBox =
                new JComboBox<>(categories);

        JLabel descriptionLabel =
                new JLabel("Description:");

        JTextField descriptionField =
                new JTextField();

        JButton addButton =
                new JButton("Add Expense");

        JButton deleteButton =
                new JButton("Delete Expense");

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        inputPanel.add(categoryLabel);
        inputPanel.add(categoryBox);

        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);

        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        // =========================
        // EXPENSE LIST
        // =========================

        DefaultListModel<String> expenseModel =
                new DefaultListModel<>();

        JList<String> expenseList =
                new JList<>(expenseModel);

        expenseList.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        JScrollPane scrollPane =
                new JScrollPane(expenseList);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Expenses"
                )
        );

        // =========================
        // CENTER PANEL
        // =========================

        JPanel centerPanel =
                new JPanel(new BorderLayout(10, 10));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 20, 0, 20
                )
        );

        centerPanel.add(
                inputPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        frame.add(
                centerPanel,
                BorderLayout.CENTER
        );

        // =========================
        // TOTAL LABEL
        // =========================

        JLabel totalLabel =
                new JLabel(
                        "Total Expense: Rs.0.0",
                        SwingConstants.CENTER
                );

        totalLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        totalLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 15, 10
                )
        );

        frame.add(
                totalLabel,
                BorderLayout.SOUTH
        );

        // =========================
        // DATE FORMAT
        // =========================

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy HH:mm"
                );

        // =========================
        // TOTAL EXPENSE
        // =========================

        final double[] totalExpense = {0};

        // =========================
        // LOAD SAVED EXPENSES
        // =========================

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(FILE_NAME)
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                expenseModel.addElement(line);

                double amount =
                        getAmountFromExpense(line);

                totalExpense[0] += amount;
            }

            reader.close();

        } catch (IOException e) {

            // File does not exist yet.
        }

        totalLabel.setText(
                "Total Expense: Rs."
                + totalExpense[0]
        );

        // =========================
        // ADD EXPENSE
        // =========================

        addButton.addActionListener(e -> {

            try {

                double amount =
                        Double.parseDouble(
                                amountField.getText()
                        );

                if (amount <= 0) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Amount must be greater than 0!"
                    );

                    return;
                }

                String category =
                        (String)
                        categoryBox.getSelectedItem();

                String description =
                        descriptionField.getText();

                if (description.isEmpty()) {

                    description =
                            "No description";
                }

                String dateTime =
                        LocalDateTime.now()
                                .format(formatter);

                String expense =
                        dateTime
                        + " | Rs." + amount
                        + " | " + category
                        + " | " + description;

                expenseModel.addElement(
                        expense
                );

                totalExpense[0] += amount;

                totalLabel.setText(
                        "Total Expense: Rs."
                        + totalExpense[0]
                );

                amountField.setText("");
                descriptionField.setText("");

                saveExpenses(
                        expenseModel
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid amount!"
                );
            }
        });

        // =========================
        // DELETE EXPENSE
        // =========================

        deleteButton.addActionListener(e -> {

            int selectedIndex =
                    expenseList.getSelectedIndex();

            if (selectedIndex != -1) {

                String selectedExpense =
                        expenseModel.getElementAt(
                                selectedIndex
                        );

                double amount =
                        getAmountFromExpense(
                                selectedExpense
                        );

                totalExpense[0] -= amount;

                expenseModel.remove(
                        selectedIndex
                );

                totalLabel.setText(
                        "Total Expense: Rs."
                        + totalExpense[0]
                );

                saveExpenses(
                        expenseModel
                );

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an expense to delete!"
                );
            }
        });

        // =========================
        // CENTER WINDOW
        // =========================

        frame.setLocationRelativeTo(null);

        // =========================
        // SHOW WINDOW
        // =========================

        frame.setVisible(true);
    }

    // =========================
    // GET AMOUNT FROM EXPENSE
    // =========================

    static double getAmountFromExpense(
            String expense) {

        try {

            String[] parts =
                    expense.split(" \\| ");

            // Old format:
            // Rs.200.0 | Food | Lunch

            if (parts[0].startsWith("Rs.")) {

                return Double.parseDouble(
                        parts[0].substring(3)
                );
            }

            // New format:
            // date | Rs.200.0 | Food | Lunch

            return Double.parseDouble(
                    parts[1].substring(3)
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // =========================
    // SAVE EXPENSES
    // =========================

    static void saveExpenses(
            DefaultListModel<String> expenseModel) {

        try {

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter(FILE_NAME)
                    );

            for (int i = 0;
                 i < expenseModel.size();
                 i++) {

                writer.write(
                        expenseModel.getElementAt(i)
                );

                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "Error saving expenses."
            );
        }
    }
}