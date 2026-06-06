package main.ui;

import main.SpaceMallApp;
import main.db.UserDatabase;
import main.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(SpaceMallApp app) {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout()); // 중앙 배치
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("[ 우주 통신 로그인 ]");
        title.setForeground(Color.GREEN);
        add(title, gbc);

        gbc.gridy++;
        JTextField idField = new JTextField(15);
        add(new JLabel("아이디: "), gbc);
        gbc.gridx = 1; add(idField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JPasswordField pwField = new JPasswordField(15);
        add(new JLabel("비밀번호: "), gbc);
        gbc.gridx = 1; add(pwField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy++;
        JButton loginBtn = new JButton("접속하기");
        JButton backBtn = new JButton("돌아가기");

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(loginBtn); btnPanel.add(backBtn);
        add(btnPanel, gbc);

        // 이벤트
        backBtn.addActionListener(e -> app.switchPanel("MAIN"));
        
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());
            User user = UserDatabase.getInstance().login(id, pw);
            
            if (user != null) {
                app.setCurrentUser(user);
                idField.setText(""); pwField.setText("");
                errorLabel.setText(" ");
                app.switchPanel("MAIN");
            } else {
                errorLabel.setText("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        });

        // 텍스트 색상 통일
        for (Component c : this.getComponents()) {
            if (c instanceof JLabel) c.setForeground(Color.GREEN);
        }
    }
}