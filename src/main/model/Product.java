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

    // [추가] 장바구니 담기 시 수량 차감
    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    // [추가] 장바구니에서 뺄 때 수량 원상복구
    public void increaseQuantity() {
        this.quantity++;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product)obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}