package HomePage;

import com.formdev.flatlaf.FlatClientProperties;
import Produtos.Produto;
import Usuarios.Usuario;
import Usuarios.Utils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("MerControle - Login");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fundo degradê verde
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x00A86B), // Verde
                        0, getHeight(), new Color(0x006B46) // Verde mais escuro
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título estilizado
        JLabel title = new JLabel("MerControle");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        bgPanel.add(title, gbc);

        // Label Usuário
        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        bgPanel.add(userLabel, gbc);

        // Campo Usuário
        userField = new JTextField(18);
        userField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite seu login");
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        bgPanel.add(userField, gbc);

        // Label Senha
        JLabel passLabel = new JLabel("Senha:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        bgPanel.add(passLabel, gbc);

        // Campo Senha
        passField = new JPasswordField(18);
        passField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite sua senha");
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        bgPanel.add(passField, gbc);

        // Botão Login
        JButton loginButton = new JButton("Entrar");
        loginButton.setBackground(new Color(0x00C97C));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        // Hover
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0x00E68A));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0x00C97C));
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        bgPanel.add(loginButton, gbc);

        // 👉 Define o botão de login como "padrão" (Enter ativa ele)
        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> login());

        add(bgPanel);
    }

    private void login() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        // Criptografa a senha digitada
        String hashedPass = Utils.hashSenha(pass);

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user);
            stmt.setString(2, hashedPass);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuarioLogado = new Usuario();
                usuarioLogado.setLogin(user);
                usuarioLogado.setTipo(rs.getString("tipo"));
                usuarioLogado.setNome(rs.getString("nome"));
                usuarioLogado.setId(rs.getInt("id"));

                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
                dispose();
                new MainFrame(usuarioLogado).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
