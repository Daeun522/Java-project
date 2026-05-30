package main.ui;

import main.SpaceMallApp;
import main.model.Product;
import main.db.CartDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartPanel extends JPanel {
    private SpaceMallApp app;
    private JPanel listPanel;
    private JLabel totalPriceLabel;
    
    // 현재 화면에 렌더링된 체크박스와 상품 정보를 맵핑하기 위한 리스트
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private List<Product> displayedProducts = new ArrayList<>();

    public CartPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        // 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backBtn = new JButton("◀ 뒤로가기");
        backBtn.addActionListener(e -> app.switchPanel("MAIN"));
        
        JLabel titleLabel = new JLabel("나의 손수레", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        header.add(backBtn, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 리스트를 담을 패널 (세로 정렬)
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 결제 영역
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        totalPriceLabel = new JLabel("= 0 원");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JButton checkoutBtn = new JButton("전체 결제");
        checkoutBtn.setBackground(Color.CYAN);
        checkoutBtn.addActionListener(e -> {
            int total = calculateTotal();
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "물건을 선택!");
            } else {
                app.showCheckoutPanel(total); // 결제창으로 이동
            }
        });

        bottomPanel.add(totalPriceLabel);
        bottomPanel.add(checkoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 장바구니 화면에 들어올 때마다 최신 DB 상태로 화면 다시 그리기
    public void refreshCartUI() {
        listPanel.removeAll();
        checkBoxes.clear();
        displayedProducts.clear();

        List<Product> cartItems = CartDatabase.getInstance().getCartList();
        
        if (cartItems.isEmpty()) {
            listPanel.add(new JLabel("손수레가 텅 비었다"));
        } else {
            for (Product p : cartItems) {
                JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                itemRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                // 1. 선택 체크박스 (기본적으로 체크됨)
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(true);
                checkBox.addActionListener(e -> updateTotalLabel()); // 체크 해제/선택 시 가격 업데이트
                checkBoxes.add(checkBox);
                displayedProducts.add(p);

                // 2. 상품 정보 텍스트
                JLabel infoLabel = new JLabel(String.format(" %s | %,d 원 ", p.getName(), p.getPrice()));
                
                // 3. 삭제 버튼
                JButton deleteBtn = new JButton("삭제");
                deleteBtn.addActionListener(e -> {
                    CartDatabase.getInstance().removeProduct(p); // DB에서 삭제
                    refreshCartUI(); // 화면 새로고침
                });

                itemRow.add(checkBox);
                itemRow.add(infoLabel);
                itemRow.add(deleteBtn);
                listPanel.add(itemRow);
            }
        }
        
        listPanel.revalidate();
        listPanel.repaint();
        updateTotalLabel(); // 최종 가격 계산
    }

    // 체크된 항목들의 총 가격을 계산
    private int calculateTotal() {
        int total = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                total += displayedProducts.get(i).getPrice();
            }
        }
        return total;
    }

    // 총합 라벨 텍스트 업데이트
    private void updateTotalLabel() {
        int total = calculateTotal();
        totalPriceLabel.setText(String.format("= %,d 원", total));
    }
}