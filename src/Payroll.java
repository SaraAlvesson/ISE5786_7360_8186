/**
 * מחלקה לבדיקת תקינות המערכת והדגמת פולימורפיזם.
 * המחלקה יוצרת עובדים מסוגים שונים, מחשבת את שכרם ומציגה את נתוניהם.
 * @author Sarah Alvesson 328958186
 */
public class Payroll {

    /**
     * נקודת הכניסה לתוכנית.
     * מבצעת אתחול מערך עובדים, עדכון בונוס והדפסת נתונים.
     * @param args ארגומנטים של שורת הפקודה (לא בשימוש)
     */
    public static void main(String[] args) {

        // יצירת מערך בגודל 3 מטיפוס Employee
        Employee[] employees = new Employee[3];

        // אתחול אינדקס 0: HourlyEmployee
        // נתונים: דני כהן, ת"ז 1234, 40 שעות שבועיות, 55.50 לשעה
        employees[0] = new HourlyEmployee("Dani", "Cohen", 1234, 40, 55.50);

        // אתחול אינדקס 1: CommissionEmployee
        // נתונים: שרה לוי, ת"ז 5678, מכירות 100,000.0, עמלה 5%
        employees[1] = new CommissionEmployee("Sara", "Levi", 5678, 100000.0, 5);

        // אתחול אינדקס 2: BasePlusCommissionEmployee
        // נתונים: יוסי ישראלי, ת"ז 9012, מכירות 50,000.0, עמלה 3%, שכר בסיס 6,000.0
        employees[2] = new BasePlusCommissionEmployee("Yossi", "Israeli", 9012, 50000.0, 3, 6000.0);

        System.out.println("--- Payroll Processing ---");
        System.out.println();

        // לולאה העוברת על כל העובדים במערך ומדגימה פולימורפיזם
        for (Employee currentEmployee : employees) {

            // אם העובד הוא מטיפוס BasePlusCommissionEmployee, יש להוסיף 10% לשכר הבסיס
            if (currentEmployee instanceof BasePlusCommissionEmployee) {
                BasePlusCommissionEmployee bpce = (BasePlusCommissionEmployee) currentEmployee;
                double currentBase = bpce.getBaseSalary();
                bpce.setBaseSalary(currentBase * 1.10); // הוספת בונוס של 10%
                System.out.println("Note: Base salary increased by 10% for this employee.");
            }

            // הדפסת פרטי עובד (קריאה מרומזת ל-toString)
            System.out.println(currentEmployee);

            // הדפסת המשכורת בפורמט כספי עם 2 ספרות אחרי הנקודה (שימוש ב-String.format לפי ההנחיה)
            double totalEarnings = currentEmployee.earnings();
            System.out.println("Total Earnings: " + String.format("%.2f", totalEarnings));

            System.out.println("--------------------------------------------------");
        }
    }
}