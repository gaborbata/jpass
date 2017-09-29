package jpass.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URI;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static jpass.util.CryptUtils.getSha256Hash;
import static jpass.util.StringUtils.byteArrayToHex;

/**
 * Icon storage for getting and caching image data from a favicon provider.
 *
 * <p>
 * Currently Google S2 is used to retrieve favicons.
 * </p>
 *
 * @author Daniil Bubnov
 */
public class IconStorage {

    private static final Logger LOG = Logger.getLogger(IconStorage.class.getName());
    private static final String FAVICON_PROVIDER_URL_PATTERN = "https://www.google.com/s2/favicons?domain=%s";
    private static final ImageIcon DEFAULT_ICON = new ImageIcon(IconStorage.class.getClassLoader()
            .getResource("resources/images/keyring.png"));
    private static final String ICONS = "icons";
    private final Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
    private final boolean enabled;

    private IconStorage() {
        enabled = Configuration.getInstance().is("fetch.favicons.enabled", false);
        if (enabled && !new File(ICONS).exists()) {
            new File(ICONS).mkdir();
        }
    }

    public static IconStorage newInstance() {
        return new IconStorage();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public synchronized ImageIcon getIcon(String url) {
        if (!enabled) {
            return null;
        }
        // get domain
        String domain;
        try {
            domain = new URI(url).getHost();
        } catch (Exception e) {
            return DEFAULT_ICON;
        }
        if (domain == null) {
            return DEFAULT_ICON;
        }
        // check cache
        ImageIcon imageIcon = icons.get(domain);
        if (imageIcon != null) {
            return imageIcon;
        }
        // check file
        String iconFileName;
        try {
            iconFileName = byteArrayToHex(getSha256Hash(domain.toCharArray())) + ".png";
        } catch (Exception e) {
            return DEFAULT_ICON;
        }
        File iconFile = new File(ICONS, iconFileName);
        if (iconFile.exists()) {
            imageIcon = new ImageIcon(iconFile.getAbsolutePath());
            icons.put(domain, imageIcon);
            return imageIcon;
        }
        return getAndCacheIcon(domain, iconFileName);
    }

    private ImageIcon getAndCacheIcon(String domain, String iconFileName) {
        ImageIcon imageIcon;
        try {
            String iconUrl = String.format(FAVICON_PROVIDER_URL_PATTERN, domain);
            imageIcon = new ImageIcon(new URL(iconUrl));
            Image image = imageIcon.getImage();
            BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), TYPE_4BYTE_ABGR);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            ImageIO.write(bi, "png", new File(ICONS, iconFileName));
            icons.put(domain, imageIcon);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not get favicon.");
            // We put a standard icon to cache.
            // Note that on the next application run we will try to retrieve the icon again,
            // this will save us from occasional connection problems.
            imageIcon = DEFAULT_ICON;
            icons.put(domain, imageIcon);
        }
        return imageIcon;
    }
}
