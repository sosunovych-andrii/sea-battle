package org.seabattlepp.gui;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    // Кнопки
    public RoundedButton randomButton;
    public JButton startButton;
    public JButton resetButton;

    // Панельки
    public BoardPanel boardPanel;
    public ShipPanel shipPanel;
    public ButtonPanel buttonPanel;

    public MainFrame() {
        setTitle("Морський Бій");

        // 🖼 Спроба завантажити іконку гри
        try {
            Image image = Toolkit.getDefaultToolkit().getImage("src/main/java/org/seabattlepp/gui/img/icon.png");
            setIconImage(image);
        } catch (Exception e) {
            System.err.println("помилка завантаження іконки: " + e.getMessage());
        }

        // 🔧 Стандартні налаштування JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        // Основна панель контенту з відступами
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // Створюємо панелі
        boardPanel = new BoardPanel(this);
        shipPanel = new ShipPanel();
        buttonPanel = new ButtonPanel();

        // Кнопки
        startButton = buttonPanel.getStartButton();
        resetButton = buttonPanel.getResetButton();

        // Шукаємо кнопку "Рандом"
        for (Component comp : shipPanel.getComponents()) {
            if (comp instanceof RoundedButton && ((RoundedButton) comp).getText().equals("Рандом")) {
                randomButton = (RoundedButton) comp;
                break;
            }
        }

        // Додаємо панелі на головне вікно
        add(boardPanel, BorderLayout.CENTER);
        add(shipPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        // Налаштування розміру вікна
        pack();
        setLocationRelativeTo(null);
    }
}
