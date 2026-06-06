package main.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.PurchaseDatabase;
import main.model.Product;
import main.model.User;

public class PurchaseHistoryPanel extends JPanel {
    private SpaceMallApp app;
    private JPanel listPanel;

    public PurchaseHistoryPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel title = new JLabel("=== 나의 물건 (구매 내역) ===", SwingConstants.CENTER);
        title.setForeground(Color.CYAN);
        title.setFont(new Font("Gulim", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("뒤로가기");
        backBtn.addActionListener(e -> app.switchPanel("MYPAGE"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 화면 진입 시 최신 구매 내역 로드
    public void loadHistory() {
        listPanel.removeAll();
        User u = app.getCurrentUser();
        
        if (u != null) {
            List<Product> history = PurchaseDatabase.getInstance().getPurchaseHistory(u.getId());
            
            if (history.isEmpty()) {
                JLabel emptyLabel = new JLabel("텅~ 좋은 물건! 구매 부탁!.");
                emptyLabel.setForeground(Color.WHITE);
                emptyLabel.setFont(new Font("Gulim", Font.BOLD, 18));
                listPanel.add(emptyLabel);
            } else {
                for (Product p : history) {
                    // 가격이 0원이면 공백으로 처리 (VIP 감사카드용)
                    String priceStr = p.getPrice() == 0 ? " " : String.format("%,d원", p.getPrice());
                    String text = String.format("■ %s | %s | %s", p.getName(), priceStr, p.getDescription());
                    
                    JLabel itemLabel = new JLabel(text);
                    itemLabel.setForeground(Color.GREEN);
                    itemLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                    listPanel.add(itemLabel);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}