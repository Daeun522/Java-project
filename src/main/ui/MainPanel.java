package main.ui;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.model.Product;

public class MainPanel extends JPanel {
    private SpaceMallApp app;

    public MainPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        // 상단 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("🌈🌈🌈 우주쇼핑몰 🌈🌈🌈", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(headerLabel, BorderLayout.CENTER);

        // [추가] 우측 상단 장바구니 보기 버튼
        JButton goCartBtn = new JButton("손수레 보다 🛒");
        goCartBtn.addActionListener(e -> app.showCartPanel()); // 장바구니 화면으로 이동
        header.add(goCartBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 상품 목록 로드
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        List<Product> productList = loadProductsFromFile("products.txt");
        for (Product p : productList) {
            grid.add(createProductBox(p));
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private List<Product> loadProductsFromFile(String filePath) {
        List<Product> list = new ArrayList<>();
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
                    list.add(new Product(name, imageName, price, quantity, description));
                }
            }
        } catch (IOException e) {
            System.out.println("상품 파일을 찾을 수 없거나 읽기 오류: " + e.getMessage());
        }
        return list;
    }

    private JPanel createProductBox(Product p) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        box.setBackground(Color.WHITE);

        JLabel imgLabel = new JLabel();
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(p.getImageName());
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) { imgLabel.setText("[이미지]"); }

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + p.getDescription() + "</center></html>");
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("%,d 원", p.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(Color.RED);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton cartBtn = new JButton("손수레 담기");
        
        // [추가] 장바구니 버튼 로직: DB(CartDatabase)에 상품 담고 팝업 띄우기
        cartBtn.addActionListener(e -> {
            CartDatabase.getInstance().addProduct(p);
            JOptionPane.showMessageDialog(this, "손수레에 담다! 탁월한!", "알림", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(cartBtn);

        box.add(Box.createVerticalStrut(10));
        box.add(imgLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(nameLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(descLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(priceLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(btnPanel);
        box.add(Box.createVerticalStrut(10));

        return box;
    }
}