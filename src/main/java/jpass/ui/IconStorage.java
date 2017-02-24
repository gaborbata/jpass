package jpass.ui;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Icon storage for getting and caching image data from a favicon provider.
 *
 * @author Daniil Bubnov
 */
public class IconStorage {
    private static final String GET_ICON = "https://www.google.com/s2/favicons?domain=";
    private static final ImageIcon DEFAULT_ICON = new ImageIcon(IconStorage.class.getClassLoader().getResource("resources/images/keyring.png"));
    private static final String ICONS = "icons";
    private final Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

    private IconStorage() {
        if (!new File(ICONS).exists()) {
            new File(ICONS).mkdir();
        }
    }

    public static IconStorage newInstance() {
        return new IconStorage();
    }

    public synchronized ImageIcon getIcon(String url) {
        if (url == null) {
            return DEFAULT_ICON;
        }
        // check cache
        ImageIcon imageIcon = icons.get(url);
        if (imageIcon != null) {
            return imageIcon;
        }
        // check file
        File iconFile = new File(ICONS, url.hashCode() + ".png");
        if (iconFile.exists()) {
            imageIcon = new ImageIcon(iconFile.getAbsolutePath());
            icons.put(url, imageIcon);
            return imageIcon;
        }
        // get the icon, put in file and to cache
        try {
            String iconUrl = GET_ICON + url;
            imageIcon = new ImageIcon(new URL(iconUrl));
            Image image = imageIcon.getImage();
            BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            ImageIO.write(bi, "png", new File(ICONS, url.hashCode() + ".png"));
            icons.put(url, imageIcon);
        } catch (Exception e) {
            e.printStackTrace();
            // so, impossible happened...
            // we put a standard icon to cache.
            // note that on the next application run we will try to retrieve the icon again,
            // this will save us from occasional connection problems
            imageIcon = DEFAULT_ICON;
            icons.put(url, imageIcon);
        }
        return imageIcon;
    }
}
