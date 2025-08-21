package Relatorios;

import HomePage.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioEstoqueFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public RelatorioEstoqueFrame() {
        setTitle("Relatório de Estoque");
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Painel principal com fundo verde
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120)); // verde estiloso
        add(mainPanel);

        // Label topo
        JLabel titleLabel = new JLabel("📦 Relatório de Estoque", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabela
        model = new DefaultTableModel(new String[]{"ID", "Código", "Produto", "Quantidade", "Preço Venda"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        // Painel branco para destacar a tabela
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, codigo, nome, estoque, preco_venda " +
                         "FROM produtos ORDER BY estoque ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getInt("estoque"),
                        rs.getDouble("preco_venda")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estoque: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
