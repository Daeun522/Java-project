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
    
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private List<Product> displayedProducts = new ArrayList<>();

    public CartPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout());

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

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        totalPriceLabel = new JLabel("= 0 원");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JButton checkoutBtn = new JButton("전체 결제");
        checkoutBtn.setBackground(Color.CYAN);
        
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
                app.showCheckoutPanel(selectedProducts, total); 
            }
        });

        bottomPanel.add(totalPriceLabel);
        bottomPanel.add(checkoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshCartUI() {
        listPanel.removeAll(); 
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
                    p.increaseQuantity(); // [추가] 장바구니에서 빼면 수량 원상복구
                    CartDatabase.getInstance().removeProduct(p);
                    refreshCartUI(); 
                });

                itemRow.add(checkBox);
                itemRow.add(infoLabel);
                itemRow.add(deleteBtn);
                listPanel.add(itemRow);
            }
        }
        
        listPanel.revalidate(); 
        listPanel.repaint();    
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