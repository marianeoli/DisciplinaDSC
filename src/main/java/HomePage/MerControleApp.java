package HomePage;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class MerControleApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
