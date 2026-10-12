import java.util.ArrayList;
import java.util.List;


public class Player {
    private final List<Weapon> loadouts = new ArrayList<>();
    private int credits;
    
        
        public Player(int credits) {
            this.credits = credits;
        }

        public int getCredits() {
            return credits;
        }

        public List<Weapon> getLoadouts() {
            return new ArrayList<>(loadouts);
        }
    
        public boolean buyWeapon(Weapon weapon) {
            if (credits >= weapon.getPrice()) {
                credits -= weapon.getPrice();
                loadouts.add(weapon);
                return true;
            }
            return false;
        }

        public boolean sellWeapon(Weapon weapon) {
            if (loadouts.remove(weapon)) {
            credits += weapon.getPrice();
            return true;
        }
            return false;
        }


}
