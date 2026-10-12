public class Main {
    
    public static void main(String[] args) {
        System.out.println(new java.io.File(".").getAbsolutePath());
        
        Weapon weapon1 = new Weapon("Vandal", 2900, "Rifle");
        Weapon weapon2 = new Weapon("Ghost", 500, "Pistol");
        Weapon weapon3 = new Weapon("Phantom", 2900, "Rifle");

        System.out.println(weapon1);
        System.out.println(weapon2);
        System.out.println(weapon3);

        //Player player = new Player(800);
        // System.out.println("\nPlayer's credits: " + player.getCredits());

        /* 
        
        boolean bought = player.buyWeapon(weapon2);
        if (bought) {
            System.out.println("Weapon bought: " + weapon2.getName());
        } else {
            System.out.println("buy status for " + weapon2.getName() + ":  Not enough credits.");
        }

        bought = player.buyWeapon(weapon1);
        if (bought) {
            System.out.println("Weapon bought: " + weapon1.getName());
        } else {
            System.out.println("buy status for " + weapon1.getName() + ":  Not enough credits.");
        }

        System.out.println("\nPlayer's loadout: ");
        for (Weapon weapon : player.getLoadouts()) {
            System.out.println(weapon);
        }

        */

        Player player = new Player(9000);
        new ShopWindow(player);
    }
}
