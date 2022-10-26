/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jpass.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * @author Karl Tauber
 * @author Gabor_Bata
 */
public class SvgImageIcon extends ImageIcon {

    private static final Logger LOG = Logger.getLogger(SvgImageIcon.class.getName());

    // use own SVG universe so that it can not be cleared from anywhere
    private static final SVGUniverse SVG_UNIVERSE = new SVGUniverse();

    private final String name;
    private final int width;
    private final int height;
    private SVGDiagram diagram;
    private boolean dark;

    public SvgImageIcon(String name) {
        this(name, 0, 0);
    }

    public SvgImageIcon(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    private void update() {
        if (dark == isDarkLaf() && diagram != null) {
            return;
        }

        dark = isDarkLaf();
        URL url = getIconURL(name, dark);
        if (url == null && dark) {
            url = getIconURL(name, false);
        }
        // load/get image
        try {
            diagram = SVG_UNIVERSE.getDiagram(url.toURI());
        } catch (Exception ex) {
            LOG.log(Level.WARNING, String.format("Could not get SVG image [%s] due to [%s]", name, ex.getMessage()));
        }
    }

    private URL getIconURL(String name, boolean dark) {
        if (dark) {
            int dotIndex = name.lastIndexOf('.');
            name = name.substring(0, dotIndex) + "_dark" + name.substring(dotIndex);
        }
        return SvgImageIcon.class.getClassLoader().getResource(name);
    }

    @Override
    public int getIconWidth() {
        if (width > 0) {
            return UIScale.scale(width);
        }
        update();
        return (int) UIScale.scale(diagram != null ? diagram.getWidth() : 16);
    }

    @Override
    public int getIconHeight() {
        if (height > 0) {
            return UIScale.scale(height);
        }
        update();
        return (int) UIScale.scale(diagram != null ? diagram.getHeight() : 16);
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        update();

        Rectangle clipBounds = g.getClipBounds();
        if (clipBounds != null && !clipBounds.intersects(new Rectangle(x, y, getIconWidth(), getIconHeight()))) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            FlatUIUtils.setRenderingHints(g2);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            paintSvg(g2, x, y);
        } finally {
            g2.dispose();
        }
    }

    private void paintSvg(Graphics2D g, int x, int y) {
        if (diagram == null) {
            paintSvgError(g, x, y);
            return;
        }

        g.translate(x, y);

        // add +1 clip width/height to properly display anti-aliased edges
        g.clipRect(0, 0, getIconWidth() + 1, getIconHeight() + 1);

        UIScale.scaleGraphics(g);

        if (width > 0 || height > 0) {
            double sx = width > 0 ? width / diagram.getWidth() : 1;
            double sy = height > 0 ? height / diagram.getHeight() : 1;
            if (sx != 1 || sy != 1) {
                g.scale(sx, sy);
            }
        }
        diagram.setIgnoringClipHeuristic(true);

        try {
            diagram.render(g);
        } catch (SVGException ex) {
            paintSvgError(g, 0, 0);
        }
    }

    private void paintSvgError(Graphics2D g, int x, int y) {
        g.setColor(Color.red);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
    }

    @Override
    public Image getImage() {
        update();

        BufferedImage image = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            paintIcon(null, g, 0, 0);
        } finally {
            g.dispose();
        }
        return image;
    }

    private static Boolean darkLaf;

    private static boolean isDarkLaf() {
        if (darkLaf == null) {
            lafChanged();
            UIManager.addPropertyChangeListener(evt -> lafChanged());
        }
        return darkLaf;
    }

    private static void lafChanged() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        darkLaf = (lookAndFeel instanceof FlatLaf && ((FlatLaf) lookAndFeel).isDark());
    }
}
