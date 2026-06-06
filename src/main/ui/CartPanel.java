package main.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.CartDatabase;
import main.model.Product;

public class CartPanel extends JPanel {
    private SpaceMallApp app;
    private JPanel listPanel;
    private JLabel totalPriceLabel;
    
    // 화면 갱신을 위해 현재 리스트 정보를 저장하는 리스트
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
        
        // 결제 버튼 로직: 선택된 상품 리스트와 총액을 함께 전달
        checkoutBtn.addActionListener(e -> {
            List<Product> selectedProducts = new ArrayList<>();
            int total = 0;
            
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selectedProducts.add(displayedProducts.get(i));
                    total += displayedProducts.get(i).getPrice();
                }
            }
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "물건을 선택하세요!");
            } else {
                // 수정된 결제 메서드 호출
                app.showCheckoutPanel(selectedProducts, total); 
            }
        });

        bottomPanel.add(totalPriceLabel);
        bottomPanel.add(checkoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 장바구니 데이터를 DB에서 가져와 화면을 새로 그리는 메서드
    public void refreshCartUI() {
        listPanel.removeAll(); // 기존 화면 제거
        checkBoxes.clear();
        displayedProducts.clear();

        List<Product> cartItems = CartDatabase.getInstance().getCartList();
        
        if (cartItems.isEmpty()) {
            listPanel.add(new JLabel("손수레가 텅 비었다..."));
        } else {
            for (Product p : cartItems) {
                JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
                itemRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(true);
                checkBox.addActionListener(e -> updateTotalLabel());
                
                checkBoxes.add(checkBox);
                displayedProducts.add(p);

                JLabel infoLabel = new JLabel(String.format(" %s | %,d 원 ", p.getName(), p.getPrice()));
                
                JButton deleteBtn = new JButton("삭제");
                deleteBtn.addActionListener(e -> {
                    CartDatabase.getInstance().removeProduct(p);
                    refreshCartUI(); // 삭제 후 화면 강제 갱신
                });

                itemRow.add(checkBox);
                itemRow.add(infoLabel);
                itemRow.add(deleteBtn);
                listPanel.add(itemRow);
            }
        }
        
        listPanel.revalidate(); // UI 다시 배치
        listPanel.repaint();    // UI 다시 그리기
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        int total = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                total += displayedProducts.get(i).getPrice();
            }
        }
        totalPriceLabel.setText(String.format("= %,d 원", total));
    }
}