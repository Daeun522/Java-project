package main.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String password;
    private String phone;
    private String address;
    // [추가] 구매 내역을 저장할 리스트
    private List<Product> purchaseHistory = new ArrayList<>();

    public User(String id, String password, String phone, String address) {
        this.id = id;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    // [추가] 구매 내역 관련 메서드
    public List<Product> getPurchaseHistory() { return purchaseHistory; }
    public void addPurchaseHistory(List<Product> products) {
        this.purchaseHistory.addAll(products);
    }
}