package Relatorios;

import HomePage.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioVendasFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public RelatorioVendasFrame() {
        setTitle("Relatório de Vendas");
        setSize(700, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Data", "Produto", "Quantidade", "Total"}, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT v.id, v.data, p.nome AS produto, iv.quantidade, iv.subtotal " +
                         "FROM vendas v " +
                         "JOIN itens_venda iv ON v.id = iv.venda_id " +
                         "JOIN produtos p ON iv.produto_id = p.id " +
                         "ORDER BY v.data DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // limpa a tabela
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDate("data"),
                        rs.getString("produto"),
                        rs.getInt("quantidade"),
                        rs.getDouble("subtotal")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
