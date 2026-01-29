// Non-pattern version: Students need to refactor this into Template Method Pattern
class TeaMaker {
    public void makeTea() {
        System.out.println("Boiling water");
        System.out.println("Steeping the tea");
        System.out.println("Pouring into cup");
        System.out.println("Adding lemon");
    }
}

class CoffeeMaker {
    public void makeCoffee() {
        System.out.println("Boiling water");
        System.out.println("Dripping coffee through filter");
        System.out.println("Pouring into cup");
        System.out.println("Adding sugar and milk");
    }
}

class HotChocolateMaker {
    public void makeHotChocolate() {
        System.out.println("Boiling water");
        System.out.println("Dissolving chocolate powder");
        System.out.println("Pouring into cup");
        System.out.println("Adding marshmallows");
    }
}

// Problem: Notice the duplication and similar process across different beverage makers
// Students should:
// 1. Identify the common template in the preparation process
// 2. Create an abstract class with the template method
// 3. Extract common methods
// 4. Make specific steps abstract
// 5. Refactor the concrete classes

public class BeverageShop {
    public static void main(String[] args) {
        System.out.println("=== Non-Pattern Version ===");
        
        TeaMaker teaMaker = new TeaMaker();
        System.out.println("\nMaking tea:");
        teaMaker.makeTea();
        
        CoffeeMaker coffeeMaker = new CoffeeMaker();
        System.out.println("\nMaking coffee:");
        coffeeMaker.makeCoffee();
        
        HotChocolateMaker hotChocolateMaker = new HotChocolateMaker();
        System.out.println("\nMaking hot chocolate:");
        hotChocolateMaker.makeHotChocolate();
        
        // TODO: Should refactor to use Template Method Pattern
        // Expected refactored output should look like:
        /*
        Making tea:
        Boiling water
        Steeping the tea
        Pouring into cup
        Adding lemon
        
        Making coffee:
        Boiling water
        Dripping coffee through filter
        Pouring into cup
        Adding sugar and milk
        
        Making hot chocolate:
        Boiling water
        Dissolving chocolate powder
        Pouring into cup
        Adding marshmallows
        */
    }
}