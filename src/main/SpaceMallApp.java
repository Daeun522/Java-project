package main;

import main.ui.IntroPanel;
import main.ui.MainPanel;
import main.ui.CartPanel;
import main.ui.CheckoutPanel;

import javax.swing.*;
import java.awt.*;

public class SpaceMallApp extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);
    
    // 추가된 패널들
    private CartPanel cartPanel;
    private CheckoutPanel checkoutPanel;

    public SpaceMallApp() {
        setTitle("우주 쇼핑몰");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 패널 초기화
        cartPanel = new CartPanel(this);
        checkoutPanel = new CheckoutPanel(this);

        // 컨테이너에 화면 추가
        container.add(new IntroPanel(this), "INTRO");
        container.add(new MainPanel(this), "MAIN");
        container.add(cartPanel, "CART");
        container.add(checkoutPanel, "CHECKOUT");

        add(container);
        setVisible(true);
    }

    // 기본 화면 이동
    public void switchPanel(String panelName) {
        cardLayout.show(container, panelName);
    }

    // 장바구니 화면으로 이동 (이동 전 최신 DB 데이터로 새로고침)
    public void showCartPanel() {
        cartPanel.refreshCartUI();
        cardLayout.show(container, "CART");
    }

    // 결제 화면으로 이동 (선택된 총 금액 전달)
    public void showCheckoutPanel(int finalAmount) {
        checkoutPanel.setCheckoutAmount(finalAmount);
        cardLayout.show(container, "CHECKOUT");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceMallApp());
    }
}