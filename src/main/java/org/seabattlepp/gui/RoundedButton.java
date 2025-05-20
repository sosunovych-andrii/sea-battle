package org.seabattlepp.gui;

import javax.swing.*;
import java.awt.*;


public class RoundedButton extends JButton {

    public Color backgroundColor;
    public Color hoverColor;

    // Ініціалізація заокругленої кнопки
    public RoundedButton(String text, Color backgroundColor, Color hoverColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.hoverColor = hoverColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Inter", Font.BOLD, 20));
    }

    // Малювання заокругленої кнопки
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isRollover() ? hoverColor : backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

        super.paintComponent(g);
        g2.dispose();
    }
}
