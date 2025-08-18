/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Produtos;

/**
 *
 * @author mariane
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import HomePage.Database;

public class GerenciarProdutosFrame extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private boolean isAdmin;

    public GerenciarProdutosFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("Gerenciar Produtos");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Tabela
        modelo = new DefaultTableModel(new String[]{"ID", "Código", "Nome", "Preço Compra", "Preço Venda", "Estoque"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // não editável diretamente
            }
        };
        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Botões
        JPanel botoesPanel = new JPanel();
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);

        if(!isAdmin){
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
        }

        add(botoesPanel, BorderLayout.SOUTH);

        // Ações
        btnEditar.addActionListener(e -> editarProduto());
        btnExcluir.addActionListener(e -> deletarProduto());

        carregarProdutos();
    }

    private void carregarProdutos() {
        modelo.setRowCount(0); // limpa tabela
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM produtos";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nome"),
                    rs.getBigDecimal("preco_compra"),
                    rs.getBigDecimal("preco_venda"),
                    rs.getInt("estoque")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarProduto() {
        int linha = tabela.getSelectedRow();
        if(linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        new EditarProdutoFrame(id, this).setVisible(true);
    }

    private void deletarProduto() {
        int linha = tabela.getSelectedRow();
        if(linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM produtos WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                carregarProdutos();
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir produto.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void atualizarTabela() {
        carregarProdutos();
    }
}