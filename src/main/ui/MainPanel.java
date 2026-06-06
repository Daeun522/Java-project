package main.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
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
    private JPanel grid;
    private JLabel emptyLabel;

    // 이스터에그용 변수
    private int clickCount = 0;
    private JPanel bannerPanel; 
    private JLabel hiddenImageLabel;

    public MainPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        try {
            backgroundImage = new ImageIcon("background.png").getImage();
        } catch (Exception e) {
            System.out.println("배경 이미지를 불러올 수 없습니다: " + e.getMessage());
        }

        // --- 상단 전체 컨테이너 (1층: 헤더, 2층: 배너) ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false); // 배경 투명화

        // --- 1층: 기존 상단 헤더 ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanelWrapper.setOpaque(false);

        JLabel leftEmoticon = new JLabel("🌈🌈🌈");
        leftEmoticon.setForeground(Color.GREEN);
        leftEmoticon.setFont(new Font("Gulim", Font.BOLD, 40));
        leftEmoticon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel remainTitle = new JLabel(" 우주쇼핑몰 🌈🌈🌈");
        remainTitle.setForeground(Color.GREEN);
        remainTitle.setFont(new Font("Gulim", Font.BOLD, 40));

        titlePanelWrapper.add(leftEmoticon);
        titlePanelWrapper.add(remainTitle);
        header.add(titlePanelWrapper, BorderLayout.CENTER);

        rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false); 
        header.add(rightHeader, BorderLayout.EAST);

        topContainer.add(header, BorderLayout.NORTH); // 1층에 헤더 부착

        // --- 2층: 히든 배너 영역 ---
        // [수정] 닫기 버튼과 배너 이미지를 함께 넣기 위해 BorderLayout 사용
        bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setOpaque(false); 
        bannerPanel.setVisible(false); 
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5)); // 여백 조절

        hiddenImageLabel = new JLabel();
        hiddenImageLabel.setHorizontalAlignment(SwingConstants.CENTER); // 이미지 중앙 정렬
        try {
            ImageIcon icon = new ImageIcon("title.png");
            Image img = icon.getImage().getScaledInstance(700, 120, Image.SCALE_SMOOTH);
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
                    System.out.println("기본 브라우저를 열 수 없습니다: " + ex.getMessage());
                }
            }
        });

        // [추가] 배너 닫기 버튼 (찌라시 느낌의 스타일)
        JButton closeBannerBtn = new JButton("[X] 광고 닫기");
        closeBannerBtn.setBackground(Color.BLACK);
        closeBannerBtn.setForeground(Color.RED);
        closeBannerBtn.setFont(new Font("Gulim", Font.BOLD, 12));
        closeBannerBtn.setFocusPainted(false);
        closeBannerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 닫기 버튼 액션: 패널 숨기고 카운트 초기화
        closeBannerBtn.addActionListener(e -> {
            bannerPanel.setVisible(false);
            clickCount = 0; // 다시 5번 누르면 나올 수 있게 초기화
            topContainer.revalidate();
            topContainer.repaint();
        });

        // 닫기 버튼을 우측에 붙이기 위한 래퍼 패널
        JPanel closeBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        closeBtnPanel.setOpaque(false);
        closeBtnPanel.add(closeBannerBtn);

        bannerPanel.add(closeBtnPanel, BorderLayout.NORTH); // 배너 영역 위쪽에 닫기 버튼 배치
        bannerPanel.add(hiddenImageLabel, BorderLayout.CENTER); // 배너 영역 중앙에 이미지 배치

        topContainer.add(bannerPanel, BorderLayout.SOUTH); // 2층에 배너 최종 부착

        add(topContainer, BorderLayout.NORTH); 

        // 좌측 이모티콘 5번 클릭 시 배너 해금
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

        // --- 하단 상품 목록 영역 ---
        grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        grid.setOpaque(false); 
        
        emptyLabel = new JLabel("※다시 꾸미는 중(일시적 사태)※.", SwingConstants.CENTER);
        emptyLabel.setForeground(Color.RED);
        emptyLabel.setFont(new Font("Gulim", Font.BOLD, 30));

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false); 
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(null); 
        
        add(scrollPane, BorderLayout.CENTER);
        
        refreshProductList();
    }

    public void refreshProductList() {
        List<Product> productList = ProductDatabase.getInstance().getAllProducts();
        
        if (productList.isEmpty()) {
            scrollPane.setViewportView(emptyLabel);
        } else {
            grid.removeAll();
            for (Product p : productList) {
                grid.add(createProductBox(p));
            }
            grid.revalidate();
            grid.repaint();
            scrollPane.setViewportView(grid);
        }
    }

    public void updateHeader() {
        rightHeader.removeAll(); 

        Font retroFont = new Font("Gulim", Font.BOLD, 14);
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
            JButton myPageBtn = new JButton("내 정보 센터");
            JButton cartBtn = new JButton("손수레 보다 🛒");
            
            styleButton(myPageBtn, btnBg, btnFg, retroFont);
            styleButton(cartBtn, btnBg, btnFg, retroFont);

            myPageBtn.addActionListener(e -> app.switchPanel("MYPAGE"));
            cartBtn.addActionListener(e -> app.showCartPanel());

            rightHeader.add(myPageBtn);
            JLabel separator = new JLabel(" | ");
            separator.setForeground(Color.WHITE);
            rightHeader.add(separator);
            rightHeader.add(cartBtn);
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

        JLabel qtyLabel = new JLabel(String.format("남은 수량: %d개", p.getQuantity()));
        qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        qtyLabel.setForeground(Color.BLUE);
        qtyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton cartBtn = new JButton("손수레 담기");
        
        cartBtn.addActionListener(e -> {
            if(app.getCurrentUser() == null) {
                JOptionPane.showMessageDialog(this, "로그인 필요!.");
                return;
            }

            if (p.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(this, "물건이 다 떨어졌다! (품절)", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            p.decreaseQuantity();
            CartDatabase.getInstance().addProduct(p);

            JOptionPane.showMessageDialog(this, "손수레에 담다!\n(남은 수량: " + p.getQuantity() + "개)", "알림", JOptionPane.INFORMATION_MESSAGE);
            refreshProductList(); 
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
        box.add(Box.createVerticalStrut(5));
        box.add(qtyLabel); 
        box.add(Box.createVerticalStrut(10));
        box.add(btnPanel);
        box.add(Box.createVerticalStrut(10));

        return box;
    }
}