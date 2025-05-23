package org.seabattlepp.gui;

import javax.swing.*;
import java.awt.*;

import org.seabattlepp.mechanics.opponent.ComputerPlayer;
import org.seabattlepp.mechanics.core.GameController;
import org.seabattlepp.mechanics.core.BoardManager;
import org.seabattlepp.mechanics.core.BoardRenderer;


public class MainFrame extends JFrame {

    // Кнопки
    public RoundedButton randomButton;
    public JButton startButton;
    public JButton resetButton;

    // Панельки
    public BoardPanel boardPanel;
    public ShipPanel shipPanel;
    public ButtonPanel buttonPanel;

    // Логіка гри
    public GameController gameController;
    public BoardManager boardManager;
    public BoardRenderer boardRenderer;
    public ComputerPlayer computerPlayer;


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

        // Додаємо логіку гри
        boardManager = new BoardManager(boardPanel.computerShipButtons, boardPanel.playerShipButtons);
        computerPlayer = new ComputerPlayer(this);
        boardRenderer = new BoardRenderer(this);
        gameController = new GameController(this);

        // Налаштування кнопки "Рандом"
        if (randomButton != null) {
            randomButton.setEnabled(false);
            randomButton.addActionListener(e -> {
                boardManager.placeShipsRandomlyOnLeftBoard();
                boardManager.enableShootingAfterRandom();
                randomButton.setEnabled(true);
            });
        }

        // Налаштування кнопки "Старт"
        if (startButton != null) {
            startButton.addActionListener(e -> {
                if (!gameController.isGameStarted) {
                    boardManager.placeShipsRandomlyOnRightBoard();
                    if (randomButton != null) {
                        randomButton.setEnabled(true);
                    }
                    gameController.startGame();
                    startButton.setEnabled(false);
                }
            });
        }

        // Налаштування кнопки "Скинути"
        if (resetButton != null) {
            resetButton.addActionListener(e -> {
                gameController.resetBoards();
                if (randomButton != null) {
                    randomButton.setEnabled(false);
                }
                startButton.setEnabled(true);
            });
        }

        // Додатково деактивуємо "Рандом", якщо гра ще не почалась
        if (!gameController.isGameStarted) {
            randomButton.setEnabled(false);
        }
    }
}
