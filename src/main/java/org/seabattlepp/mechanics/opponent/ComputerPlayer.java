package org.seabattlepp.mechanics.opponent;

import javax.swing.*;

import org.seabattlepp.gui.MainFrame;
import org.seabattlepp.gui.ShipButton;
import org.seabattlepp.model.Ship;


public class ComputerPlayer {

    private final ComputerStrategy computerStrategy;
    public  MainFrame mainFrame;
    private Timer timer;

    public ComputerPlayer(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.computerStrategy = new ComputerStrategy();
    }

    public void resetBot() {
        computerStrategy.resetStrategy();
        stopTimer();
    }

    // точка входу в хід комп'ютера
    public void startComputerTurn() {
        if (!mainFrame.gameController.isGameStarted ||
                mainFrame.gameController.isGameEnded) {
        }
        mainFrame.gameController.setPlayerTurn(false);
        mainFrame.boardManager.disableComputerButtons();
        mainFrame.boardManager.disablePlayerButtons();

        int[] shotCoordinates = findComputerShotCoordinates();

        if (shotCoordinates == null) {
            mainFrame.gameController.startPlayerTurn();
            return;
        }

        stopTimer();

        timer = new Timer(1000, e -> {
            boolean hit = processComputerShot(shotCoordinates[0], shotCoordinates[1]);
            if (!hit && !mainFrame.gameController.isGameEnded) {
                mainFrame.gameController.setPlayerTurn(true);
                mainFrame.boardManager.enableComputerButtons();
            } else if (hit && mainFrame.gameController.isGameStarted &&
                    !mainFrame.gameController.isGameEnded &&
                    !mainFrame.gameController.isPlayerTurn) {
                startComputerTurn();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // обирає допустиму клітинку для пострілу
    private int[] findComputerShotCoordinates() {
        int[] coordinates;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;

        do {
            coordinates = computerStrategy.chooseShot(mainFrame.boardManager.playerShipButtons);
            attempts++;
            if (coordinates == null || attempts > MAX_ATTEMPTS) {
                return null;
            }

            // Перевіряємо, чи клітинка доступна через GameContoller
            boolean isAvailable = mainFrame.boardManager.isCellAvailableForShot(coordinates[0], coordinates[1]);
            boolean isNotShot = !mainFrame.boardManager.isComputerShotAt(coordinates[0], coordinates[1]);
            String cellText = mainFrame.boardManager.playerShipButtons[coordinates[0]][coordinates[1]] != null ?
                    mainFrame.boardManager.playerShipButtons[coordinates[0]][coordinates[1]].getText() : "null";

            if (!isAvailable || !isNotShot || "•".equals(cellText)) {
                coordinates = null; // Скидаємо координати для нової спроби
                continue;
            }

            // Перевіряємо валідність клітинки лише після всіх перевірок
            if (coordinates != null &&
                    !computerStrategy.isValidCell(coordinates[0], coordinates[1])) {
                coordinates = null; // Скидаємо, якщо клітинка невалідна
            }
        } while (coordinates == null);

        return coordinates;
    }

    // виконує постріл комп'ютера в гравця
    public boolean processComputerShot(int row, int col) {
        if (!computerStrategy.isValidCell(row, col)) {
            return false;
        }

        if (mainFrame.gameController.isGameEnded) {
            stopTimer();
            return false;
        }

        ShipButton button = mainFrame.boardManager.playerShipButtons[row][col];
        if (button == null) {
            return false;
        }

        // Додаткова перевірка перед пострілом
        if (!mainFrame.boardManager.isCellAvailableForShot(row, col)
                || mainFrame.boardManager.isComputerShotAt(row, col)) {
            if (!mainFrame.gameController.isGameEnded) {
                startComputerTurn(); // Повторна спроба лише якщо гра не закінчена
            }
            return false;
        }

        Ship ship = mainFrame.boardManager.playerShipsLocations[row][col];
        boolean hit = ship != null;
        boolean sunk = false;

        if (hit) {
            sunk = mainFrame.boardRenderer.markPlayerBoardHit(row, col, ship);
        } else {
            mainFrame.boardRenderer.markPlayerBoardMiss(row, col);
        }

        computerStrategy.processShotResult(new int[]{row, col}, hit, sunk);

        if (sunk && !mainFrame.gameController.isGameEnded) {
            mainFrame.gameController.checkGameEnd();
        }
        return hit;
    }

    // Зупиняє роботу таймера
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
}
