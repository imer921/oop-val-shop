import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShopWindow extends JFrame {
    private final Player player;
    private final JLabel creditsLabel = new JLabel();
    private final DefaultListModel<String> loadoutModel = new DefaultListModel<>();

    public ShopWindow(Player player) {
        this.player = player;

        setTitle("Valorant Shop");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top: credits
        creditsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        updateCredits();
        add(creditsLabel, BorderLayout.NORTH);

        // Center: weapon buttons
        JPanel weaponPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        weaponPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        for (Weapon weapon : createWeapons()) {
            weaponPanel.add(createWeaponButton(weapon));
        }
        add(weaponPanel, BorderLayout.CENTER);

        // Right: loadout list
        for (Weapon owned : player.getLoadouts()) {
            loadoutModel.addElement(owned.toString());
        }
        JScrollPane loadoutScroll = new JScrollPane(new JList<>(loadoutModel));
        loadoutScroll.setBorder(BorderFactory.createTitledBorder("Loadout"));
        loadoutScroll.setPreferredSize(new Dimension(180, 0));
        add(loadoutScroll, BorderLayout.EAST);

        setVisible(true);
    }

    private List<Weapon> createWeapons() {
        return List.of(
            new Weapon("Classic", 0, "Pistol"),
            new Weapon("Ghost", 500, "Pistol"),
            new Weapon("Sheriff", 800, "Pistol"),
            new Weapon("Spectre", 1600, "SMG"),
            new Weapon("Phantom", 2900, "Rifle"),
            new Weapon("Vandal", 2900, "Rifle"),
            new Weapon("Operator", 4700, "Sniper")
        );
    }

    private JButton createWeaponButton(Weapon weapon) {
        JButton button = new JButton("<html><center>" + weapon.getName()
                + "<br>" + weapon.getPrice() + " - " + weapon.getCategory()
                + "</center></html>");

        button.addActionListener(e -> {
            if (player.buyWeapon(weapon)) {
                updateCredits();
                loadoutModel.addElement(weapon.toString());
                button.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Not enough credits for " + weapon.getName() + ".");
            }
        });
        return button;
    }

    private void updateCredits() {
        creditsLabel.setText("Credits: " + player.getCredits());
    }
}