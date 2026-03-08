import java.util.Objects;

/**
 * Represents an employee who is paid based on an hourly wage.
 * This class extends the Employee class and implements specific
 * earnings calculation and string representation for hourly workers.
 */
public class HourlyEmployee extends Employee
{
    //fields

    /*** The number of hours worked per week.*/
    private int hours;

    /*** The hourly wage for the employee.*/
    private double wage;


//constructors

    /**
     * Default constructor that initializes the employee with default values:
     * Uses super to initialize base class fields and setters for local fields.
     */
    public HourlyEmployee()
    {
        super("Plony", "Almony", 0);
        setHours(0);
        setWage(0);
    }

    /**
     * parameterize constructor
     * @param firstName- employee's first name
     * @param lastName- employee's last name
     * @param id- employee's id
     * @param hours- number of hours worked
     * @param wage- wage per hour
     */
    public HourlyEmployee(String firstName, String lastName, int id, int hours, double wage)
    {
        super(firstName, lastName, id);
        setHours(hours);
        setWage(wage);
    }


    //setters & getters

    /**
     * Gets the number of hours worked by the employee.
     * @return the total hours worked as an integer.
     */
    public int getHours() {
        return hours;
    }

    /**
     * Sets the number of hours worked by the employee.
     * @param hours- the total hours worked.
     * @throws IllegalArgumentException if hours is negative.
     */
    public void setHours(int hours)
    {
        if (hours < 0)
            throw new IllegalArgumentException("Hours worked cannot be negative");
        this.hours = hours;
    }

    /**
     * Gets the hourly wage of the employee.
     * @return the wage per hour as a double.
     */
    public double getWage()
    {
        return wage;
    }

    /**
     * Sets the hourly wage of the employee.
     * @param wage the hourly wage to set.
     * @throws IllegalArgumentException if the wage is negative.
     */
    public void setWage(double wage)
    {
        if (wage < 0)
            throw new IllegalArgumentException("Wage per hour cannot be negative");
        this.wage = wage;
    }


    //methods

    /**
     * Calculates earnings based on hours worked multiplied by hourly wage.
     * @return the total earnings as a double.
     */
    @Override
    public double earnings()
    {
        return hours * wage;
    }

    /**
     * Compares this hourly employee to the specified object.
     * @param obj the object to compare this employee against.
     * @return true if the object is an HourlyEmployee with the same basic details (name, ID)
     * and the same hours and wage; false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        HourlyEmployee other = (HourlyEmployee) obj;
        return hours == other.hours &&
                Double.compare(wage, other.wage) == 0;
    }

    /**
     * Returns a hash code value for the hourly employee.
     * @return a hash code value based on the employee's basic details, hours, and wage.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), hours, wage);
    }

    /**
     * Returns a string representation of the hourly employee.
     * Includes basic details from Employee and specific hourly data.
     * @return a formatted string with name, ID, hours, and wage.
     */
    @Override
    public String toString()
    {
        return String.format("%s, hours: %d, wage: %.2f",
                super.toString(), hours, wage);
    }
}




