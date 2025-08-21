package Relatorios;

import HomePage.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

public class RelatorioFinanceiroFrame extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private JLabel lblSaldo;

    public RelatorioFinanceiroFrame(int mes, int ano) {
        setTitle("Relatório Financeiro - " + mes + "/" + ano);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Colunas: Tipo, ID, Data, Descrição, Valor
        model = new DefaultTableModel(new String[]{"Tipo", "ID", "Data", "Descrição", "Valor"}, 0);
        tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);

        carregarVendas(mes, ano);
        carregarSaidas(mes, ano);

        float saldo = calcularSaldo();
        lblSaldo = new JLabel("Saldo do mês: R$ " + saldo);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblSaldo, BorderLayout.SOUTH);
    }

    private void carregarVendas(int mes, int ano) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT v.id, v.data, SUM(iv.quantidade * iv.precoUnitario) AS total_venda " +
                         "FROM vendaItens iv " +
                         "JOIN VendaItens iv ON v.id = iv.vendaId " +
                         "WHERE MONTH(v.data)=? AND YEAR(v.data)=? " +
                         "GROUP BY v.id, v.data " +
                         "ORDER BY v.data ASC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idVenda = rs.getInt("id");
                Date data = rs.getDate("data");
                float totalVenda = rs.getFloat("total_venda");

                // Tipo = "ENTRADA"
                model.addRow(new Object[]{"ENTRADA", idVenda, data, "Venda de produtos", totalVenda});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarSaidas(int mes, int ano) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, data, valor FROM transacaoFinanceira " +
                         "WHERE categoria='SAIDA' AND MONTH(data)=? AND YEAR(data)=? " +
                         "ORDER BY data ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date data = rs.getDate("data");
                float valor = rs.getFloat("valor");

                model.addRow(new Object[]{"SAIDA", id, data, "Saída financeira", valor});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float calcularSaldo() {
        float saldo = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String tipo = (String) model.getValueAt(i, 0);
            float valor = ((Number) model.getValueAt(i, 4)).floatValue();

            if (tipo.equals("ENTRADA")) {
                saldo += valor;
            } else if (tipo.equals("SAIDA")) {
                saldo -= valor;
            }
        }
        return saldo;
    }
}
