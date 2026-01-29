package template;

// =====================
// Template (Abstract Class)
// =====================
 abstract class BeverageShop {

    // Template Method (fixed algorithm)
    public final void makeBeverage() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
    }

    protected void boilWater() {
        System.out.println("Boiling water");
    }

    protected void pourInCup() {
        System.out.println("Pouring into cup");
    }

    protected abstract void brew();
    protected abstract void addCondiments();

    // =====================
    // Test Main
    // =====================
    public static void main(String[] args) {

        BeverageShop tea = new TeaMaker();
        System.out.println("\nMaking tea:");
        tea.makeBeverage();

        BeverageShop coffee = new CoffeeMaker();
        System.out.println("\nMaking coffee:");
        coffee.makeBeverage();

        BeverageShop chocolate = new HotChocolateMaker();
        System.out.println("\nMaking hot chocolate:");
        chocolate.makeBeverage();
    }
}

// =====================
// Concrete Classes
// =====================
class TeaMaker extends BeverageShop {

    @Override
    protected void brew() {
        System.out.println("Steeping the tea");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding lemon");
    }
}

class CoffeeMaker extends BeverageShop {

    @Override
    protected void brew() {
        System.out.println("Dripping coffee through filter");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding sugar and milk");
    }
}

class HotChocolateMaker extends BeverageShop {

    @Override
    protected void brew() {
        System.out.println("Dissolving chocolate powder");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding marshmallows");
    }
}
