package Usuarios;

import HomePage.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import Usuarios.Utils;

public class CadastroUsuarioFrame extends JFrame {

    private JTextField nomeField, loginField;
    private JPasswordField senhaField;
    private JComboBox<String> tipoCombo;

    public CadastroUsuarioFrame() {
        setTitle("Cadastro de Usuário");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x00A86B), 
                        0, getHeight(), new Color(0x006B46)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("➕ Cadastro de Usuário", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setForeground(Color.WHITE);
        mainPanel.add(nomeLabel, gbc);

        gbc.gridx = 1;
        nomeField = new JTextField(20);
        mainPanel.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setForeground(Color.WHITE);
        mainPanel.add(loginLabel, gbc);

        gbc.gridx = 1;
        loginField = new JTextField(20);
        mainPanel.add(loginField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setForeground(Color.WHITE);
        mainPanel.add(senhaLabel, gbc);

        gbc.gridx = 1;
        senhaField = new JPasswordField(20);
        mainPanel.add(senhaField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel tipoLabel = new JLabel("Tipo:");
        tipoLabel.setForeground(Color.WHITE);
        mainPanel.add(tipoLabel, gbc);

        gbc.gridx = 1;
        tipoCombo = new JComboBox<>(new String[]{"Administrador","Funcionario"});
        mainPanel.add(tipoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salvarButton = new JButton("💾 Salvar");
        salvarButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        salvarButton.setBackground(new Color(0x00A86B));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setFocusPainted(false);
        salvarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(salvarButton, gbc);

        salvarButton.addActionListener(e -> salvarUsuario());
    }

    private void salvarUsuario() {
        String nome = nomeField.getText();
        String login = loginField.getText();
        String senha = new String(senhaField.getPassword());
        String tipo = (String) tipoCombo.getSelectedItem();

        if(nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String senhaHash = Utils.hashSenha(senha);

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO usuarios (nome, login, senha, tipo) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, login);
            stmt.setString(3, senhaHash); 
            stmt.setString(4, tipo);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
