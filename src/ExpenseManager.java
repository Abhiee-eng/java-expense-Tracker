import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private static final Path DATA_FILE = Paths.get("data", "expenses.csv");
    private final List<Expense> expenses = new ArrayList<>();
    private int nextId = 1;

    public ExpenseManager() {
        load();
    }

    public Expense add(String title, String category, double amount, LocalDate date) {
        Expense expense = new Expense(nextId++, title, category, amount, date);
        expenses.add(expense);
        save();
        return expense;
    }

    public boolean delete(int id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) save();
        return removed;
    }

    public List<Expense> getAll() {
        return expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    public double getTotal() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getMonthlyTotal(YearMonth month) {
        return expenses.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(month))
                .mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getCategoryTotals() {
        Map<String, Double> totals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Expense e : expenses) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return totals;
    }

    private void load() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            List<String> lines = Files.readAllLines(DATA_FILE);
            for (String line : lines) {
                if (line.isBlank() || line.startsWith("id,")) continue;
                try {
                    Expense expense = Expense.fromCsv(line);
                    expenses.add(expense);
                    nextId = Math.max(nextId, expense.getId() + 1);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Skipping damaged record: " + line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not load saved expenses: " + ex.getMessage());
        }
    }

    private void save() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("id,title,category,amount,date");
            for (Expense e : expenses) lines.add(e.toCsv());
            Files.write(DATA_FILE, lines);
        } catch (IOException ex) {
            System.err.println("Could not save expenses: " + ex.getMessage());
        }
    }
}
