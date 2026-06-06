package main.model;

public class User {
    private String id;
    private String password;
    private String phone;
    private String address;
    
    // [추가] VIP 시스템용 변수
    private int totalSpent = 0;
    private boolean isVip = false;

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
    
    // [추가] 누적 금액 및 VIP 관리 메서드
    public int getTotalSpent() { return totalSpent; }
    public void addSpent(int amount) { this.totalSpent += amount; }
    public boolean isVip() { return isVip; }
    public void setVip(boolean vip) { this.isVip = vip; }
}