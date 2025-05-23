package org.seabattlepp.mechanics.opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.seabattlepp.gui.MainFrame;
import org.seabattlepp.gui.ShipButton;
import org.seabattlepp.mechanics.core.GameController;


class ComputerStrategy {
    private Random random = new Random();
    private Mode currentMode = Mode.RANDOM;
    private List<int[]> hits = new ArrayList<>();
    private int[][] targetedArea = new int[11][11];
    private List<int[]> availableShots = new ArrayList<>();
    private GameController gameController;
    private MainFrame mainFrame;

    public enum Mode {
        RANDOM, HUNT, TARGET
    }

    // метод для вибору пострілу
    public int[] chooseShot(ShipButton[][] playerButtons) {
        updateAvailableShots(playerButtons);

        if (availableShots.isEmpty()) {
            return null;
        }

        int[] shot = null;
        int attempts = 0;
        final int maxAttempts = 100;

        while (shot == null && attempts < maxAttempts) {
            switch (currentMode) {
                case RANDOM:
                    shot = getRandomShot();
                    break;
                case HUNT:
                    shot = getHuntShot();
                    break;
                case TARGET:
                    shot = getTargetShot();
                    break;
                default:
                    shot = getRandomShot();
            }

            if (shot != null &&
                    (isAlreadyShot(shot[0], shot[1]) ||
                            isMissSymbol(playerButtons, shot[0], shot[1]))) {
                shot = null; // Ігноруємо, якщо клітинка вже використана або містить "•"
            }

            // Додаткова перевірка через GameContoller, якщо він доступний
            if (shot != null && gameController != null &&
                    mainFrame.boardManager.isComputerShotAt(shot[0], shot[1])) {
                shot = null;
            }

            attempts++;
        }

        if (shot != null && isValidCell(shot[0], shot[1])
                && !isAlreadyShot(shot[0], shot[1]) &&
                !isMissSymbol(playerButtons, shot[0], shot[1])) {
            recordShot(shot[0], shot[1]);
            return shot;
        }
        return null;
    }

    /*
    Методи вибору пострілу:
     */
    private int[] getRandomShot() {
        if (availableShots.isEmpty()) {
            return null;
        }
        int index = random.nextInt(availableShots.size());
        return availableShots.get(index);
    }

    private int[] getHuntShot() {
        if (hits.isEmpty()) {
            currentMode = Mode.RANDOM;
            return getRandomShot();
        }

        for (int[] hit : hits) {
            int row = hit[0];
            int col = hit[1];
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1} // Вгору, вниз, ліворуч, праворуч
            };
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isValidCell(newRow, newCol) && !isAlreadyShot(newRow, newCol)) {
                    return new int[]{newRow, newCol};
                }
            }
        }

        currentMode = Mode.RANDOM;
        return getRandomShot();
    }

    private int[] getTargetShot() {
        if (hits.size() < 2) {
            currentMode = Mode.HUNT;
            return getHuntShot();
        }

        int[] firstHit = hits.get(0);
        int[] lastHit = hits.get(hits.size() - 1);
        boolean isHorizontal = firstHit[0] == lastHit[0];

        int row = lastHit[0];
        int col = lastHit[1];
        int step = isHorizontal ? 1 : 0;
        int dir = (lastHit[step] > firstHit[step]) ? 1 : -1;
        int newRow = row + (isHorizontal ? 0 : dir);
        int newCol = col + (isHorizontal ? dir : 0);

        if (isValidCell(newRow, newCol) && !isAlreadyShot(newRow, newCol)) {
            return new int[]{newRow, newCol};
        }

        newRow = row + (isHorizontal ? 0 : -dir);
        newCol = col + (isHorizontal ? -dir : 0);
        if (isValidCell(newRow, newCol) && !isAlreadyShot(newRow, newCol)) {
            return new int[]{newRow, newCol};
        }

        currentMode = Mode.HUNT;
        return getHuntShot();
    }

    // реакція на результат пострілу
    public void processShotResult(int[] coordinates, boolean hit, boolean sunk) {
        int row = coordinates[0];
        int col = coordinates[1];
        recordShot(row, col);

        if (hit && !sunk) {
            hits.add(coordinates);
            if (hits.size() == 1) {
                currentMode = Mode.HUNT;
            } else if (hits.size() >= 2) {
                boolean inLine = false;
                if (hits.size() >= 2) {
                    int firstRow = hits.get(0)[0];
                    int firstCol = hits.get(0)[1];
                    boolean allSameRow = true, allSameCol = true;
                    for (int[] h : hits) {
                        if (h[0] != firstRow) allSameRow = false;
                        if (h[1] != firstCol) allSameCol = false;
                    }
                    inLine = allSameRow || allSameCol;
                }
                currentMode = inLine ? Mode.TARGET : Mode.HUNT;
            }
        } else if (sunk) {
            for (int[] h : hits) {
                int r = h[0];
                int c = h[1];
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int newRow = r + dr;
                        int newCol = c + dc;
                        if (isValidCell(newRow, newCol) && !isAlreadyShot(newRow, newCol)) {
                            recordShot(newRow, newCol);
                        }
                    }
                }
            }
            hits.clear();
            currentMode = Mode.RANDOM;
        } else {
            if (currentMode == Mode.TARGET) {
                currentMode = Mode.HUNT;
            } else if (currentMode == Mode.HUNT && hits.isEmpty()) {
                currentMode = Mode.RANDOM;
            }
        }
    }

    // оновлює список доступних клітинок у які комп'ютер може стріляти
    private void updateAvailableShots(ShipButton[][] playerButtons) {
        availableShots.clear();
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                if (playerButtons[i][j] != null && !isAlreadyShot(i, j) &&
                        !isMissSymbol(playerButtons, i, j)) {
                    if (gameController == null ||
                            (!mainFrame.boardManager.isComputerShotAt(i, j) &&
                                    mainFrame.boardManager.isCellAvailableForShot(i, j))) {
                        availableShots.add(new int[]{i, j});
                    }
                }
            }
        }

    }

    // перевіряє чи є в клітинці символ, який позначає промах
    private boolean isMissSymbol(ShipButton[][] playerButtons, int row, int col) {
        ShipButton button = playerButtons[row][col];
        return button != null && "•".equals(button.getText());
    }

    // перевіряє чи в клітинку вже попадали
    private boolean isAlreadyShot(int row, int col) {
        return targetedArea[row][col] == 1;
    }

    // позначання клітики обстріляною
    private void recordShot(int row, int col) {
        if (isValidCell(row, col)) {
            targetedArea[row][col] = 1;
            if (gameController != null) {
                if (isValidCell(row, col)) {
                    mainFrame.boardManager.computerTargetedArea[row][col] = 1;
                }
            }
        }
    }

    // перевірка чи належить координата ігровому полю
    public boolean isValidCell(int row, int col) {
        return row >= 1 && row <= 10 && col >= 1 && col <= 10;
    }

    // скидає стан стратегії перед новою грою
    public void resetStrategy() {
        hits.clear();
        currentMode = Mode.RANDOM;
        for (int i = 0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {
                targetedArea[i][j] = 0;
            }
        }
        availableShots.clear();
    }
}
//
