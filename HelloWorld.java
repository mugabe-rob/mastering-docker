public class HelloWorld {
    public static void main(String[] args) {
        // Simple greeting
        System.out.println("Hello, World!");
        System.out.println("Welcome to Java programming!");
        
        // Basic variables and operations
        String name = "Robert MUGABE";
        int age = 23;
        double salary = 75000.50;
        boolean isEmployed = true;
        
        // Display information
        System.out.println("\n--- Personal Information ---");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Salary: $" + salary);
        System.out.println("Employed: " + isEmployed);
        
        // Simple calculations
        System.out.println("\n--- Calculations ---");
        int num1 = 10;
        int num2 = 5;
        System.out.println(num1 + " + " + num2 + " = " + (num1 + num2));
        System.out.println(num1 + " - " + num2 + " = " + (num1 - num2));
        System.out.println(num1 + " * " + num2 + " = " + (num1 * num2));
        System.out.println(num1 + " / " + num2 + " = " + (num1 / num2));
        
        // Simple loop
        System.out.println("\n--- Counting from 1 to 5 ---");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Count: " + i);
        }
        
        // Conditional statement
        System.out.println("\n--- Age Check ---");
        if (age >= 18) {
            System.out.println("You are an adult!");
        } else {
            System.out.println("You are a minor.");
        }
        
        System.out.println("\nProgram completed successfully!");
    }
}
