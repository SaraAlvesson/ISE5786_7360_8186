import java.util.Objects;

/**
 * מחלקה המייצגת עובד המשתכר לפי עמלת מכירות.
 * יורשת ממחלקת Employee.
 * @author Sarah Alvesson 328958186
 */
public class CommissionEmployee extends Employee {

    /** סך המכירות ברוטו של העובד */
    private double grossSales;
    /** אחוז עמלת המכירות של העובד */
    private int commission;

    /**
     * @param firstName שם פרטי
     * @param lastName שם משפחה
     * @param id תעודת זהות
     * @param grossSales סך מכירות
     * @param commission אחוז עמלה
     * @throws IllegalArgumentException אם המכירות שליליות או העמלה לא הגיונית
     */
    public CommissionEmployee(String firstName, String lastName, int id, double grossSales, int commission) {
        super(firstName, lastName, id);
        setGrossSales(grossSales);
        setCommission(commission);
    }

    /**
     * בנאי ברירת מחדל המאתחל לערכים "Plony", "Almony", 0, 0, 0.
     */
    public CommissionEmployee() {
        this("Plony", "Almony", 0, 0, 0);
    }

    /**
     * @return סך המכירות ברוטו
     */
    public double getGrossSales() {
        return grossSales;
    }

    /**
     * @param grossSales סך מכירות לעדכון
     * @throws IllegalArgumentException אם המכירות שליליות
     */
    public void setGrossSales(double grossSales) {
        if (grossSales < 0) {
            throw new IllegalArgumentException("Gross sales must be non-negative");
        }
        this.grossSales = grossSales;
    }

    /**
     * @return אחוז עמלת המכירות
     */
    public int getCommission() {
        return commission;
    }

    /**
     * @param commission אחוז עמלה לעדכון
     * @throws IllegalArgumentException אם העמלה לא בטווח 0-100
     */
    public void setCommission(int commission) {
        if (commission < 0 || commission > 100) {
            throw new IllegalArgumentException("Commission must be between 0 and 100");
        }
        this.commission = commission;
    }

    /**
     * חישוב שכר העובד: מכירות כפול עמלה חלקי 100.
     * @return סך השכר המחושב
     */
    @Override
    public double earnings() {
        return (getGrossSales() * getCommission()) / 100.0;
    }

    /**
     * @return ייצוג טקסטואלי הכולל את נתוני האב ופורמט כספי למכירות
     */
    @Override
    public String toString() {
        return String.format("%s, Gross Sales: %s, Commission: %d%%",
                super.toString(),
                String.format("%.2f", grossSales),
                commission);
    }

    /**
     * @param obj אובייקט להשוואה
     * @return true אם האובייקטים זהים, אחרת false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;

        CommissionEmployee other = (CommissionEmployee) obj;
        return commission == other.commission &&
                Double.compare(grossSales, other.grossSales) == 0;
    }

    /**
     * @return קוד גיבוב המשלב את נתוני האב והשדות הנוכחיים
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), grossSales, commission);
    }
}