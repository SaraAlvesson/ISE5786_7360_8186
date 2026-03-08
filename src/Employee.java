import java.util.Objects;

/**
 * מחלקה אבסטרקטית המייצגת עובד כללי בארגון.
 * @author Sarah Alvesson 328958186
 */
public abstract class Employee {

    /** שם פרטי של העובד */
    private String firstName;
    /** שם משפחה של העובד */
    private String lastName;
    /** מספר תעודת זהות של העובד */
    private int id;

    /**
     * @param firstName שם פרטי
     * @param lastName שם משפחה
     * @param id מספר תעודת זהות
     * @throws IllegalArgumentException אם השמות ריקים או ה-ID שלילי
     */
    public Employee(String firstName, String lastName, int id) {
        setFirstName(firstName);
        setLastName(lastName);
        setId(id);
    }

    /**
     * בנאי ברירת מחדל המאתחל לערכים "Plony", "Almony", 0.
     */
    public Employee() {
        this("Plony", "Almony", 0);
    }

    /**
     * @return שם פרטי
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName שם פרטי לעדכון
     * @throws IllegalArgumentException אם השם null או ריק
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        this.firstName = firstName;
    }

    /**
     * @return שם משפחה
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName שם משפחה לעדכון
     * @throws IllegalArgumentException אם השם null או ריק
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        this.lastName = lastName;
    }

    /**
     * @return מספר זהות
     */
    public int getId() {
        return id;
    }

    /**
     * @param id מספר זהות לעדכון
     * @throws IllegalArgumentException אם ה-ID שלילי
     */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        this.id = id;
    }

    /**
     * @return סכום המשכורת המחושב
     */
    public abstract double earnings();

    /**
     * @return ייצוג טקסטואלי של העובד
     */
    @Override
    public String toString() {
        return String.format("Employee: %s %s, ID: %d", firstName, lastName, id);
    }

    /**
     * @param obj אובייקט להשוואה
     * @return true אם האובייקטים זהים, אחרת false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Employee other = (Employee) obj;

        return id == other.id &&
                Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName);
    }

    /**
     * @return קוד גיבוב של האובייקט
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, id);
    }
}