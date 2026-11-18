import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExpenseManager manager = new ExpenseManager();

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("       JAVA EXPENSE TRACKER");
        System.out.println("================================");

        boolean running = true;
        while (running) {
            showMenu();
            switch (readInt("Choose an option: ")) {
                case 1 -> addExpense();
                case 2 -> listExpenses();
                case 3 -> showSummary();
                case 4 -> deleteExpense();
                case 5 -> running = false;
                default -> System.out.println("Please choose an option from 1 to 5.");
            }
        }
        System.out.println("Thank you for using Java Expense Tracker!");
        scanner.close();
    }

    private static void showMenu() {
        System.out.println("\\n1. Add expense");
        System.out.println("2. View all expenses");
        System.out.println("3. View spending summary");
        System.out.println("4. Delete an expense");
        System.out.println("5. Exit");
    }

    private static void addExpense() {
        try {
            System.out.print("Expense title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Category (Food/Travel/Bills/Shopping/Other): ");
            String category = scanner.nextLine().trim();
            double amount = readDouble("Amount (INR): ");
            System.out.print("Date (YYYY-MM-DD, blank for today): ");
            String dateText = scanner.nextLine().trim();
            LocalDate date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText);

            Expense expense = manager.add(title, category, amount, date);
            System.out.println("Expense added successfully. ID: " + expense.getId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Could not add expense: " + ex.getMessage());
        }
    }

    private static void listExpenses() {
        List<Expense> expenses = manager.getAll();
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.printf("%-5s %-22s %-15s %-12s %-12s%n",
                "ID", "TITLE", "CATEGORY", "AMOUNT", "DATE");
        System.out.println("---------------------------------------------------------------------");
        for (Expense e : expenses) {
            System.out.printf("%-5d %-22s %-15s ₹%-11.2f %-12s%n",
                    e.getId(), shorten(e.getTitle(), 21), shorten(e.getCategory(), 14),
                    e.getAmount(), e.getDate());
        }
    }

    private static void showSummary() {
        YearMonth currentMonth = YearMonth.now();
        System.out.printf("%nAll-time spending: ₹%.2f%n", manager.getTotal());
        System.out.printf("Spending this month (%s): ₹%.2f%n",
                currentMonth, manager.getMonthlyTotal(currentMonth));

        System.out.println("\\nCategory breakdown:");
        Map<String, Double> totals = manager.getCategoryTotals();
        if (totals.isEmpty()) {
            System.out.println("No data available.");
        } else {
            totals.forEach((category, amount) ->
                    System.out.printf("- %-15s ₹%.2f%n", category, amount));
        }
    }

    private static void deleteExpense() {
        int id = readInt("Enter expense ID to delete: ");
        if (manager.delete(id)) {
            System.out.println("Expense deleted.");
        } else {
            System.out.println("No expense found with that ID.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Amount must be greater than zero.");
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid amount.");
            }
        }
    }

    private static String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "…";
    }
}
