import java.util.Objects;

/**
 * Abstract class representing a general employee.
 * This class serves as the base for all employee types in the system.
 */
public abstract class Employee
{
    //fields

    /*** employee's first name*/
    private String firstName;

    /*** employee's last name*/
    private String lastName;

    /*** employee's id*/
    private int id;


    //constructors

    /**
     * default constructor
     */
    public Employee()
    {
        setFirstName("Plony");
        setLastName("Almony");
        setId(0);
    }

    /**
     * parameterized constructor
     * @param firstName- employee's first name
     * @param lastName- employee's last name
     * @param id- employee's id
     */
    public Employee(String firstName, String lastName, int id)
    {
        setFirstName(firstName);
        setLastName(lastName);
        setId(id);
    }


    //setters & getters

    /**
     * @return the employee's first name
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * sets the employee's first name only if the value is not null\empty
     * @param firstName- employee's first name
     */
    public void setFirstName(String firstName)
    {
        if (firstName == null || firstName.trim().isEmpty())
            throw new IllegalArgumentException("first name should have a real value");
        this.firstName = firstName;
    }

    /**
     * @return the employee's last name
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * sets the employee's last name only if the value is not null\empty
     * @param lastName- employee's last name
     */
    public void setLastName(String lastName)
    {
        if (lastName == null || lastName.trim().isEmpty())
            throw new IllegalArgumentException("last name should have a real value");
        this.lastName = lastName;
    }

    /**
     * @return the employee's id
     */
    public int getId()
    {
        return id;
    }

    /**
     * sets the employee's id only if the value is not negative
     * @param id- employee's id
     */
    public void setId(int id)
    {
        if (id < 0)
            throw new IllegalArgumentException("ID cannot be negative");
        this.id = id;
    }


    //methods

    /**
     * Calculates the employee's earnings.
     * @return the total earnings of the employee as a double.
     */
    public abstract double earnings();

    /**
     * Compares this employee to the specified object.
     * @param obj- the object to compare this employee against.
     * @return true if the given object represents an Employee equivalent
     * to this employee (same name and ID), false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        return id == other.id &&
                Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName);
    }

    /**
     * Returns a hash code value for the employee.
     * @return a hash code value based on first name, last name, and ID.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(firstName, lastName, id);
    }

    /**
     * Returns a string representation of the employee's details.
     * @return a formatted string including name and ID.
     */
    @Override
    public String toString()
    {
        return String.format("name: %s %s, id: %d", firstName, lastName, id);
    }



}
