package main.ui;

import main.SpaceMallApp;
import main.db.CartDatabase;

import javax.swing.*;
import java.awt.*;

public class CheckoutPanel extends JPanel {
    private JLabel amountLabel;

    public CheckoutPanel(SpaceMallApp app) {
        setLayout(new GridBagLayout()); // 중앙 정렬
        setBackground(Color.BLACK);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        centerPanel.setOpaque(false);

        JLabel msgLabel = new JLabel("성공구매!", SwingConstants.CENTER);
        msgLabel.setForeground(Color.CYAN);
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        amountLabel = new JLabel("", SwingConstants.CENTER);
        amountLabel.setForeground(Color.YELLOW);
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton homeBtn = new JButton("쇼핑몰로 돌아가기");
        homeBtn.addActionListener(e -> {
            CartDatabase.getInstance().clearCart(); // 결제 완료 후 장바구니 비우기
            app.switchPanel("MAIN");
        });

        centerPanel.add(msgLabel);
        centerPanel.add(amountLabel);
        centerPanel.add(homeBtn);

        add(centerPanel);
    }

    // 넘어온 결제 금액을 화면에 세팅
    public void setCheckoutAmount(int amount) {
        amountLabel.setText(String.format("총: %,d 원", amount));
    }
}