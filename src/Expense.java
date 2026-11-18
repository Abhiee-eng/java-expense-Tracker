import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Expense {
    private final int id;
    private final String title;
    private final String category;
    private final double amount;
    private final LocalDate date;

    public Expense(int id, String title, String category, double amount, LocalDate date) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category cannot be empty.");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
        this.id = id;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public String toCsv() {
        return id + "," + clean(title) + "," + clean(category) + "," + amount + "," + date;
    }

    private String clean(String value) {
        return value.replace(",", " ").replace("\n", " ").trim();
    }

    public static Expense fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 5) throw new IllegalArgumentException("Invalid expense record.");
        try {
            return new Expense(Integer.parseInt(p[0]), p[1], p[2],
                    Double.parseDouble(p[3]), LocalDate.parse(p[4]));
        } catch (NumberFormatException | DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid expense record.", ex);
        }
    }
}
