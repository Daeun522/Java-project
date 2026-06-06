package main.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.model.Product;

public class PurchaseDatabase {
    private static PurchaseDatabase instance = new PurchaseDatabase();
    
    // 유저 ID를 키(Key)로, 구매한 상품 리스트를 값(Value)으로 저장
    private Map<String, List<Product>> purchaseMap = new HashMap<>();

    private PurchaseDatabase() {}

    public static PurchaseDatabase getInstance() {
        return instance;
    }

    // 특정 유저의 구매 내역 추가
    public void addPurchase(String userId, List<Product> products) {
        purchaseMap.putIfAbsent(userId, new ArrayList<>());
        purchaseMap.get(userId).addAll(products);
    }

    // 특정 유저의 전체 구매 내역 반환
    public List<Product> getPurchaseHistory(String userId) {
        return purchaseMap.getOrDefault(userId, new ArrayList<>());
    }
}