import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShopWindow extends JFrame {
    private static final Color BG_TOP = new Color(0x2B3338);
    private static final Color BG_BOTTOM = new Color(0x10171B);
    private static final Color PANEL = new Color(0x1C2428);
    private static final Color CARD = new Color(0x3A4247);
    private static final Color CARD_HOVER = new Color(0x4B565C);
    private static final Color TEAL = new Color(0x3DD6B0);
    private static final Color TEAL_FILL = new Color(0x2E8F7D);
    private static final Color TEAL_DARK = new Color(0x2C6E64);
    private static final Color TEXT = new Color(0xECE8E1);
    private static final Color MUTED = new Color(0x9AA5A8);
    private static final Color GOLD = new Color(0xF2E98E);

    private enum Slot { SIDEARM, PRIMARY, ARMOR }

    private static final String[][] COLUMNS = {
        {"Sidearm"}, {"SMGs", "Shotguns"}, {"Rifles"}, {"Sniper", "Heavy"}, {"Armor"}
    };

    private final Player player;
    private final JLabel creditsLabel = new JLabel();
    private final JLabel loadoutLabel = new JLabel();
    private final List<WeaponCard> cards = new ArrayList<>();
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final double MAX_SCALE = 1.0; // 1.0 = never enlarge past the PNG's real size
    private final Map<String, BufferedImage> scaledCache = new HashMap<>();
    private final DetailPanel detail = new DetailPanel();

    public ShopWindow(Player player) {
        this.player = player;

        setTitle("VALORANT - Buy Phase");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Backdrop root = new Backdrop();
        root.setLayout(new BorderLayout(16, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));
        root.add(buildPlayerPanel(), BorderLayout.WEST);
        root.add(buildShop(), BorderLayout.CENTER);
        root.add(detail, BorderLayout.EAST);
        root.add(buildHint(), BorderLayout.SOUTH);
        setContentPane(root);

        refresh();
        setVisible(true);
    }


    private JPanel buildPlayerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL_DARK, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        panel.setPreferredSize(new Dimension(250, 0));

        JLabel creditsCaption = new JLabel("CREDITS");
        creditsCaption.setFont(font(Font.BOLD, 13));
        creditsCaption.setForeground(MUTED);

        creditsLabel.setFont(font(Font.BOLD, 36));
        creditsLabel.setForeground(GOLD);

        JLabel loadoutCaption = new JLabel("LOADOUT");
        loadoutCaption.setFont(font(Font.BOLD, 13));
        loadoutCaption.setForeground(MUTED);

        loadoutLabel.setFont(font(Font.BOLD, 15));
        loadoutLabel.setForeground(TEXT);

        panel.add(creditsCaption);
        panel.add(creditsLabel);
        panel.add(Box.createVerticalStrut(24));
        panel.add(loadoutCaption);
        panel.add(Box.createVerticalStrut(8));
        panel.add(loadoutLabel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildShop() {
        Map<String, List<Weapon>> catalog = createCatalog();
        JPanel shop = new JPanel(new GridLayout(1, COLUMNS.length, 10, 0));
        shop.setOpaque(false);

        for (String[] group : COLUMNS) {
            JPanel column = new JPanel(new GridBagLayout());
            column.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.BOTH;

            for (int i = 0; i < group.length; i++) {
                List<Weapon> weapons = catalog.get(group[i]);
                gbc.gridy = i;
                gbc.weighty = weapons.size();
                gbc.insets = new Insets(i == 0 ? 0 : 10, 0, 0, 0);
                column.add(buildSection(group[i], weapons), gbc);
            }
            shop.add(column);
        }
        return shop;
    }

    private JPanel buildSection(String category, List<Weapon> weapons) {
        JPanel section = new JPanel(new BorderLayout(0, 4));
        section.setOpaque(false);

        JLabel header = new JLabel(category.toUpperCase(), SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(TEAL_DARK);
        header.setForeground(TEXT);
        header.setFont(font(Font.BOLD, 13));
        header.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        section.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(weapons.size(), 1, 0, 6));
        grid.setOpaque(false);
        for (Weapon weapon : weapons) {
            WeaponCard card = new WeaponCard(weapon);
            cards.add(card);
            grid.add(card);
        }
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JLabel buildHint() {
        JLabel hint = new JLabel("Click to buy, click again to sell. One sidearm, one primary, one armor: "
                + "buying another in the same slot sells the old one.");
        hint.setFont(font(Font.PLAIN, 12));
        hint.setForeground(MUTED);
        return hint;
    }


    private Map<String, List<Weapon>> createCatalog() {
        Map<String, List<Weapon>> c = new LinkedHashMap<>();

        addWeapon(c, "Sidearm", "Classic", 0);
        addWeapon(c, "Sidearm", "Shorty", 300);
        addWeapon(c, "Sidearm", "Frenzy", 450);
        addWeapon(c, "Sidearm", "Ghost", 500);
        addWeapon(c, "Sidearm", "Bandit", 600);
        addWeapon(c, "Sidearm", "Sheriff", 800);

        addWeapon(c, "SMGs", "Stinger", 1100);
        addWeapon(c, "SMGs", "Spectre", 1600);

        addWeapon(c, "Shotguns", "Bucky", 850);
        addWeapon(c, "Shotguns", "Judge", 1850);

        addWeapon(c, "Rifles", "Bulldog", 2050);
        addWeapon(c, "Rifles", "Guardian", 2250);
        addWeapon(c, "Rifles", "Phantom", 2900);
        addWeapon(c, "Rifles", "Vandal", 2900);

        addWeapon(c, "Sniper", "Marshal", 950);
        addWeapon(c, "Sniper", "Outlaw", 2400);
        addWeapon(c, "Sniper", "Operator", 4700);

        addWeapon(c, "Heavy", "Ares", 1600);
        addWeapon(c, "Heavy", "Odin", 3200);

        addWeapon(c, "Armor", "Light Shields", 400);
        addWeapon(c, "Armor", "Heavy Shields", 1000);

        return c;
    }

    private void addWeapon(Map<String, List<Weapon>> catalog, String category, String name, int price) {
        catalog.computeIfAbsent(category, k -> new ArrayList<>())
               .add(new Weapon(name, price, category));
    }


    private static Slot slotOf(Weapon weapon) {
        String category = weapon.getCategory();
        if (category.equals("Sidearm")) return Slot.SIDEARM;
        if (category.equals("Armor")) return Slot.ARMOR;
        return Slot.PRIMARY;
    }

    private Weapon findInSlot(Slot slot) {
        for (Weapon owned : player.getLoadouts()) {
            if (slotOf(owned) == slot) return owned;
        }
        return null;
    }

    private boolean isOwned(Weapon weapon) {
        return player.getLoadouts().contains(weapon);
    }

    private boolean canAfford(Weapon weapon) {
        Weapon replaced = findInSlot(slotOf(weapon));
        int refund = (replaced == null) ? 0 : replaced.getPrice();
        return player.getCredits() + refund >= weapon.getPrice();
    }

    private void onClick(Weapon weapon) {
        if (isOwned(weapon)) {
            player.sellWeapon(weapon);
            refresh();
            return;
        }

        Weapon replaced = findInSlot(slotOf(weapon));
        if (replaced != null) {
            player.sellWeapon(replaced);
        }
        if (!player.buyWeapon(weapon)) {
            if (replaced != null) {
                player.buyWeapon(replaced);
            }
            JOptionPane.showMessageDialog(this,
                    "Not enough credits for " + weapon.getName() + ".",
                    "Insufficient credits", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refresh();
    }


    private void refresh() {
        creditsLabel.setText("\u00A4 " + String.format("%,d", player.getCredits()));
        loadoutLabel.setText("<html>"
                + loadoutLine("SIDEARM", Slot.SIDEARM) + "<br>"
                + loadoutLine("PRIMARY", Slot.PRIMARY) + "<br>"
                + loadoutLine("ARMOR", Slot.ARMOR)
                + "</html>");
        for (WeaponCard card : cards) {
            card.repaint();
        }
        detail.repaint();
    }

    private String loadoutLine(String label, Slot slot) {
        Weapon weapon = findInSlot(slot);
        return label + "&nbsp;&nbsp;&nbsp;" + (weapon == null ? "-" : weapon.getName().toUpperCase());
    }

    private BufferedImage imageFor(Weapon weapon) {
        String key = weapon.getName().toLowerCase().replace(' ', '_');
        if (!imageCache.containsKey(key)) {
            BufferedImage image = null;
            String[] folders = {
                "images/",
                "oop-val/images/",
                "Java Projects/oop-val/images/"
            };
            for (String folder : folders) {
                File file = new File(folder + key + ".png");
                if (file.exists()) {
                    try {
                        image = trim(ImageIO.read(file));
                    } catch (Exception ignored) {
                        // unreadable file: fall back to text
                    }
                    break;
                }
            }
            imageCache.put(key, image);
        }
        return imageCache.get(key);
    }

    private void drawScaled(Graphics2D g2, BufferedImage image, int x, int y, int maxW, int maxH) {
        double scale = Math.min(MAX_SCALE,
                Math.min((double) maxW / image.getWidth(), (double) maxH / image.getHeight()));
        int w = Math.max(1, (int) (image.getWidth() * scale));
        int h = Math.max(1, (int) (image.getHeight() * scale));

        BufferedImage drawn = (scale < 1.0) ? scaled(image, w, h) : image;
        g2.drawImage(drawn, x + (maxW - w) / 2, y + (maxH - h) / 2, w, h, null);
    }

    private static BufferedImage trim(BufferedImage src) {
        if (!src.getColorModel().hasAlpha()) return src;

        int minX = src.getWidth(), minY = src.getHeight(), maxX = -1, maxY = -1;
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int alpha = (src.getRGB(x, y) >>> 24) & 0xFF;
                if (alpha > 20) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < 0) return src; // fully transparent, leave it alone
        return src.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private BufferedImage scaled(BufferedImage src, int targetW, int targetH) {
        String key = System.identityHashCode(src) + "@" + targetW + "x" + targetH;
        BufferedImage cached = scaledCache.get(key);
        if (cached != null) return cached;
        if (scaledCache.size() > 300) scaledCache.clear();

        BufferedImage current = src;
        int w = src.getWidth();
        int h = src.getHeight();
        while (w > targetW * 2 || h > targetH * 2) {
            w = Math.max(targetW, w / 2);
            h = Math.max(targetH, h / 2);
            current = resize(current, w, h);
        }
        current = resize(current, targetW, targetH);

        scaledCache.put(key, current);
        return current;
    }

    private static BufferedImage resize(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return out;
    }

    private static void drawCentered(Graphics2D g2, String text, int centerX, int y) {
        g2.drawString(text, centerX - g2.getFontMetrics().stringWidth(text) / 2, y);
    }

    private static Font font(int style, int size) {
        return new Font("Bahnschrift", style, size); 
    }

    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }


    private static class Backdrop extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private class WeaponCard extends JButton {
        private final Weapon weapon;
        private boolean hovered;

        WeaponCard(Weapon weapon) {
            this.weapon = weapon;
            setPreferredSize(new Dimension(120, 40));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    detail.display(weapon);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
            addActionListener(e -> onClick(weapon));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);

            int w = getWidth();
            int h = getHeight();
            boolean owned = isOwned(weapon);
            boolean dimmed = !owned && !canAfford(weapon);

            if (owned) {
                g2.setColor(TEAL_FILL);
            } else if (hovered && !dimmed) {
                g2.setColor(CARD_HOVER);
            } else {
                g2.setColor(CARD);
            }
            g2.fillRect(0, 0, w, h);

            if (owned || (hovered && !dimmed)) {
                g2.setColor(TEAL);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(1, 1, w - 3, h - 3);
            }

            BufferedImage image = imageFor(weapon);
            if (image != null) {
                Composite old = g2.getComposite();
                if (dimmed) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                }
                drawScaled(g2, image, 12, 6, w - 24, Math.max(10, h - 44));
                g2.setComposite(old);
            }

            String top = owned ? "OWNED" : "\u00A4" + String.format("%,d", weapon.getPrice());
            g2.setFont(font(Font.PLAIN, 12));
            g2.setColor(dimmed ? MUTED : TEXT);
            g2.drawString(top, 12, h - 28);

            g2.setFont(font(Font.PLAIN, 12));
            g2.setColor(owned ? TEXT : MUTED);
            g2.drawString(weapon.getName().toUpperCase(), 12, h - 10);

            g2.dispose();
        }
    }

    private class DetailPanel extends JPanel {
        private Weapon weapon;

        DetailPanel() {
            setBackground(PANEL);
            setPreferredSize(new Dimension(270, 0));
            setBorder(BorderFactory.createLineBorder(TEAL_DARK, 1));
        }

        void display(Weapon weapon) {
            this.weapon = weapon;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);

            int w = getWidth();
            if (weapon == null) {
                g2.setColor(MUTED);
                g2.setFont(font(Font.BOLD, 14));
                drawCentered(g2, "HOVER A WEAPON", w / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            BufferedImage image = imageFor(weapon);
            if (image != null) {
                drawScaled(g2, image, 20, 30, w - 40, 200);
            }

            g2.setColor(TEXT);
            g2.setFont(font(Font.BOLD, 28));
            drawCentered(g2, weapon.getName().toUpperCase(), w / 2, 280);

            g2.setColor(TEAL_DARK);
            g2.fillRect(20, 296, w - 40, 2);

            String status = isOwned(weapon) ? "OWNED" : (canAfford(weapon) ? "AVAILABLE" : "CAN'T AFFORD");
            g2.setColor(MUTED);
            g2.setFont(font(Font.PLAIN, 14));
            g2.drawString("Category:   " + weapon.getCategory(), 24, 330);
            g2.drawString("Cost:   \u00A4" + String.format("%,d", weapon.getPrice()), 24, 355);
            g2.drawString("Status:   " + status, 24, 380);

            g2.dispose();
        }
    }
}