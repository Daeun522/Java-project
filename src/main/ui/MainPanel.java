package main.ui;

import java.awt.*;
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

    public MainPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        try {
            backgroundImage = new ImageIcon("background.png").getImage();
        } catch (Exception e) {
            System.out.println("배경 이미지를 불러올 수 없습니다: " + e.getMessage());
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("🌈🌈🌈 우주쇼핑몰 🌈🌈🌈", SwingConstants.CENTER);
        headerLabel.setForeground(Color.GREEN);
        headerLabel.setFont(new Font("Gulim", Font.BOLD , 40));
        header.add(headerLabel, BorderLayout.CENTER);

        rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightHeader.setOpaque(false); 
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        updateHeader();

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

        // [추가] 남은 수량 표시 라벨
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

            // [추가] 수량이 0이면 장바구니에 담기 금지
            if (p.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(this, "물건이 다 떨어졌다! (품절)", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // [추가] 장바구니 담을 때 수량 감소 및 UI 즉시 갱신
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
        box.add(qtyLabel); // 수량 라벨 추가
        box.add(Box.createVerticalStrut(10));
        box.add(btnPanel);
        box.add(Box.createVerticalStrut(10));

        return box;
    }
}