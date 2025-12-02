package designpattern.creational.builder.impl.before;

public class Product {
    /// To Simulate attributes
    private String partA;
    private String partB;
    private String partC;
    private String partD;
    private String partE;
    private String partF;
    private String partG;
    private String partH; // Optional
    private String partI; // Optional

    public Product() {}
    
    // a lot of constructors ...
    
    public Product(String partA, String partB, String partC, String partD, String partE, String partF, String partG, String partH, String partI) {
        this.partA = partA;
        this.partB = partB;
        this.partC = partC;
        this.partD = partD;
        this.partE = partE;
        this.partF = partF;
        this.partG = partG;
        this.partH = partH;
        this.partI = partI;
    }


    @Override
    public String toString() {
        return "Product{" +
                "partA='" + partA + '\'' +
                ", partB='" + partB + '\'' +
                ", partC='" + partC + '\'' +
                ", partD='" + partD + '\'' +
                ", partE='" + partE + '\'' +
                ", partF='" + partF + '\'' +
                ", partG='" + partG + '\'' +
                ", partH='" + partH + '\'' +
                ", partI='" + partI + '\'' +
                '}';
    }

    public String getPartA() {
        return partA;
    }

    public void setPartA(String partA) {
        this.partA = partA;
    }

    public String getPartB() {
        return partB;
    }

    public void setPartB(String partB) {
        this.partB = partB;
    }

    public String getPartC() {
        return partC;
    }

    public void setPartC(String partC) {
        this.partC = partC;
    }

    public String getPartD() {
        return partD;
    }

    public void setPartD(String partD) {
        this.partD = partD;
    }

    public String getPartE() {
        return partE;
    }

    public void setPartE(String partE) {
        this.partE = partE;
    }

    public String getPartF() {
        return partF;
    }

    public void setPartF(String partF) {
        this.partF = partF;
    }

    public String getPartG() {
        return partG;
    }

    public void setPartG(String partG) {
        this.partG = partG;
    }

    public String getPartH() {
        return partH;
    }

    public void setPartH(String partH) {
        this.partH = partH;
    }

    public String getPartI() {
        return partI;
    }

    public void setPartI(String partI) {
        this.partI = partI;
    }
}


class ProductTest{
    public static void main(String[] args) {
        /// Creating Product with all parts
        Product product1 = new Product("A", "B", "C", "D", "E", "F", "G", "H", "I");

        /// Creating Product without optional parts
        Product product2 = new Product("A", "B", "C", "D", "E", "F", "G", null, null);

        /// Creating Product with default constructor and change his state -> what about immutability?
        Product product3 = new Product();
        product3.setPartA("A");
        product3.setPartB("B");

        System.out.println( product1);
        System.out.println( product2);
        System.out.println( product3);
    }
}
