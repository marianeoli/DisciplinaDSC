package Usuarios;

import HomePage.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GerenciarUsuariosFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public GerenciarUsuariosFrame() {
        setTitle("Gerenciar Usuários");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Painel principal verde
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120)); // verde moderno
        add(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("👥 Gerenciar Usuários", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabela com scroll
        model = new DefaultTableModel(new String[]{"ID", "Nome", "Login", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID não editável
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(34, 139, 120)); // mesmo verde
        JButton atualizarBtn = new JButton("🔄 Atualizar");
        JButton deletarBtn = new JButton("❌ Deletar");
        atualizarBtn.setBackground(new Color(34, 139, 120));
        atualizarBtn.setForeground(Color.WHITE);
        atualizarBtn.setFocusPainted(false);
        deletarBtn.setBackground(new Color(34, 139, 120));
        deletarBtn.setForeground(Color.WHITE);
        deletarBtn.setFocusPainted(false);
        buttonPanel.add(atualizarBtn);
        buttonPanel.add(deletarBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Carrega os usuários do banco
        carregarUsuarios();

        // Botões
        atualizarBtn.addActionListener(e -> atualizarUsuarios());
        deletarBtn.addActionListener(e -> deletarUsuario());
    }

    private void carregarUsuarios() {
        model.setRowCount(0);
        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nome, login, tipo FROM usuarios");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("tipo")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarUsuarios() {
        try (Connection conn = Database.getConnection()) {
            for (int i = 0; i < model.getRowCount(); i++) {
                int id = (int) model.getValueAt(i, 0);
                String nome = (String) model.getValueAt(i, 1);
                String login = (String) model.getValueAt(i, 2);
                String tipo = (String) model.getValueAt(i, 3);

                String sql = "UPDATE usuarios SET nome=?, login=?, tipo=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, login);
                stmt.setString(3, tipo);
                stmt.setInt(4, id);
                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Usuários atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarUsuario() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para deletar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar este usuário?");
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM usuarios WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Usuário deletado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao deletar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
