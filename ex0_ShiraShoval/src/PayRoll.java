/**
 * Testing class for the employee project.
 * This class runs the payroll simulation using an array.
 */
public class PayRoll
{

    public static void main(String[] args)
    {

        // Create an array to hold all types of employees
        Employee[] empArr = new Employee[3];

        //  Initialize with some real test data
        empArr[0] = new HourlyEmployee("Shira", "Shon", 20230011, 45, 55.0);
        empArr[1] = new CommissionEmployee("Sarah", "Cohen", 20230022, 60000, 12.5);
        empArr[2] = new BasePlusCommissionEmployee("Mika", "Wheeler", 20230033, 40000, 8.0, 4500.0f);

        System.out.println("Monthly Payroll Summary:");
        System.out.println("-------------------------");

        //  Loop through the array using a standard index
        for (int i = 0; i < empArr.length; i++)
        {
            // Get current worker
            Employee worker = empArr[i];

            // Print worker details - calling toString implicitly
            System.out.println("Employee " + (i+1) + ": " + worker);

            // Calculate money to pay
            double money = worker.earnings();

            // Check if we need to add a 10% bonus for base+commission
            if (worker instanceof BasePlusCommissionEmployee)
            {
                double bonusAmount = money * 0.10;
                money += bonusAmount;
                System.out.printf("   > Added 10%% bonus: %.2f%n", bonusAmount);
            }

            // Print final result with two decimal digits
            System.out.printf("   Total Pay: %.2f%n", money);
            System.out.println();
        }
    }
}