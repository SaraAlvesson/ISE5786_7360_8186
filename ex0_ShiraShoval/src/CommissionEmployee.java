import java.util.Objects;

/**
 * Represents an employee who earns a commission based on a percentage of gross sales.
 * This class follows the formula: (grossSales * commission) / 100.
 */
public class CommissionEmployee extends Employee
{
    //fields

    /** Total sales made by the employee. */
    private double grossSales;

    /** The commission percentage (e.g., 10 for 10%). */
    private double commission;


    //constructors

    /**
     * Default constructor.
     * Initializes with: Name "Plony Almony", ID 0, Sales 0, Commission 0.
     */
    public CommissionEmployee() {
        super("Plony", "Almony", 0);
        setGrossSales(0);
        setCommission(0);
    }

    /**
     * Parameterized constructor.
     * @param firstName employee's first name.
     * @param lastName employee's last name.
     * @param id employee's ID.
     * @param grossSales total sales.
     * @param commission commission percentage.
     */
    public CommissionEmployee(String firstName, String lastName, int id, double grossSales, double commission) {
        super(firstName, lastName, id);
        setGrossSales(grossSales);
        setCommission(commission);
    }


    // Getters & Setters

    /** @return the gross sales. */
    public double getGrossSales() {
        return grossSales;
    }

    /**
     * Sets gross sales. Must be non-negative.
     * @param grossSales sales amount.
     * @throws IllegalArgumentException if sales are negative.
     */
    public void setGrossSales(double grossSales)
    {
        if (grossSales < 0)
            throw new IllegalArgumentException("Gross sales cannot be negative");
        this.grossSales = grossSales;
    }

    /** @return the commission percentage. */
    public double getCommission()
    {
        return commission;
    }

    /**
     * Sets the commission percentage. Must be positive (e.g., 5 for 5%).
     * @param commission percentage value.
     * @throws IllegalArgumentException if commission is negative or exceeds 100.
     */
    public void setCommission(double commission)
    {
        if (commission < 0 || commission > 100)
            throw new IllegalArgumentException("Commission must be between 0 and 100");
        this.commission = commission;
    }


    // Methods

    /**
     * Calculates earnings: (gross sales * commission) / 100.
     * @return total earnings as a double.
     */
    @Override
    public double earnings()
    {
        return (grossSales * commission) / 100;
    }

    /**
     * Returns a string representation. Uses %.2f for the commission and sales (red asterisk).
     * @return formatted string.
     */
    @Override
    public String toString()
    {
        return String.format("%s, gross sales: %.2f, commission: %.2f%%",
                super.toString(), grossSales, commission);
    }

    /**
     * Compares this commission employee to the specified object for equality.
     * @param obj the reference object with which to compare.
     * @return true if the specified object is an instance of CommissionEmployee,
     * and its basic details (from Employee), gross sales, and commission
     * are equal to this employee's; false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        CommissionEmployee other = (CommissionEmployee) obj;
        return Double.compare(grossSales, other.grossSales) == 0 &&
                Double.compare(commission, other.commission) == 0;
    }

    /**
     * Returns a hash code value for the commission employee.
     * @return a hash code value based on the employee's basic details,
     * gross sales, and commission percentage.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), grossSales, commission);
    }
}