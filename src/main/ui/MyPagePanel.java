package main.ui;

import java.awt.*;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.db.UserDatabase;
import main.model.User;

public class MyPagePanel extends JPanel {
    private SpaceMallApp app;
    private JLabel idLabel;
    private JTextField pwField, phoneField, addressField;

    public MyPagePanel(SpaceMallApp app) {
        this.app = app;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("★ 내 정보 ★", SwingConstants.CENTER);
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

        centerPanel.add(createGreenLabel("ID:")); centerPanel.add(idLabel);
        centerPanel.add(createGreenLabel("password:")); centerPanel.add(pwField);
        centerPanel.add(createGreenLabel("phonecall:")); centerPanel.add(phoneField);
        centerPanel.add(createGreenLabel("address:")); centerPanel.add(addressField);

        JButton updateBtn = new JButton("정보 업데이트");
        updateBtn.addActionListener(e -> {
            User u = app.getCurrentUser();
            if(u != null) {
                u.setPassword(pwField.getText());
                u.setPhone(phoneField.getText());
                u.setAddress(addressField.getText());
                UserDatabase.getInstance().update(u);
                JOptionPane.showMessageDialog(this, "정보가 우리 서버에 저장됐다!");
            }
        });
        centerPanel.add(new JLabel("")); 
        centerPanel.add(updateBtn);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        JButton cartBtn = new JButton("내 장바구니 보기");
        JButton historyBtn = new JButton("나의물건 보기"); 
        JButton logoutBtn = new JButton("logout");
        JButton backBtn = new JButton("Main");

        cartBtn.addActionListener(e -> app.showCartPanel());
        
        // [수정] 팝업 대신 새로 만든 패널 화면으로 이동
        historyBtn.addActionListener(e -> app.switchPanel("HISTORY"));

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

    public void loadUserData() {
        User u = app.getCurrentUser();
        if (u != null) {
            // [추가] VIP 여부에 따른 타이틀 변경
            if(u.isVip()) {
                idLabel.setText(u.getId() + " ~VIP: 당신은 소중한 사람~");
                idLabel.setForeground(Color.YELLOW); // VIP는 노란색으로 강조
            } else {
                idLabel.setText(u.getId());
                idLabel.setForeground(Color.WHITE);
            }
            
            pwField.setText(u.getPassword());
            phoneField.setText(u.getPhone());
            addressField.setText(u.getAddress());
        }
    }
}