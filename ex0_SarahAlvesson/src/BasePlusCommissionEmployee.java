import java.util.Objects;

/**
 * מחלקה המייצגת עובד המקבל שכר בסיס בנוסף לעמלת מכירות.
 * יורשת ממחלקת CommissionEmployee.
 * @author Sarah Alvesson 328958186
 */
public class BasePlusCommissionEmployee extends CommissionEmployee {

    /** שכר הבסיס של העובד */
    private double baseSalary;

    /**
     * @param firstName שם פרטי
     * @param lastName שם משפחה
     * @param id תעודת זהות
     * @param grossSales סך מכירות
     * @param commission אחוז עמלה
     * @param baseSalary שכר בסיס
     * @throws IllegalArgumentException אם שכר הבסיס שלילי
     */
    public BasePlusCommissionEmployee(String firstName, String lastName, int id,
                                      double grossSales, int commission, double baseSalary) {
        super(firstName, lastName, id, grossSales, commission);
        setBaseSalary(baseSalary);
    }

    /**
     * בנאי ברירת מחדל המאתחל לערכים "Plony", "Almony", 0, 0, 0, 0.
     */
    public BasePlusCommissionEmployee() {
        this("Plony", "Almony", 0, 0, 0, 0);
    }

    /**
     * @return שכר הבסיס של העובד
     */
    public double getBaseSalary() {
        return baseSalary;
    }

    /**
     * @param baseSalary שכר בסיס לעדכון
     * @throws IllegalArgumentException אם שכר הבסיס שלילי
     */
    public void setBaseSalary(double baseSalary) {
        if (baseSalary < 0) {
            throw new IllegalArgumentException("Base salary must be non-negative");
        }
        this.baseSalary = baseSalary;
    }

    /**
     * חישוב שכר: שכר בסיס + חישוב העמלה ממחלקת האב.
     * @return סך השכר המחושב
     */
    @Override
    public double earnings() {
        return getBaseSalary() + super.earnings();
    }

    /**
     * @return ייצוג טקסטואלי הכולל את נתוני האב ופורמט כספי לשכר הבסיס
     */
    @Override
    public String toString() {
        return String.format("%s, Base Salary: %s",
                super.toString(),
                String.format("%.2f", baseSalary));
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

        BasePlusCommissionEmployee other = (BasePlusCommissionEmployee) obj;
        return Double.compare(baseSalary, other.baseSalary) == 0;
    }

    /**
     * @return קוד גיבוב המשלב את נתוני האב ושכר הבסיס
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseSalary);
    }
}