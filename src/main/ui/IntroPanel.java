package main.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.SpaceMallApp;

public class IntroPanel extends JPanel {
    public IntroPanel(SpaceMallApp app) {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout()); // 화면 중앙 정렬을 위한 레이아웃

        // 컴포넌트들을 세로로 배치하기 위한 내부 패널 (3행 1열, 행간 여백 20)
        JPanel textContainer = new JPanel(new GridLayout(3, 1, 0, 20));
        textContainer.setBackground(Color.BLACK);
        textContainer.setOpaque(false); // 배경 투명화

        // 공통으로 사용할 폰트 설정 (크기 30, 볼드체)
        Font commonFont = new Font("Monospaced", Font.BOLD, 30);

        // 1. 첫 번째 줄: 쇼핑몰 이름
        JLabel titleLabel = new JLabel("<<우주 쇼핑몰>>", SwingConstants.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(commonFont);

        // 2. 두 번째 줄: 밑줄(<u> 태그 활용)이 적용된 서브 타이틀
        JLabel subTitleLabel = new JLabel("<html><u>※외계에서 온 멋진 물건※~!!</u></html>", SwingConstants.CENTER);
        subTitleLabel.setForeground(Color.CYAN);
        subTitleLabel.setFont(commonFont);

        // 3. 세 번째 줄: 클릭 시 쇼핑몰 메인으로 이동하는 텍스트 버튼
        JLabel enterLabel = new JLabel(">> 나는 물건을 본다", SwingConstants.CENTER);
        enterLabel.setForeground(Color.WHITE);
        enterLabel.setFont(commonFont);
        enterLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 오버 시 손가락 모양 변경

        // 입장 버튼에 마우스 클릭 이벤트 추가
        enterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                app.switchPanel("MAIN"); // 메인 화면으로 전환
            }
        });

        // 세로 컨테이너 패널에 순서대로 추가
        textContainer.add(titleLabel);
        textContainer.add(subTitleLabel);
        textContainer.add(enterLabel);

        // 최종적으로 메인 IntroPanel의 중앙에 배치
        add(textContainer);
    }
}