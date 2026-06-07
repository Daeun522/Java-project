package main.ui;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import main.SpaceMallApp;

public class ReviewPanel extends JPanel {
    private SpaceMallApp app;
    private Image backgroundImage;
    
    private JComboBox<String> scoreCombo;
    private JTextArea reviewInputField;
    private JPanel reviewListContainer;
    private JScrollPane scrollPane;

    public ReviewPanel(SpaceMallApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));

        // 우주 이미지 배경 로딩
        try {
            backgroundImage = new ImageIcon("background.png").getImage();
        } catch (Exception e) {
            System.out.println("리뷰 배경 로드 오류: " + e.getMessage());
        }

        // ----------------------------------------------------
        // 1. 헤더 구역 (상단 제목 및 뒤로가기 버튼)
        // ----------------------------------------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 20, 20, 220));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("☄️ 우주 전송 센터 - 생생 리뷰 ☄️");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setFont(new Font("Gulim", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("상점으로 복귀");
        backBtn.setBackground(Color.BLACK);
        backBtn.setForeground(Color.CYAN);
        backBtn.setFont(new Font("Gulim", Font.BOLD, 12));
        backBtn.addActionListener(e -> app.switchPanel("MAIN"));
        headerPanel.add(backBtn, BorderLayout.EAST);

        // ----------------------------------------------------
        // 2. 작성 구역 (별점 선택 + 내용 작성 칸)
        // ----------------------------------------------------
        JPanel writeContainer = new JPanel();
        writeContainer.setLayout(new BoxLayout(writeContainer, BoxLayout.Y_AXIS));
        writeContainer.setBackground(new Color(40, 40, 40, 200));
        writeContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.CYAN), "외계 지구 물품 소감 작성하기",
            0, 0, new Font("Gulim", Font.BOLD, 13), Color.CYAN));

        // 별점 줄
        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreRow.setOpaque(false);
        JLabel scoreLabel = new JLabel("만족도 조사(별점): ");
        scoreLabel.setForeground(Color.WHITE);
        
        String[] scores = {"★★★★★ (5점)", "★★★★☆ (4점)", "★★★☆☆ (3점)", "★★☆☆☆ (2점)", "★☆☆☆☆ (1점)"};
        scoreCombo = new JComboBox<>(scores);
        scoreRow.add(scoreLabel);
        scoreRow.add(scoreCombo);

        // 내용 입력 칸 줄
        JPanel textRow = new JPanel(new BorderLayout(5, 5));
        textRow.setOpaque(false);
        textRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        reviewInputField = new JTextArea(3, 40);
        reviewInputField.setLineWrap(true);
        reviewInputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(reviewInputField);
        
        JButton submitBtn = new JButton("리뷰 등록");
        submitBtn.setBackground(Color.BLACK);
        submitBtn.setForeground(Color.GREEN);
        submitBtn.setFont(new Font("Gulim", Font.BOLD, 14));
        
        // 리뷰 저장 리스너
        submitBtn.addActionListener(e -> saveReviewToFile());

        textRow.add(inputScroll, BorderLayout.CENTER);
        textRow.add(submitBtn, BorderLayout.EAST);

        writeContainer.add(scoreRow);
        writeContainer.add(textRow);

        // 상단에 헤더와 입력 컨테이너 결합 배치
        JPanel northWrapper = new JPanel(new BorderLayout(0, 10));
        northWrapper.setOpaque(false);
        northWrapper.add(headerPanel, BorderLayout.NORTH);
        northWrapper.add(writeContainer, BorderLayout.CENTER);

        add(northWrapper, BorderLayout.NORTH);

        // ----------------------------------------------------
        // 3. 리뷰 출력 리스트 영역 (CENTER)
        // ----------------------------------------------------
        reviewListContainer = new JPanel();
        reviewListContainer.setLayout(new BoxLayout(reviewListContainer, BoxLayout.Y_AXIS));
        reviewListContainer.setOpaque(false);

        scrollPane = new JScrollPane(reviewListContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "전 우주 연동 소감 목록",
            0, 0, new Font("Gulim", Font.BOLD, 13), Color.WHITE));

        add(scrollPane, BorderLayout.CENTER);

        // 파일 데이터 불러와서 화면 구성
        loadReviewsFromFile();
    }

    // [핵심] review.txt 에 입력값 추가 및 동적 전송 저장 로직
    private void saveReviewToFile() {
        if (app.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "로그인한 외계인만 작성 권한이 있습니다.");
            return;
        }

        String content = reviewInputField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "소감 내용을 입력해 주십시오.");
            return;
        }

        String userId = app.getCurrentUser().getId();
        String selectedStar = (String) scoreCombo.getSelectedItem();
        // 개행 문자로 데이터 파싱 손상을 막기 위해 공백으로 치환 처리
        content = content.replace("\n", " "); 

        // 파일에 이어쓰기 (append=true)
        try (PrintWriter writer = new PrintWriter(new FileWriter("review.txt", true))) {
            writer.println(userId + "||" + selectedStar + "||" + content);
            reviewInputField.setText(""); // 입력 초기화
            JOptionPane.showMessageDialog(this, "지구국에 리뷰가 성공적으로 보존되었습니다.");
            
            loadReviewsFromFile(); // 화면 즉시 갱신
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "리뷰 디스크 쓰기 에러: " + e.getMessage());
        }
    }

    // [핵심] review.txt 파일을 읽어서 스크롤 화면 패널 리스트에 그려주기
    public void loadReviewsFromFile() {
        reviewListContainer.removeAll();
        File file = new File("review.txt");

        if (!file.exists()) {
            JLabel noReview = new JLabel("아직 수신된 전 우주적 리뷰가 존재하지 않습니다.", SwingConstants.CENTER);
            noReview.setForeground(Color.YELLOW);
            noReview.setFont(new Font("Gulim", Font.BOLD, 15));
            noReview.setAlignmentX(Component.CENTER_ALIGNMENT);
            reviewListContainer.add(Box.createVerticalStrut(30));
            reviewListContainer.add(noReview);
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\\|\\|");
                    if (tokens.length >= 3) {
                        reviewListContainer.add(createReviewCard(tokens[0], tokens[1], tokens[2]));
                        reviewListContainer.add(Box.createVerticalStrut(8));
                    }
                }
            } catch (IOException e) {
                System.out.println("리뷰 데이터 로드 실패: " + e.getMessage());
            }
        }

        reviewListContainer.revalidate();
        reviewListContainer.repaint();
    }

    // 리뷰 개별 상자 컴포넌트 찍어내기
    private JPanel createReviewCard(String user, String star, String text) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        card.setBackground(new Color(255, 255, 255, 220));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75)); // 가로 유연, 세로 고정

        JLabel userMetaLabel = new JLabel("🛸 작성원 ID: " + user + "   |   평가: " + star);
        userMetaLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        userMetaLabel.setForeground(new Color(0, 100, 150));

        JLabel contentLabel = new JLabel(text);
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentLabel.setForeground(Color.BLACK);

        card.add(userMetaLabel, BorderLayout.NORTH);
        card.add(contentLabel, BorderLayout.CENTER);

        return card;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}