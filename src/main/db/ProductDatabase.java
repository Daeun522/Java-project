package main.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import main.model.Product;

public class ProductDatabase {
    private static ProductDatabase instance = new ProductDatabase();
    private List<Product> productList = new ArrayList<>();

    private ProductDatabase() {
        loadProductsFromFile("products.txt");
    }

    public static ProductDatabase getInstance() {
        return instance;
    }

    public List<Product> getAllProducts() {
        return productList;
    }

    // [수정된 부분] 무조건 0개인 것을 지우지 않고, 요청받은 특정 상품만 리스트에서 제거합니다.
    public void removeProduct(Product p) {
        productList.remove(p);
    }

    private void loadProductsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("'", -1);
                if (parts.length >= 4) {
                    String name = parts[1].trim();
                    String[] middleParts = parts[2].trim().split("\\s+");
                    if (middleParts.length < 3) continue;
                    String imageName = middleParts[0];
                    int price = Integer.parseInt(middleParts[1]);
                    int quantity = Integer.parseInt(middleParts[2]);
                    String description = parts[3].trim().isEmpty() ? "설명 없음" : parts[3].trim();
                    productList.add(new Product(name, imageName, price, quantity, description));
                }
            }
        } catch (IOException e) {
            System.out.println("상품 DB 로드 중 ERROR!: " + e.getMessage());
        }
    }
}