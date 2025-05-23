package org.seabattlepp.mechanics.core;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import org.seabattlepp.gui.MainFrame;
import org.seabattlepp.gui.ShipButton;
import org.seabattlepp.model.Ship;


public class BoardRenderer {

    public MainFrame mainFrame;

    public BoardRenderer(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // Позначення промаху на кнопці
    public void markMissSymbol(ShipButton button) {
        button.setEnabled(false);
        button.setOpaque(false);
        markMissUI(button);
    }

    // Додавання символу промаху на кнопці
    private void markMissUI(ShipButton button) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                super.paint(g, c);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Inter", Font.BOLD, 70));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "•";
                int textWidth = fm.stringWidth(text);
                int x = (c.getWidth() - textWidth) / 2;

                int verticalOffset = 39;
                int y = c.getHeight() / 3 + verticalOffset;

                g2d.drawString(text, x, y);
            }
        });
    }


    // Обробка влучання по кораблю на дошці комп'ютера
    public boolean markHit(int row, int col, Ship ship) {
        boolean sunk = false;
        if (mainFrame.boardManager.computerShipButtons[row][col] != null) {
            ship.takeHit();
            sunk = ship.isSunk();
            if (sunk) {
                markComputerBoardSunkShip(ship);
            } else {
                markComputerBoardHit(mainFrame.boardManager.computerShipButtons[row][col]);
            }
        }
        return sunk;
    }

    // Додавання символу влучання на кнопці
    public void markHitSymbol(ShipButton button) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                super.paint(g, c);

                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Inter", Font.BOLD, 70));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "✖";
                int textWidth = fm.stringWidth(text);
                int x = (c.getWidth() - textWidth) / 2;

                int verticalOffset = 41;
                int y = c.getHeight() / 3 + verticalOffset;

                g2d.drawString(text, x, y);
            }
        });
    }

    // Позначення влучання на дошці комп'ютера
    public void markComputerBoardHit(ShipButton button) {
        button.setEnabled(false);
        button.setOpaque(false);
        markHitSymbol(button);
    }

    // Позначення влучання на дошці гравця
    public void markPlayerBoardHit(ShipButton button) {
        if (button.getText() == null || !button.getText().equals("⚓")) {
            button.setEnabled(false);
        }
        button.setOpaque(false);
        markHitSymbol(button);
    }

    // Обробка влучання по кораблю на дошці гравця
    public boolean markPlayerBoardHit(int row, int col, Ship ship) {
        if (!mainFrame.boardManager.isCellAvailableForShot(row, col) || mainFrame.boardManager.isComputerShotAt(row, col)) {
            return false;
        }

        mainFrame.boardManager.updateComputerShotRecord(row, col);

        boolean sunk = false;
        if (mainFrame.boardManager.playerShipButtons[row][col] != null) {
            ship.takeHit();
            sunk = ship.isSunk();
            if (sunk) {
                markPlayerBoardSunkShip(ship);
                for (int[] coord : ship.getCoordinates()) {
                    int r = coord[0];
                    int c = coord[1];
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int newRow = r + dr;
                            int newCol = c + dc;
                            if (newRow >= 1 && newRow <= 10 &&
                                    newCol >= 1 && newCol <= 10 &&
                                    !mainFrame.boardManager.isComputerShotAt(newRow, newCol)) {
                                mainFrame.boardManager.updateComputerShotRecord(newRow, newCol);
                            }
                        }
                    }
                }
            } else {
                markPlayerBoardHit(mainFrame.boardManager.playerShipButtons[row][col]);
            }
        }
        return sunk;
    }

    // Позначення промаху на дошці гравця
    public void markPlayerBoardMiss(int row, int col) {
        if (!mainFrame.boardManager.isCellAvailableForShot(row, col) ||
                mainFrame.boardManager.isComputerShotAt(row, col)) {
            return;
        }

        mainFrame.boardManager.updateComputerShotRecord(row, col);

        if (mainFrame.boardManager.playerShipButtons[row][col] != null) {
            ShipButton button = mainFrame.boardManager.playerShipButtons[row][col];
            if (button != null) {
                markMissSymbol(button);
                mainFrame.boardManager.updateComputerShotRecord(row, col);
            }
        }
    }

    // Позначення промаху на дошці комп'ютера
    public void markComputerBoardMiss(int row, int col) {
        ShipButton button = mainFrame.boardManager.computerShipButtons[row][col];
        if (button != null && button.isEnabled()) {
            markMissSymbol(button);
            mainFrame.boardManager.updatePlayerShotRecord(row, col);
        }
    }

    // Додавання символу потоплення на кнопці
    public void markSunkSymbol(ShipButton button) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 50, 50);

                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Inter", Font.BOLD, 50));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "☠";
                int textWidth = fm.stringWidth(text);
                int x = (c.getWidth() - textWidth) / 2;

                int verticalOffset = 20;
                int y = c.getHeight() / 2 + verticalOffset;

                g2d.drawString(text, x, y);
            }
        });
    }

    // Позначення потопленого корабля на дошці комп'ютера
    public void markComputerBoardSunkShip(Ship ship) {
        for (int[] coord : ship.getCoordinates()) {
            int row = coord[0];
            int col = coord[1];
            if (mainFrame.boardManager.computerShipButtons[row][col] != null) {
                markSunkSymbol(mainFrame.boardManager.computerShipButtons[row][col]);
                mainFrame.boardManager.computerShipButtons[row][col].setEnabled(false);
                mainFrame.boardManager.computerShipButtons[row][col].setOpaque(false);
                mainFrame.boardManager.computerShipButtons[row][col].setText(null);
            }
        }
        markSurroundingCellsAsMissComputerBoard(ship);
    }

    // Позначення потопленого корабля на дошці гравця
    public void markPlayerBoardSunkShip(Ship ship) {
        for (int[] coord : ship.getCoordinates()) {
            int row = coord[0];
            int col = coord[1];
            if (mainFrame.boardManager.playerShipButtons[row][col] != null) {
                markSunkSymbol(mainFrame.boardManager.playerShipButtons[row][col]);
                mainFrame.boardManager.playerShipButtons[row][col].setEnabled(false);
                mainFrame.boardManager.playerShipButtons[row][col].setOpaque(false);
                mainFrame.boardManager.playerShipButtons[row][col].setIcon(null);
            }
        }
        markSurroundingCellsAsMissPlayerBoard(ship);
    }

    // Позначення сусідніх клітинок як промахи на дошці комп'ютера після потоплення
    public void markSurroundingCellsAsMissComputerBoard(Ship ship) {
        Set<String> markedCells = new HashSet<>();

        for (int[] coord : ship.getCoordinates()) {
            int shipRow = coord[0];
            int shipCol = coord[1];

            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) continue;

                    int adjacentRow = shipRow + rowOffset;
                    int adjacentCol = shipCol + colOffset;

                    String cellKey = adjacentRow + "," + adjacentCol;

                    if (mainFrame.boardManager.isValidCell(adjacentRow, adjacentCol) && !markedCells.contains(cellKey)) {
                        if (mainFrame.boardManager.computerShipsLocations[adjacentRow][adjacentCol] == null &&
                                mainFrame.boardManager.computerShipButtons[adjacentRow][adjacentCol] != null) {
                            markMissSymbol(mainFrame.boardManager.computerShipButtons[adjacentRow][adjacentCol]);
                            mainFrame.boardManager.updatePlayerShotRecord(adjacentRow, adjacentCol);
                            markedCells.add(cellKey);
                        }
                    }
                }
            }
        }
    }

    // Позначення сусіднікх клітинок як промахи на дошці гравця
    public void markSurroundingCellsAsMissPlayerBoard(Ship ship) {
        Set<String> markedCells = new HashSet<>();

        for (int[] coord : ship.getCoordinates()) {
            int shipRow = coord[0];
            int shipCol = coord[1];

            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) continue;

                    int adjacentRow = shipRow + rowOffset;
                    int adjacentCol = shipCol + colOffset;

                    String cellKey = adjacentRow + "," + adjacentCol;

                    if (mainFrame.boardManager.isValidCell(adjacentRow, adjacentCol) && !markedCells.contains(cellKey)) {
                        if (mainFrame.boardManager.playerShipsLocations[adjacentRow][adjacentCol] == null &&
                                mainFrame.boardManager.playerShipButtons[adjacentRow][adjacentCol] != null) {
                            markMissSymbol(mainFrame.boardManager.playerShipButtons[adjacentRow][adjacentCol]);
                            mainFrame.boardManager.updateComputerShotRecord(adjacentRow, adjacentCol);
                            markedCells.add(cellKey);
                        }
                    }
                }
            }
        }
    }
}
