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
import java.sql.Connection;
import java.sql.PreparedStatement;
import HomePage.Database;

public class CadastroProdutoFrame extends JFrame {

    private JTextField nomeField, precoCompraField, precoVendaField, estoqueField;

    public CadastroProdutoFrame() {
        setTitle("Cadastro de Produto");
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
        add(new JLabel("Quantidade em Estoque:"), gbc);
        gbc.gridx = 1;
        estoqueField = new JTextField(20);
        add(estoqueField, gbc);

        // Botão salvar
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salvarButton = new JButton("Salvar");
        add(salvarButton, gbc);

        salvarButton.addActionListener(e -> salvarProduto());
    }
    
    private String gerarCodigoProduto(int tamanho) {
    String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder();
    java.util.Random random = new java.util.Random();
    for(int i = 0; i < tamanho; i++) {
        sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
    }
    return sb.toString();
    }

    private void salvarProduto() {
        String codigo = gerarCodigoProduto(8); // código de 8 caracteres
        String nome = nomeField.getText();
        String precoCompra = precoCompraField.getText().trim().replace(",", ".");
        String precoVenda = precoVendaField.getText().trim().replace(",", ".");
        String estoque = estoqueField.getText();

        if(nome.isEmpty() || precoCompra.isEmpty() || precoVenda.isEmpty() || estoque.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
        java.math.BigDecimal precoCompraBD = new java.math.BigDecimal(precoCompra);
        java.math.BigDecimal precoVendaBD = new java.math.BigDecimal(precoVenda);
        int estoqueInt = Integer.parseInt(estoque);

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO produtos (codigo, nome, preco_compra, preco_venda, estoque) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, codigo);
                stmt.setString(2, nome);
                stmt.setBigDecimal(3, new java.math.BigDecimal(precoCompra));
                stmt.setBigDecimal(4, new java.math.BigDecimal(precoVenda));
                stmt.setInt(5, Integer.parseInt(estoque));
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
                dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
