package Financeiro;

import HomePage.Database;
import Usuarios.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CadastroTransacaoFrame extends JFrame {

    private JTextField valorField;
    private JComboBox<String> categoriaCombo;
    private JTextField descricaoField;
    private Usuario usuarioLogado;

    public CadastroTransacaoFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setTitle("Cadastro de Transação Financeira");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(34, 139, 120));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Valor:"), gbc);

        valorField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(valorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Categoria:"), gbc);

        categoriaCombo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(categoriaCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Descrição:"), gbc);

        descricaoField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(descricaoField, gbc);
        
        JButton salvarBtn = new JButton("Salvar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        salvarBtn.addActionListener(this::salvarTransacao);
        mainPanel.add(salvarBtn, gbc);
    }

    private void salvarTransacao(ActionEvent e) {
        String valorStr = valorField.getText().trim();
        String categoria = (String) categoriaCombo.getSelectedItem();
        String descricao = descricaoField.getText().trim();

        if (valorStr.isEmpty() || descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double valor = Double.parseDouble(valorStr);

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO transacaoFinanceira (data, valor, categoria, usuario_id, descricao) "
                        + "VALUES (NOW(), ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDouble(1, valor);
                stmt.setString(2, categoria);
                stmt.setInt(3, usuarioLogado.getId());
                stmt.setString(4, descricao);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Transação cadastrada com sucesso!");
                valorField.setText("");
                descricaoField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar transação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
