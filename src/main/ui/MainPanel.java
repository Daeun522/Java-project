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
    private Image backgroundImage; 
    private JPanel rightHeader; // [추가] 우측 상단 버튼들이 들어갈 동적 패널

    public MainPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        // 배경 이미지 로드
        try {
            backgroundImage = new ImageIcon("background.png").getImage();
        } catch (Exception e) {
            System.out.println("배경 이미지를 불러올 수 없습니다: " + e.getMessage());
        }

        // --- 상단 헤더 ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("🌈🌈🌈 우주쇼핑몰 🌈🌈🌈", SwingConstants.CENTER);
        headerLabel.setForeground(Color.GREEN);
        headerLabel.setFont(new Font("Gulim", Font.BOLD | Font.ITALIC, 40));
        header.add(headerLabel, BorderLayout.CENTER);

        // [추가] 우측 상단 동적 버튼 패널 초기화
        rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightHeader.setOpaque(false); // 헤더 배경색(DARK_GRAY)이 보이도록 투명화
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 초기 헤더 버튼 세팅 (로그인 여부 확인)
        updateHeader();

        // --- 상품 목록 로드 ---
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        grid.setOpaque(false); // 배경 이미지가 보이도록 투명하게 설정

        List<Product> productList = loadProductsFromFile("products.txt");
        for (Product p : productList) {
            grid.add(createProductBox(p));
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(null); 
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // [핵심 추가] 로그인 상태에 따라 우측 상단 버튼을 동적으로 변경하는 메서드
    public void updateHeader() {
        rightHeader.removeAll(); // 기존 버튼 모두 지우기

        // 찌라시 감성 버튼 스타일 설정 헬퍼 (익명 내부 클래스/메서드 대신 직접 적용)
        Font retroFont = new Font("Gulim", Font.BOLD, 14);
        Color btnBg = Color.BLACK;
        Color btnFg = Color.CYAN;

        // SpaceMallApp에 getCurrentUser()가 null인지 여부로 로그인 판단
        if (app.getCurrentUser() == null) {
            // [비로그인 상태] 로그인 | 회원가입
            JButton loginBtn = new JButton("접속(로그인)");
            JButton regBtn = new JButton("신규등록");
            
            styleButton(loginBtn, btnBg, btnFg, retroFont);
            styleButton(regBtn, btnBg, btnFg, retroFont);

            loginBtn.addActionListener(e -> app.switchPanel("LOGIN"));
            regBtn.addActionListener(e -> app.switchPanel("REGISTER"));

            rightHeader.add(loginBtn);
            JLabel separator = new JLabel(" | ");
            separator.setForeground(Color.WHITE);
            rightHeader.add(separator);
            rightHeader.add(regBtn);
        } else {
            // [로그인 상태] 마이페이지 | 장바구니
            JButton myPageBtn = new JButton("내 정보 센터");
            JButton cartBtn = new JButton("손수레 보다 🛒");
            
            styleButton(myPageBtn, btnBg, btnFg, retroFont);
            styleButton(cartBtn, btnBg, btnFg, retroFont);

            myPageBtn.addActionListener(e -> app.switchPanel("MYPAGE"));
            // 기존 app.showCartPanel()을 쓰셨다면 아래 코드를 상황에 맞게 수정하세요.
            // (여기서는 CardLayout의 표준인 switchPanel로 통일했습니다)
            cartBtn.addActionListener(e -> app.showCartPanel());

            rightHeader.add(myPageBtn);
            JLabel separator = new JLabel(" | ");
            separator.setForeground(Color.WHITE);
            rightHeader.add(separator);
            rightHeader.add(cartBtn);
        }

        // 화면 새로고침
        rightHeader.revalidate();
        rightHeader.repaint();
    }

    // 버튼 스타일링 중복 제거용 헬퍼 메서드
    private void styleButton(JButton btn, Color bg, Color fg, Font font) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(font);
        btn.setBorder(BorderFactory.createLineBorder(fg));
        btn.setFocusPainted(false);
    }

    // 배경 이미지를 화면에 그리는 메서드 오버라이드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // 파일에서 상품 목록 읽어오기
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

    // 개별 상품 카드 생성
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
        
        cartBtn.addActionListener(e -> {

            if(app.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "로그인 필요!."
                );
                return;
            }

            CartDatabase.getInstance().addProduct(p);

            JOptionPane.showMessageDialog(
                this,
                "손수레에 담다!",
                "알림",
                JOptionPane.INFORMATION_MESSAGE
            );
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