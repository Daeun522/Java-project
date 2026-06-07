package main.ui;

import java.awt.*;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.UserDatabase;
import main.model.User;

public class RegisterPanel extends JPanel {
    public RegisterPanel(SpaceMallApp app) {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("=== 신규 등록 ===");
        title.setForeground(Color.CYAN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);
        gbc.gridwidth = 1; gbc.gridy++;
        JTextField idField = new JTextField(15);
        addLabelAndField("새 아이디:", idField, gbc);

        gbc.gridy++;
        JPasswordField pwField = new JPasswordField(15);
        addLabelAndField("새 비밀번호:", pwField, gbc);

        gbc.gridy++;
        JTextField phoneField = new JTextField(15);
        addLabelAndField("전화번호(11자):", phoneField, gbc);

        gbc.gridy++;
        JTextField addressField = new JTextField(15);
        addLabelAndField("주소:", addressField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy++;
        JButton regBtn = new JButton("등록 완료");
        JButton backBtn = new JButton("취소");
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(regBtn); btnPanel.add(backBtn);
        add(btnPanel, gbc);

        backBtn.addActionListener(e -> app.switchPanel("MAIN"));

        regBtn.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());
            String phone = phoneField.getText();
            String addr = addressField.getText();

            if (phone.length() != 11) {
                errorLabel.setText("Error: 전화번호는 정확히 11자리여야 합니다.");
                return;
            }

            boolean success = UserDatabase.getInstance().register(new User(id, pw, phone, addr));
            if (success) {
                JOptionPane.showMessageDialog(this, "등록 완료! 로그인해주세요.");
                idField.setText(""); pwField.setText(""); phoneField.setText(""); addressField.setText("");
                errorLabel.setText(" ");
                app.switchPanel("MAIN");
            } else {
                errorLabel.setText("Error: 이미 존재하는 아이디입니다.");
            }
        });
    }

    private void addLabelAndField(String labelText, JComponent field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.GREEN);
        add(label, gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }
}