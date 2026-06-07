package main.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.db.ProductDatabase;
import main.db.PurchaseDatabase;
import main.model.Product;
import main.model.User;

//결제 화면입니다. 
public class CheckoutPanel extends JPanel {
    private SpaceMallApp app;
    private JTextArea receiptArea;
    private List<Product> currentItems;

    public CheckoutPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        //최종 결제 페이지: 물건, 총합, 결제 버튼 기능
        JLabel title = new JLabel("=== 최종 결제 확인 ===", SwingConstants.CENTER);
        title.setForeground(Color.CYAN);
        title.setFont(new Font("Gulim", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.DARK_GRAY);
        receiptArea.setForeground(Color.GREEN); 
        receiptArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        JButton payBtn = new JButton("결제완료");
        payBtn.setBackground(Color.RED);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Gulim", Font.BOLD, 20));

        payBtn.addActionListener(e -> processPayment());    //버튼: processPayment 수행

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(payBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setCheckoutData(List<Product> items, int amount) {
        this.currentItems = items;
        User u = app.getCurrentUser();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n [ 구매 정보 ]\n");
        sb.append(" - 아이디: ").append(u.getId()).append("\n");
        sb.append(" - 배송지: ").append(u.getAddress()).append("\n\n");
        
        sb.append(" [ 구매 상품 리스트 ]\n");
        for(Product p : items) {
            sb.append(" - ").append(p.getName()).append(" (").append(p.getPrice()).append("원)\n");
        }
        
        sb.append("\n =====================================\n");
        sb.append("  총 결제 금액: ").append(amount).append(" 원\n");
        sb.append(" =====================================\n\n");
        
        sb.append(" [ 입금 계좌 정보 ]\n");
        sb.append("  무통장 입금 계좌명: 우주은행 123-0000-xxxxx\n");

        receiptArea.setText(sb.toString());
    }

    private void processPayment() {
        JDialog dialog = new JDialog(app, "로켓 배송 진행중...", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.getContentPane().setBackground(Color.white);
        
        // 배송중일 때 창 못닫게 함
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JLabel msg1 = new JLabel("성공구매!", SwingConstants.CENTER);
        msg1.setForeground(Color.YELLOW);
        msg1.setFont(new Font("Gulim", Font.BOLD, 22));

        JLabel msg2 = new JLabel("물건이 로켓을 탔다!", SwingConstants.CENTER);
        msg2.setForeground(Color.CYAN);
        msg2.setFont(new Font("Gulim", Font.BOLD, 18));

        JLabel timerLabel = new JLabel("도착까지 00:00:05", SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

        dialog.add(msg1);
        dialog.add(msg2);
        dialog.add(timerLabel);

        Timer timer = new Timer(1000, new ActionListener() {
            int secondsLeft = 5;
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                timerLabel.setText("도착까지 00:00:0" + secondsLeft);
                
                if (secondsLeft <= 0) {
                    ((Timer)e.getSource()).stop(); 
                    dialog.dispose(); 
                    
                    JOptionPane.showMessageDialog(app, "도착!", "로켓 배송 완료", JOptionPane.INFORMATION_MESSAGE);
                    
                    User u = app.getCurrentUser();
                    if(u != null) {
                        int totalPurchasePrice = 0;
                        for(Product p : currentItems) { totalPurchasePrice += p.getPrice(); }
                        
                        boolean wasVip = u.isVip();
                        u.addSpent(totalPurchasePrice);
                        
                        if (u.getTotalSpent() >= 100000000 && !wasVip) {
                            u.setVip(true);
                            JOptionPane.showMessageDialog(app, "[감사카드\n나는 우주 쇼핑몰 VIP!\n~감사 세포 동봉~]", "VIP 승급!", JOptionPane.WARNING_MESSAGE);
                            Product vipCard = new Product("감사카드", "", 0, 1, "나는 우주 쇼핑몰 VIP!", 3);
                            PurchaseDatabase.getInstance().addPurchase(u.getId(), Arrays.asList(vipCard));
                        }
                        
                        PurchaseDatabase.getInstance().addPurchase(u.getId(), currentItems);
                        
                        //'진짜로 결제한 물건'들 중에서 수량이 0인 것만 DB에서 삭제
                        for (Product p : currentItems) {
                            if (p.getQuantity() <= 0) {
                                ProductDatabase.getInstance().removeProduct(p);
                            }
                        }
                    }
                    
                    for(Product p : currentItems){
                        CartDatabase.getInstance().removeProduct(p);
                    }
                    app.switchPanel("MAIN");
                }
            }
        });
        
        timer.start();
        dialog.setVisible(true);
    }
}