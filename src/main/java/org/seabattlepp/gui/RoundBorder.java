package org.seabattlepp.gui;

import java.awt.*;
import javax.swing.border.AbstractBorder;


public class RoundBorder extends AbstractBorder {

    public Color color;
    public int thickness;
    public int arcRadius;

    // Ініціалізація заокругленої рамки
    public RoundBorder(Color color, int thickness, int arcRadius) {
        this.color = color;
        this.thickness = thickness;
        this.arcRadius = arcRadius;
    }

    // Малювання заокругленої рамки
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int adjustedX = x + thickness / 2;
        int adjustedY = y + thickness / 2;
        int adjustedWidth = width - thickness;
        int adjustedHeight = height - thickness;

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));

        g2d.drawRoundRect(adjustedX, adjustedY, adjustedWidth, adjustedHeight, arcRadius, arcRadius);
        g2d.dispose();
    }

    // Повертання відступів
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
}
