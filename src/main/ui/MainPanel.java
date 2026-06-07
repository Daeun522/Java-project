package main.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList; // [추가] 리스트 필터링을 위해 추가
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.db.ProductDatabase;
import main.model.Product;

public class MainPanel extends JPanel {
    private SpaceMallApp app;
    private Image backgroundImage; 
    private JPanel rightHeader; 
    
    private JScrollPane scrollPane;
    private JPanel productGridPanel;
    private JLabel emptyLabel;

    private int clickCount = 0;
    private JPanel bannerPanel; 
    private JLabel hiddenImageLabel;

    // [추가] 현재 선택된 카테고리 번호 (0: 전체, 1: 식품, 2: 도구, 3: ???)
    private int currentCategoryId = 0; 

    public MainPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        try {
            backgroundImage = new ImageIcon("background.png").getImage();
        } catch (Exception e) {
            System.out.println("배경 이미지를 불러올 수 없습니다: " + e.getMessage());
        }

        // ==========================================
        // 1. 상단 영역 (NORTH): 헤더 + 광고 배너
        // ==========================================
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        // --- 헤더 (1층) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30, 200));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanelWrapper.setOpaque(false);

        Font titleFont = new Font("Gulim", Font.BOLD | Font.ITALIC, 40);

        JLabel leftEmoticon = new JLabel("※※※");
        leftEmoticon.setForeground(Color.GREEN);
        leftEmoticon.setFont(titleFont);
        leftEmoticon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel remainTitle = new JLabel(" 우주쇼핑몰 ※※※");
        remainTitle.setForeground(Color.GREEN);
        remainTitle.setFont(titleFont);

        titlePanelWrapper.add(leftEmoticon);
        titlePanelWrapper.add(remainTitle);
        header.add(titlePanelWrapper, BorderLayout.WEST);

        rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false); 
        header.add(rightHeader, BorderLayout.EAST);

        topContainer.add(header, BorderLayout.NORTH);

        // --- 히든 배너 영역 (2층) ---
        bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(Color.DARK_GRAY);
        bannerPanel.setVisible(false);
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));

        hiddenImageLabel = new JLabel();
        hiddenImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon("title.png");
            Image img = icon.getImage().getScaledInstance(700, 110, Image.SCALE_SMOOTH);
            hiddenImageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            hiddenImageLabel.setText(">> [원작] 괴담에 떨어져도 출근을 해야 하는구나 보러가기 <<");
            hiddenImageLabel.setForeground(Color.YELLOW);
            hiddenImageLabel.setFont(new Font("Gulim", Font.BOLD, 20));
        }
        hiddenImageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        hiddenImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://webtoon.kakao.com/content/%EA%B4%B4%EB%8B%B4%EC%97%90-%EB%96%A8%EC%96%B4%EC%A0%B8%EB%8F%84-%EC%B6%9C%EA%B7%BC%EC%9D%84-%ED%95%B4%EC%95%BC-%ED%95%98%EB%8A%94%EA%B5%AC%EB%82%98/4869"));
                } catch (Exception ex) {
                    System.out.println("기본 브라우저를 열 수 없습니다.");
                }
            }
        });

        JButton closeBannerBtn = new JButton("[X]closeAD");
        closeBannerBtn.setBackground(Color.BLACK);
        closeBannerBtn.setForeground(Color.RED);
        closeBannerBtn.setFont(new Font("Gulim", Font.BOLD, 12));
        closeBannerBtn.setFocusPainted(false);
        closeBannerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeBannerBtn.addActionListener(e -> {
            bannerPanel.setVisible(false);
            clickCount = 0;
            topContainer.revalidate();
            topContainer.repaint();
        });

        JPanel closeBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        closeBtnPanel.setOpaque(false);
        closeBtnPanel.add(closeBannerBtn);

        bannerPanel.add(closeBtnPanel, BorderLayout.NORTH);
        bannerPanel.add(hiddenImageLabel, BorderLayout.CENTER);
        topContainer.add(bannerPanel, BorderLayout.SOUTH);

        leftEmoticon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!bannerPanel.isVisible()) {
                    clickCount++;
                    if (clickCount >= 5) {
                        bannerPanel.setVisible(true);
                        topContainer.revalidate(); 
                        topContainer.repaint();
                    }
                }
            }
        });

        updateHeader();

        // ==========================================
        // 2. 좌측 영역 (WEST): 카테고리 사이드바
        // ==========================================
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setPreferredSize(new Dimension(150, 0));
        categoryPanel.setBackground(new Color(173, 216, 230, 255));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel categoryTitle = new JLabel("<< 카테고리 >>");
        categoryTitle.setForeground(Color.RED);
        categoryTitle.setFont(new Font("Gulim", Font.BOLD, 13));
        categoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryPanel.add(categoryTitle);
        categoryPanel.add(Box.createVerticalStrut(20));

        String[] categories = {"전체보기", "식품", "도구", "???"};
        
        for (int i = 0; i < categories.length; i++) {
            String catName = categories[i];
            int catId = i; // 카테고리 번호 부여
            
            JButton catBtn = new JButton(catName);
            catBtn.setMaximumSize(new Dimension(130, 40));
            catBtn.setBackground(Color.BLUE);
            catBtn.setForeground(Color.WHITE);
            
            // [수정] 버튼 클릭 시 현재 카테고리를 변경하고 화면 새로고침
            catBtn.addActionListener(e -> {
                currentCategoryId = catId; 
                refreshProductList();
            });
            
            categoryPanel.add(catBtn);
            categoryPanel.add(Box.createVerticalStrut(10));
        }

        // ==========================================
        // 3. 중앙 영역 (CENTER): 세로 스크롤 가로형 목록
        // ==========================================
        productGridPanel = new JPanel();
        productGridPanel.setLayout(new BoxLayout(productGridPanel, BoxLayout.Y_AXIS));
        productGridPanel.setOpaque(false); 
        
        emptyLabel = new JLabel("※ 선택한 카테고리에 상품이 없습니다 ※", SwingConstants.CENTER);
        emptyLabel.setForeground(Color.RED);
        emptyLabel.setFont(new Font("Gulim", Font.BOLD, 20));

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(null); 
        
        add(topContainer, BorderLayout.NORTH);
        add(categoryPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshProductList();
    }

    // 카테고리 기능
    public void refreshProductList() {
        // 전체 상품 목록 가져오기
        List<Product> allProducts = ProductDatabase.getInstance().getAllProducts();
        
        // 필터링된 상품을 담을 리스트
        List<Product> filteredProducts = new ArrayList<>();
        
        for (Product p : allProducts) {
            // 현재 카테고리가 0(전체보기)이거나, 상품의 카테고리 번호가 현재 카테고리와 일치할 때만 담기
            if (currentCategoryId == 0 || p.getCategoryId() == currentCategoryId) {
                filteredProducts.add(p);
            }
        }
        
        // 화면에 필터링된 상품 그리기
        if (filteredProducts.isEmpty()) {
            scrollPane.setViewportView(emptyLabel);
        } else {
            productGridPanel.removeAll();
            for (Product p : filteredProducts) {
                productGridPanel.add(createProductBox(p));
                productGridPanel.add(Box.createVerticalStrut(10));
            }
            productGridPanel.revalidate();
            productGridPanel.repaint();
            scrollPane.setViewportView(productGridPanel);
        }
    }

    public void updateHeader() {
        rightHeader.removeAll(); 

        Font retroFont = new Font("Gulim", Font.BOLD, 13);
        Color btnBg = Color.BLACK;
        Color btnFg = Color.CYAN;

        if (app.getCurrentUser() == null) {
            JButton loginBtn = new JButton("접속(로그인)");
            JButton regBtn = new JButton("가입하다");
            
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
            JButton myPageBtn = new JButton("내 정보");
            JButton cartBtn = new JButton("장바구니");
            JButton logoutBtn = new JButton("로그아웃"); 
            
            styleButton(myPageBtn, btnBg, btnFg, retroFont);
            styleButton(cartBtn, btnBg, btnFg, retroFont);
            styleButton(logoutBtn, btnBg, Color.ORANGE, retroFont); 

            myPageBtn.addActionListener(e -> app.switchPanel("MYPAGE"));
            cartBtn.addActionListener(e -> app.showCartPanel());
            
            logoutBtn.addActionListener(e -> {
                int select = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
                if (select == JOptionPane.YES_OPTION) {
                    app.setCurrentUser(null); 
                    updateHeader(); 
                    JOptionPane.showMessageDialog(this, "이탈 완료!");
                }
            });

            rightHeader.add(myPageBtn);
            JLabel separator1 = new JLabel(" | ");
            separator1.setForeground(Color.WHITE);
            rightHeader.add(separator1);
            rightHeader.add(cartBtn);
            JLabel separator2 = new JLabel(" | ");
            separator2.setForeground(Color.WHITE);
            rightHeader.add(separator2);
            rightHeader.add(logoutBtn); 
        }

        rightHeader.revalidate();
        rightHeader.repaint();
    }

    private void styleButton(JButton btn, Color bg, Color fg, Font font) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(font);
        btn.setBorder(BorderFactory.createLineBorder(fg));
        btn.setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JPanel createProductBox(Product p) {
        JPanel box = new JPanel(new BorderLayout(15, 0));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        box.setBackground(new Color(255, 255, 255, 255)); 
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); 

        // 1) 구역: 좌측 (이미지 영역)
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 100));
        try {
            ImageIcon icon = new ImageIcon(p.getImageName());
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) { 
            imgLabel.setText("[이미지]"); 
            imgLabel.setForeground(Color.BLACK);
        }
        box.add(imgLabel, BorderLayout.WEST);

        // 2) 구역: 중앙 (상품 정보 - 이름, 설명, 가격)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); 
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);

        JLabel descLabel = new JLabel(p.getDescription());
        descLabel.setForeground(Color.DARK_GRAY);

        JLabel priceLabel = new JLabel(String.format("%,d 원", p.getPrice()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(Color.RED);

        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(priceLabel);
        
        box.add(infoPanel, BorderLayout.CENTER);

        // 3) 구역: 우측 (수량 및 담기 버튼)
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setOpaque(false);
        actionPanel.setPreferredSize(new Dimension(130, 100)); 

        JLabel qtyLabel = new JLabel(String.format("남은 수량: %d개", p.getQuantity()), SwingConstants.CENTER);
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        qtyLabel.setForeground(Color.BLUE);
        qtyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cartBtn = new JButton("손수레 담기");
        cartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartBtn.setMaximumSize(new Dimension(120, 35));
        
        cartBtn.addActionListener(e -> {
            if(app.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(this, "로그인 필요!");
                return;
            }

            if (p.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(this, "물건이 부족하다!", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            p.decreaseQuantity(); 
            CartDatabase.getInstance().addProduct(p); 

            JOptionPane.showMessageDialog(this, "손수레에 담다!\n(남은 수량: " + p.getQuantity() + "개)", "알림", JOptionPane.INFORMATION_MESSAGE);
            refreshProductList(); 
        });

        actionPanel.add(Box.createVerticalGlue());
        actionPanel.add(qtyLabel);
        actionPanel.add(Box.createVerticalStrut(10));
        actionPanel.add(cartBtn);
        actionPanel.add(Box.createVerticalGlue());

        box.add(actionPanel, BorderLayout.EAST);

        return box;
    }
}