package main.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*; // [추가] 구매 내역의 상품 정보를 가져오기 위해 필요
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.db.UserDatabase;
import main.model.Product;
import main.model.User;


public class MyPagePanel extends JPanel {
    private SpaceMallApp app;
    private JLabel idLabel;
    private JTextField pwField, phoneField, addressField;

    public MyPagePanel(SpaceMallApp app) {
        this.app = app;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("★ 내 정보 센터 ★", SwingConstants.CENTER);
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        idLabel = new JLabel("ID: ");
        idLabel.setForeground(Color.WHITE);
        pwField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();

        centerPanel.add(createGreenLabel("아이디 (수정불가):")); centerPanel.add(idLabel);
        centerPanel.add(createGreenLabel("비밀번호:")); centerPanel.add(pwField);
        centerPanel.add(createGreenLabel("전화번호:")); centerPanel.add(phoneField);
        centerPanel.add(createGreenLabel("우주 주소:")); centerPanel.add(addressField);

        JButton updateBtn = new JButton("정보 업데이트");
        updateBtn.addActionListener(e -> {
            User u = app.getCurrentUser();
            if(u != null) {
                u.setPassword(pwField.getText());
                u.setPhone(phoneField.getText());
                u.setAddress(addressField.getText());
                UserDatabase.getInstance().update(u);
                JOptionPane.showMessageDialog(this, "정보가 우주 서버에 저장되었습니다.");
            }
        });
        centerPanel.add(new JLabel("")); // 빈칸 맞추기
        centerPanel.add(updateBtn);

        add(centerPanel, BorderLayout.CENTER);

        // 하단 버튼들
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        JButton cartBtn = new JButton("내 장바구니 보기");
        JButton historyBtn = new JButton("구매내역 보기"); // [수정] 텍스트 변경
        JButton logoutBtn = new JButton("로그아웃");
        JButton backBtn = new JButton("메인으로");

        cartBtn.addActionListener(e -> app.showCartPanel());
        
        // [핵심 추가] 구매 내역 버튼 액션
        historyBtn.addActionListener(e -> {
            User u = app.getCurrentUser();
            if (u != null) {
                List<Product> history = u.getPurchaseHistory();
                if (history.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "아직 우주에서 구매한 내역이 없습니다.", "텅~", JOptionPane.WARNING_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("=== 나의 우주 구매 내역 ===\n\n");
                    for (Product p : history) {
                        sb.append("- ").append(p.getName()).append(" [").append(p.getPrice()).append("원]\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "영수증 보관함", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        logoutBtn.addActionListener(e -> {
            CartDatabase.getInstance().clearCart();
            app.setCurrentUser(null);
            app.switchPanel("MAIN");
        });
        
        backBtn.addActionListener(e -> app.switchPanel("MAIN"));

        bottomPanel.add(cartBtn);
        bottomPanel.add(historyBtn);
        bottomPanel.add(logoutBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createGreenLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.GREEN);
        return l;
    }

    // 마이페이지 열릴 때마다 호출되어 데이터 채워넣기
    public void loadUserData() {
        User u = app.getCurrentUser();
        if (u != null) {
            idLabel.setText(u.getId());
            pwField.setText(u.getPassword());
            phoneField.setText(u.getPhone());
            addressField.setText(u.getAddress());
        }
    }
}