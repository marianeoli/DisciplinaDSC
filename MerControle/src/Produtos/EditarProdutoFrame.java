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
import java.awt.*;
import java.sql.*;
import HomePage.Database;
import java.math.BigDecimal;

public class EditarProdutoFrame extends JFrame {

    private JTextField nomeField, precoCompraField, precoVendaField, estoqueField;
    private int produtoId;
    private GerenciarProdutosFrame parent;

    public EditarProdutoFrame(int produtoId, GerenciarProdutosFrame parent) {
        this.produtoId = produtoId;
        this.parent = parent;

        setTitle("Editar Produto");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        nomeField = new JTextField(20);
        add(nomeField, gbc);

        // Preço de compra
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Preço de Compra:"), gbc);
        gbc.gridx = 1;
        precoCompraField = new JTextField(20);
        add(precoCompraField, gbc);

        // Preço de venda
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Preço de Venda:"), gbc);
        gbc.gridx = 1;
        precoVendaField = new JTextField(20);
        add(precoVendaField, gbc);

        // Estoque
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Estoque:"), gbc);
        gbc.gridx = 1;
        estoqueField = new JTextField(20);
        add(estoqueField, gbc);

        // Botão salvar
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salvarButton = new JButton("Salvar");
        add(salvarButton, gbc);

        salvarButton.addActionListener(e -> salvarEdicao());

        carregarProduto();
    }

    private void carregarProduto() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM produtos WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, produtoId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                nomeField.setText(rs.getString("nome"));
                precoCompraField.setText(rs.getBigDecimal("preco_compra").toString());
                precoVendaField.setText(rs.getBigDecimal("preco_venda").toString());
                estoqueField.setText(String.valueOf(rs.getInt("estoque")));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarEdicao() {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE produtos SET nome=?, preco_compra=?, preco_venda=?, estoque=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nomeField.getText());
            stmt.setBigDecimal(2, new BigDecimal(precoCompraField.getText()));
            stmt.setBigDecimal(3, new BigDecimal(precoVendaField.getText()));
            stmt.setInt(4, Integer.parseInt(estoqueField.getText()));
            stmt.setInt(5, produtoId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            parent.atualizarTabela();
            dispose();
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar produto.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
