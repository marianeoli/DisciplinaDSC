package Vendas;

import HomePage.Database;
import Usuarios.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegistrarVendaFrame extends JFrame {
    private JComboBox<String> produtoCombo;
    private JTextField quantidadeField;
    private Usuario usuarioLogado; // <<< atributo que faltava

    public RegistrarVendaFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado; // <<< salva usuário logado
        setTitle("Registrar Venda");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuário logado
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel label = new JLabel("Usuário: " + usuarioLogado.getNome());
        add(label, gbc);
        gbc.gridwidth = 1;

        // Produto
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1;
        produtoCombo = new JComboBox<>();
        carregarProdutos();
        add(produtoCombo, gbc);

        // Quantidade
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1;
        quantidadeField = new JTextField(10);
        add(quantidadeField, gbc);

        // Botão registrar
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registrarBtn = new JButton("Registrar Venda");
        add(registrarBtn, gbc);

        registrarBtn.addActionListener(e -> registrarVenda());
    }

    private void carregarProdutos() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, nome FROM produtos";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                produtoCombo.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarVenda() {
        String produtoSelecionado = (String) produtoCombo.getSelectedItem();
        if(produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }

        int produtoId = Integer.parseInt(produtoSelecionado.split(" - ")[0]);
        int quantidade;

        try {
            quantidade = Integer.parseInt(quantidadeField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Verifica estoque e preço
            String sqlProduto = "SELECT preco_venda, estoque FROM produtos WHERE id=?";
            PreparedStatement stmtProd = conn.prepareStatement(sqlProduto);
            stmtProd.setInt(1, produtoId);
            ResultSet rs = stmtProd.executeQuery();

            if(rs.next()) {
                int estoqueAtual = rs.getInt("estoque");
                BigDecimal precoVenda = rs.getBigDecimal("preco_venda");

                if(quantidade > estoqueAtual) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente.");
                    return;
                }

                BigDecimal valorTotal = precoVenda.multiply(new BigDecimal(quantidade));

                // Insere venda com ID do usuário logado
                String sqlVenda = "INSERT INTO vendas (produto_id, quantidade, valor_total, data_hora, usuario_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda);
                stmtVenda.setInt(1, produtoId);
                stmtVenda.setInt(2, quantidade);
                stmtVenda.setBigDecimal(3, valorTotal);
                stmtVenda.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                stmtVenda.setInt(5, usuarioLogado.getId()); // <<< pega ID do usuário logado
                stmtVenda.executeUpdate();

                // Atualiza estoque
                String sqlUpdate = "UPDATE produtos SET estoque=? WHERE id=?";
                PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                stmtUpdate.setInt(1, estoqueAtual - quantidade);
                stmtUpdate.setInt(2, produtoId);
                stmtUpdate.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
                dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
