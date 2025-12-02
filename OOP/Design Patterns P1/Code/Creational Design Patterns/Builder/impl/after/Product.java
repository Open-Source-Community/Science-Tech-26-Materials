package designpattern.creational.builder.impl.after;

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

    /// Enforcing construct with builder
    private Product(Builder builder){ // Private Constructor
        this.partA = builder.partA;
        this.partB = builder.partB;
        this.partC = builder.partC;
        this.partD = builder.partD;
        this.partE = builder.partE;
        this.partF = builder.partF;
        this.partG = builder.partG;
        this.partH = builder.partH;
        this.partI = builder.partI;
    }

    public static Builder builder(){
        return new Builder();
    }


    public static class Builder{
        private String partA;
        private String partB;
        private String partC;
        private String partD;
        private String partE;
        private String partF;
        private String partG;
        private String partH; // Optional
        private String partI; // Optional

        public Builder setPartA(String partA){
            this.partA = partA;
            return this;
        }

        public Builder setPartB(String partB){
            this.partB = partB;
            return this;
        }

        public Builder setPartC(String partC){
            this.partC = partC;
            return this;
        }

        public Builder setPartD(String partD){
            this.partD = partD;
            return this;
        }

        public Builder setPartE(String partE){
            this.partE = partE;
            return this;
        }

        public Builder setPartF(String partF){
            this.partF = partF;
            return this;
        }

        public Builder setPartG(String partG){
            this.partG = partG;
            return this;
        }

        public Builder setPartH(String partH){
            this.partH = partH;
            return this;
        }

        public Builder setPartI(String partI){
            this.partI = partI;
            return this;
        }

        public Product build(){ // Make the atomic object creation
            return new Product(this);
        }
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
        Product product = Product.builder()
                .setPartB("B") /// More Readable
                .setPartA("A") /// Constructing in any order
                .setPartC("C")
                .setPartE("E")
                .setPartD("D")
                .build();
        System.out.println(product);
    }
}
