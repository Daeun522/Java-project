package main.ui;

import java.awt.*;
import javax.swing.*;
import main.SpaceMallApp;
import main.db.UserDatabase;
import main.model.User;

public class LoginPanel extends JPanel {
    private SpaceMallApp app;
    
    // [추가] 로그인 실패 횟수 저장 변수
    private int loginFailCount = 0; 
    private final int MAX_FAIL_COUNT = 5;

    public LoginPanel(SpaceMallApp app) {
        this.app = app;
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout()); // 중앙 배치
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("[ 외계 상점 로그인 ]");
        title.setForeground(Color.GREEN);
        add(title, gbc);

        gbc.gridy++;
        JTextField idField = new JTextField(15);
        add(new JLabel("ID: "), gbc);
        gbc.gridx = 1; add(idField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JPasswordField pwField = new JPasswordField(15);
        add(new JLabel("password: "), gbc);
        gbc.gridx = 1; add(pwField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy++;
        JButton loginBtn = new JButton("접속하다");
        JButton backBtn = new JButton("돌아가다");

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.BLACK);
        btnPanel.add(loginBtn); btnPanel.add(backBtn);
        add(btnPanel, gbc);

        // 이벤트
        backBtn.addActionListener(e -> {
            loginFailCount = 0; // 돌아갈 때 카운트 초기화 (선택사항)
            app.switchPanel("MAIN");
        });
        
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());
            User user = UserDatabase.getInstance().login(id, pw);
            
            if (user != null) {
                // 로그인 성공
                app.setCurrentUser(user);
                idField.setText(""); pwField.setText("");
                errorLabel.setText(" ");
                loginFailCount = 0; // 성공 시 카운트 초기화
                app.switchPanel("MAIN");
            } else {
                // [수정] 로그인 실패 로직
                loginFailCount++;
                
                if (loginFailCount >= MAX_FAIL_COUNT) {
                    JOptionPane.showMessageDialog(this, "비정상적인 로그인이 감지되었습니다. 서버를 종료합니다.", 
                                                "경고", JOptionPane.ERROR_MESSAGE);
                    System.exit(0); // 프로그램 강제 종료
                } else {
                    errorLabel.setText("ID or PW incorrect! (" + loginFailCount + "/" + MAX_FAIL_COUNT + ")");
                }
            }
        });

        // 텍스트 색상 통일
        for (Component c : this.getComponents()) {
            if (c instanceof JLabel) c.setForeground(Color.GREEN);
        }
    }
}