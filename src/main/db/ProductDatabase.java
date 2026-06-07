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

    // 기존 로직 유지
    public void removeProduct(Product p) {
        productList.remove(p);
    }

    private void loadProductsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                // 기존 파싱 방식: 작은따옴표(')를 기준으로 분리
                String[] parts = line.split("'", -1);
                
                // parts 구조: [0: 빈값, 1: 상품명, 2: 나머지공백값, 3: 설명, 4: 카테고리번호 등]
                if (parts.length >= 5) {
                    String name = parts[1].trim();
                    
                    // 나머지 정보 파싱 (이미지명, 가격, 수량)
                    String[] middleParts = parts[2].trim().split("\\s+");
                    if (middleParts.length < 3) continue;
                    
                    String imageName = middleParts[0];
                    int price = Integer.parseInt(middleParts[1]);
                    int quantity = Integer.parseInt(middleParts[2]);
                    
                    String description = parts[3].trim().isEmpty() ? "설명 없음" : parts[3].trim();
                    
                    // [수정] 마지막 카테고리 번호 추출
                    // parts[4] 혹은 그 이후에 존재하는 카테고리 번호를 가져옵니다.
                    String categoryPart = parts[4].trim();
                    int categoryId = 1; // 기본값
                    try {
                        // 공백이 섞여있을 수 있으므로 첫 번째 숫자만 파싱
                        categoryId = Integer.parseInt(categoryPart.split("\\s+")[0]);
                    } catch (Exception e) {
                        categoryId = 1; // 파싱 실패 시 기본값 1
                    }

                    // [수정] Product 객체 생성 시 categoryId 추가
                    productList.add(new Product(name, imageName, price, quantity, description, categoryId));
                }
            }
        } catch (IOException e) {
            System.out.println("상품 DB 로드 중 ERROR!: " + e.getMessage());
        }
    }
}