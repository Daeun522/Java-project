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
import main.ui.PurchaseHistoryPanel;
import main.ui.RegisterPanel;

public class SpaceMallApp extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);
    private User currentUser = null; 
    private MainPanel mainPanel; 
    private MyPagePanel myPagePanel;
    private CartPanel cartPanel;
    private CheckoutPanel checkoutPanel;
    private PurchaseHistoryPanel historyPanel; 

    public SpaceMallApp() {
        setTitle("우주 쇼핑몰");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cartPanel = new CartPanel(this);
        checkoutPanel = new CheckoutPanel(this);
        mainPanel = new MainPanel(this);
        myPagePanel = new MyPagePanel(this);
        historyPanel = new PurchaseHistoryPanel(this);

        container.add(new IntroPanel(this), "INTRO");
        container.add(mainPanel, "MAIN");
        container.add(new LoginPanel(this), "LOGIN");
        container.add(new RegisterPanel(this), "REGISTER");
        container.add(myPagePanel, "MYPAGE");
        container.add(cartPanel, "CART");
        container.add(checkoutPanel, "CHECKOUT");
        container.add(historyPanel, "HISTORY");

        add(container);
        setVisible(true);
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    
    public void switchPanel(String panelName) {
        if(panelName.equals("MAIN")) {
            mainPanel.refreshProductList(); //상품갱신
            mainPanel.updateHeader(); 
        } else if(panelName.equals("MYPAGE")) {
            myPagePanel.loadUserData(); 
        } else if(panelName.equals("HISTORY")) {
            historyPanel.loadHistory();
        }
        cardLayout.show(container, panelName);
    }

    public void showCartPanel() {
        cartPanel.refreshCartUI();
        cardLayout.show(container, "CART");
    }

    public void showCheckoutPanel(List<Product> products, int amount) {
        checkoutPanel.setCheckoutData(products, amount);
        cardLayout.show(container, "CHECKOUT");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceMallApp());
    }
}