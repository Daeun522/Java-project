package main;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import main.model.Product;
import main.model.User;
import main.ui.CartPanel;
import main.ui.CheckoutPanel;
import main.ui.IntroPanel;
import main.ui.LoginPanel;
import main.ui.MainPanel;
import main.ui.MyPagePanel;
import main.ui.RegisterPanel;

public class SpaceMallApp extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);
    private User currentUser = null; // 로그인 상태 저장
    private MainPanel mainPanel; // 업데이트를 위해 변수로 빼둡니다.
    private MyPagePanel myPagePanel;
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
        mainPanel = new MainPanel(this);
        myPagePanel = new MyPagePanel(this);

        // 컨테이너에 화면 추가
        
        container.add(new IntroPanel(this), "INTRO");
        container.add(mainPanel, "MAIN");
        container.add(new LoginPanel(this), "LOGIN");
        container.add(new RegisterPanel(this), "REGISTER");
        container.add(myPagePanel, "MYPAGE");
        container.add(cartPanel, "CART");
        container.add(checkoutPanel, "CHECKOUT");

        add(container);
        setVisible(true);
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    
    // 기본 화면 이동
    public void switchPanel(String panelName) {
        if(panelName.equals("MAIN")) {
            mainPanel.updateHeader(); // 메인 화면으로 갈 때마다 헤더(로그인/마이페이지 버튼) 갱신
        } else if(panelName.equals("MYPAGE")) {
            myPagePanel.loadUserData(); // 마이페이지 진입 시 최신 데이터 로드
        }
        cardLayout.show(container, panelName);
    }

    // 장바구니 화면으로 이동 (이동 전 최신 DB 데이터로 새로고침)
    public void showCartPanel() {
        cartPanel.refreshCartUI();
        cardLayout.show(container, "CART");
    }

    // 결제 화면으로 이동 (선택된 총 금액 전달)
    public void showCheckoutPanel(List<Product> products, int amount) {
        checkoutPanel.setCheckoutData(products, amount);
        cardLayout.show(container, "CHECKOUT");
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceMallApp());
    }
}