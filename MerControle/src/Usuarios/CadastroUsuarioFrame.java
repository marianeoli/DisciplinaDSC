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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(20);
        add(nomeField, gbc);

        // Login
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Login:"), gbc);
        gbc.gridx = 1;
        loginField = new JTextField(20);
        add(loginField, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        senhaField = new JPasswordField(20);
        add(senhaField, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        tipoCombo = new JComboBox<>(new String[]{"Administrador","Funcionario"});
        add(tipoCombo, gbc);

        // Botão salvar
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salvarButton = new JButton("Salvar");
        add(salvarButton, gbc);

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
