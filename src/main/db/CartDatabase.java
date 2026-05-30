package main.db;

import main.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartDatabase {
    // 싱글톤 패턴: 앱 전체에서 단 하나의 인스턴스만 유지하여 데이터 공유
    private static CartDatabase instance = new CartDatabase();
    private List<Product> cartList = new ArrayList<>();

    private CartDatabase() {}

    public static CartDatabase getInstance() {
        return instance;
    }

    public void addProduct(Product p) {
        cartList.add(p);
    }

    public void removeProduct(Product p) {
        cartList.remove(p);
    }

    public List<Product> getCartList() {
        return cartList;
    }

    public void clearCart() {
        cartList.clear();
    }
}