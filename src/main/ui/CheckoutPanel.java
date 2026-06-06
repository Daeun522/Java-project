package main.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.model.Product;
import main.model.User;

public class CheckoutPanel extends JPanel {
    private SpaceMallApp app;
    private JTextArea receiptArea;
    private List<Product> currentItems;

    public CheckoutPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        JLabel title = new JLabel("=== 최종 결제 확인 ===", SwingConstants.CENTER);
        title.setForeground(Color.CYAN);
        title.setFont(new Font("Gulim", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // 영수증 내역이 출력될 텍스트 구역
        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.DARK_GRAY);
        receiptArea.setForeground(Color.GREEN); // 터미널 감성
        receiptArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        JButton payBtn = new JButton("결제완료");
        payBtn.setBackground(Color.RED);
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Gulim", Font.BOLD, 20));

        payBtn.addActionListener(e -> processPayment());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(payBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 넘어온 결제 데이터로 영수증 텍스트 그리기
    public void setCheckoutData(List<Product> items, int amount) {
        this.currentItems = items;
        User u = app.getCurrentUser();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n [ 구매 외계인 정보 ]\n");
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

    // 결제 타이머 애니메이션 처리
    private void processPayment() {
        // 타이머용 커스텀 다이얼로그(팝업창) 생성
        JDialog dialog = new JDialog(app, "로켓 배송 진행중...", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.getContentPane().setBackground(Color.BLACK);

        JLabel msg1 = new JLabel("성공구매!", SwingConstants.CENTER);
        msg1.setForeground(Color.YELLOW);
        msg1.setFont(new Font("Gulim", Font.BOLD, 22));

        JLabel msg2 = new JLabel("물건이 로켓을 탔다!", SwingConstants.CENTER);
        msg2.setForeground(Color.CYAN);
        msg2.setFont(new Font("Gulim", Font.BOLD, 18));

        JLabel timerLabel = new JLabel("도착까지 00:00:08", SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

        dialog.add(msg1);
        dialog.add(msg2);
        dialog.add(timerLabel);

        // 1초(1000ms)마다 실행되는 타이머
        Timer timer = new Timer(1000, new ActionListener() {
            int secondsLeft = 8;
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                timerLabel.setText("도착까지 00:00:0" + secondsLeft);
                
                if (secondsLeft <= 0) {
                    ((Timer)e.getSource()).stop(); // 타이머 종료
                    dialog.dispose(); // 팝업 닫기
                    
                    // 1. 도착 알림
                    JOptionPane.showMessageDialog(app, "도착!", "로켓 배송 완료", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 2. 유저 정보에 구매 내역 추가
                    if(app.getCurrentUser() != null) {
                        app.getCurrentUser().addPurchaseHistory(currentItems);
                    }
                    
                    // 3. 장바구니 비우고 메인으로 튕겨내기
                    for(Product p : currentItems){
                        CartDatabase.getInstance().removeProduct(p);
                    }
                    app.switchPanel("MAIN");
                }
            }
        });
        
        timer.start();
        dialog.setVisible(true); // 창을 화면에 띄움 (타이머가 0이 될 때까지 여기서 멈춤)
    }
}