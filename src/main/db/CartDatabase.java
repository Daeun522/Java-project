package main.db;

import java.util.ArrayList;
import java.util.List;
import main.model.Product;

public class CartDatabase {
    // 1. 딱 하나만 존재하는 인스턴스 (싱글톤)
    private static CartDatabase instance = new CartDatabase();
    private List<Product> cartList = new ArrayList<>();

    // 외부에서 new CartDatabase()를 못 하게 막음
    private CartDatabase() {}

    public static CartDatabase getInstance() {
        return instance;
    }

    public void addProduct(Product p) {
        cartList.add(p);
        System.out.println("장바구니 추가됨: " + p.getName()); // 콘솔에 찍히는지 확인!
    }

    public List<Product> getCartList() {
        return cartList;
    }
    
    public void removeProduct(Product p) {
        cartList.remove(p);
    }
    
    public void clearCart() {
        cartList.clear();
    }
}