import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JavaExpenseTracker {

    static final String FILE_NAME = "expenses.txt";

    public static void main(String[] args) {

        JFrame frame = new JFrame("Java Expense Tracker");

        frame.setSize(750, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel title = new JLabel("JAVA EXPENSE TRACKER");

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(10);

        JLabel categoryLabel = new JLabel("Category:");

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
                new JTextField(15);

        JButton addButton =
                new JButton("Add Expense");

        JButton deleteButton =
                new JButton("Delete Expense");

        JLabel totalLabel =
                new JLabel("Total Expense: Rs.0.0");

        DefaultListModel<String> expenseModel =
                new DefaultListModel<>();

        JList<String> expenseList =
                new JList<>(expenseModel);

        JScrollPane scrollPane =
                new JScrollPane(expenseList);

        scrollPane.setPreferredSize(
                new Dimension(650, 300)
        );

        final double[] totalExpense = {0};

        // DATE AND TIME FORMAT
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy HH:mm"
                );

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

                totalExpense[0] =
                        totalExpense[0] + amount;
            }

            reader.close();

        } catch (IOException e) {

            // File does not exist yet.
            // It will be created when the first
            // expense is added.
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
                        (String) categoryBox
                                .getSelectedItem();

                String description =
                        descriptionField.getText();

                if (description.isEmpty()) {

                    description =
                            "No description";
                }

                // GET CURRENT DATE AND TIME
                String dateTime =
                        LocalDateTime.now()
                                .format(formatter);

                // CREATE EXPENSE
                String expense =
                        dateTime
                        + " | Rs." + amount
                        + " | " + category
                        + " | " + description;

                // ADD TO LIST
                expenseModel.addElement(expense);

                // UPDATE TOTAL
                totalExpense[0] =
                        totalExpense[0] + amount;

                totalLabel.setText(
                        "Total Expense: Rs."
                        + totalExpense[0]
                );

                // CLEAR INPUT FIELDS
                amountField.setText("");
                descriptionField.setText("");

                // SAVE TO FILE
                saveExpenses(expenseModel);

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

                // UPDATE TOTAL
                totalExpense[0] =
                        totalExpense[0] - amount;

                // REMOVE EXPENSE
                expenseModel.remove(
                        selectedIndex
                );

                // UPDATE TOTAL LABEL
                totalLabel.setText(
                        "Total Expense: Rs."
                        + totalExpense[0]
                );

                // SAVE UPDATED LIST
                saveExpenses(expenseModel);

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an expense to delete!"
                );
            }
        });

        // =========================
        // ADD COMPONENTS TO FRAME
        // =========================

        frame.add(title);

        frame.add(amountLabel);
        frame.add(amountField);

        frame.add(categoryLabel);
        frame.add(categoryBox);

        frame.add(descriptionLabel);
        frame.add(descriptionField);

        frame.add(addButton);
        frame.add(deleteButton);

        frame.add(totalLabel);

        frame.add(scrollPane);

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

            /*
             * Old format:
             * Rs.200.0 | Food | Lunch
             *
             * New format:
             * 27-08-2026 14:30 | Rs.200.0 | Food | Lunch
             */

            if (parts[0].startsWith("Rs.")) {

                // OLD FORMAT
                return Double.parseDouble(
                        parts[0].substring(3)
                );

            } else {

                // NEW FORMAT
                return Double.parseDouble(
                        parts[1].substring(3)
                );
            }

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