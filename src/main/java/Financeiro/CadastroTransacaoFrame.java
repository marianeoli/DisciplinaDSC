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

        setTitle("💰 Cadastro de Transação Financeira");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("💰 Cadastro de Transação", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // --- Usuário logado ---
        JLabel userLabel = new JLabel("Usuário: " + usuarioLogado.getNome(), SwingConstants.CENTER);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(userLabel, gbc);
        gbc.gridwidth = 1;

        JLabel valorLabel = new JLabel("💵 Valor:");
        valorLabel.setForeground(Color.WHITE);
        valorLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(valorLabel, gbc);

        valorField = new JTextField();
        valorField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(valorField, gbc);

        JLabel categoriaLabel = new JLabel("📂 Categoria:");
        categoriaLabel.setForeground(Color.WHITE);
        categoriaLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(categoriaLabel, gbc);

        categoriaCombo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        categoriaCombo.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(categoriaCombo, gbc);

        JLabel descricaoLabel = new JLabel("📝 Descrição:");
        descricaoLabel.setForeground(Color.WHITE);
        descricaoLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(descricaoLabel, gbc);

        descricaoField = new JTextField();
        descricaoField.setFont(new Font("Inter", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(descricaoField, gbc);

        JButton salvarBtn = new JButton("💾 Salvar");
        salvarBtn.setFont(new Font("Inter", Font.BOLD, 16));
        salvarBtn.setForeground(Color.WHITE);
        salvarBtn.setBackground(new Color(0x00A86B));
        salvarBtn.setFocusPainted(false);
        salvarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        salvarBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        salvarBtn.putClientProperty("FlatLaf.style", "arc: 10");
        salvarBtn.addActionListener(this::salvarTransacao);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
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
