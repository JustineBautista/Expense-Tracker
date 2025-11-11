import java.util.Date;
import java.text.SimpleDateFormat;

public class Expense {
    private double amount;
    private String category;
    private String description;
    private Date date;

    public Expense(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = new Date();
    }

    public Expense(double amount, String category, String description, Date date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(Date date) { this.date = date; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    @Override
    public String toString() {
        return String.format("%.2f,%s,%s,%d", amount, category, description, date.getTime());
    }

    public static Expense fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 4) return null;
        try {
            double amount = Double.parseDouble(parts[0]);
            String category = parts[1];
            String description = parts[2];
            Date date = new Date(Long.parseLong(parts[3]));
            return new Expense(amount, category, description, date);
        } catch (Exception e) {
            return null;
        }
    }
}
