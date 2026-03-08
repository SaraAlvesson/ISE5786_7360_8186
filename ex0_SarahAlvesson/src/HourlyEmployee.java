import java.util.Objects;

/**
 * מחלקה המייצגת עובד המשתכר לפי שעות עבודה.
 * יורשת ממחלקת Employee.
 * @author Sarah Alvesson 328958186
 */
public class HourlyEmployee extends Employee {

    /** מספר שעות עבודה שבועיות */
    private int hours;
    /** שכר לשעה */
    private double wage;

    /**
     * @param firstName שם פרטי
     * @param lastName שם משפחה
     * @param id תעודת זהות
     * @param hours שעות עבודה שבועיות
     * @param wage שכר לשעה
     * @throws IllegalArgumentException אם השעות או השכר שליליים
     */
    public HourlyEmployee(String firstName, String lastName, int id, int hours, double wage) {
        super(firstName, lastName, id);
        setHours(hours);
        setWage(wage);
    }

    /**
     * בנאי ברירת מחדל המאתחל לערכים "Plony", "Almony", 0, 0, 0.
     */
    public HourlyEmployee() {
        this("Plony", "Almony", 0, 0, 0);
    }

    /**
     * @return מספר שעות עבודה שבועיות
     */
    public int getHours() {
        return hours;
    }

    /**
     * @param hours מספר שעות עבודה לעדכון
     * @throws IllegalArgumentException אם השעות שליליות
     */
    public void setHours(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        this.hours = hours;
    }

    /**
     * @return שכר לשעה
     */
    public double getWage() {
        return wage;
    }

    /**
     * @param wage שכר לשעה לעדכון
     * @throws IllegalArgumentException אם השכר שלילי
     */
    public void setWage(double wage) {
        if (wage < 0) {
            throw new IllegalArgumentException("Wage cannot be negative");
        }
        this.wage = wage;
    }

    /**
     * חישוב שכר: שעות עבודה כפול שכר לשעה.
     * @return סך השכר המחושב
     */
    @Override
    public double earnings() {
        return getHours() * getWage();
    }

    /**
     * @return ייצוג טקסטואלי של העובד כולל נתוני אב ופורמט כספי לשכר השעתי
     */
    @Override
    public String toString() {
        return String.format("%s, Hours: %d, Wage: %s",
                super.toString(),
                hours,
                String.format("%.2f", wage));
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

        HourlyEmployee other = (HourlyEmployee) obj;
        return hours == other.hours &&
                Double.compare(wage, other.wage) == 0;
    }

    /**
     * @return קוד גיבוב המשלב את נתוני מחלקת האב והשדות הנוכחיים
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hours, wage);
    }
}