import java.util.Objects;

/**
 * Represents an employee who receives a base salary plus commission
 * based on gross sales. This class extends CommissionEmployee.
 */
public class BasePlusCommissionEmployee extends CommissionEmployee
{
    //field

    /** The base salary for the employee. */
    private float baseSalary;


    //Constructors

    /**
     * Default constructor.
     * Initializes with: Name "Plony Almony", ID 0, Sales 0, Commission 0, Base Salary 0.
     */
    public BasePlusCommissionEmployee()
    {
        super("Plony", "Almony", 0, 0, 0);
        setBaseSalary(0);
    }

    /**
     * Parameterized constructor.
     * @param firstName employee's first name.
     * @param lastName employee's last name.
     * @param id employee's ID.
     * @param grossSales total sales.
     * @param commission commission percentage.
     * @param baseSalary base monthly salary.
     */
    public BasePlusCommissionEmployee(String firstName, String lastName, int id,
                                      double grossSales, double commission, float baseSalary)
    {
        super(firstName, lastName, id, grossSales, commission);
        setBaseSalary(baseSalary);
    }


    // Getters & Setters

    /** @return the base salary. */
    public float getBaseSalary()
    {
        return baseSalary;
    }

    /**
     * Sets the base salary. Must be non-negative.
     * @param baseSalary the amount to set.
     * @throws IllegalArgumentException if salary is negative.
     */
    public void setBaseSalary(float baseSalary)
    {
        if (baseSalary < 0)
            throw new IllegalArgumentException("Base salary cannot be negative");
        this.baseSalary = baseSalary;
    }


    //Methods

    /**
     * Calculates total earnings: base salary + commission earnings.
     * Uses super.earnings() to maintain DRY principle.
     * @return total earnings as a double.
     */
    @Override
    public double earnings()
    {
        // DRY: super.earnings() מחשב כבר את ה-(grossSales * commission) / 100
        return baseSalary + super.earnings();
    }

    /**
     * Returns a string representation. Uses %.2f for the base salary (red asterisk).
     * @return formatted string.
     */
    @Override
    public String toString()
    {
        return String.format("%s, base salary: %.2f", super.toString(), baseSalary);
    }

    /**
     * Compares this employee to another for equality.
     * @param obj the reference object.
     * @return true if all fields from parent classes and local baseSalary match.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        BasePlusCommissionEmployee other = (BasePlusCommissionEmployee) obj;
        return Float.compare(baseSalary, other.baseSalary) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), baseSalary);
    }
}