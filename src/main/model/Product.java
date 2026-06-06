package main.model;

public class Product {
    private String name;
    private String imageName;
    private int price;
    private int quantity;
    private String description;

    public Product(String name, String imageName, int price, int quantity, String description) {
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    // Getter 메서드
    public String getName() { return name; }
    public String getImageName() { return imageName; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(obj == null || getClass() != obj.getClass())
            return false;

        Product other = (Product)obj;

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}