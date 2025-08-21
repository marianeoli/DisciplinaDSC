package Vendas;

import Usuarios.Usuario;
import HomePage.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class RegistrarVendaFrame extends JFrame {

    private JComboBox<String> produtoCombo;
    private JTextField quantidadeField;
    private DefaultTableModel carrinhoModel;
    private JTable carrinhoTable;
    private Usuario usuarioLogado;

    public RegistrarVendaFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setTitle("Registrar Venda");
        setSize(600, 400);
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        JLabel titleLabel = new JLabel("💰 Registrar Venda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel userLabel = new JLabel("Usuário: " + usuarioLogado.getNome(), SwingConstants.CENTER);
        userLabel.setForeground(Color.WHITE);
        mainPanel.add(userLabel, gbc);
        gbc.gridwidth = 1;

      
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel produtoLabel = new JLabel("Produto:");
        produtoLabel.setForeground(Color.WHITE);
        mainPanel.add(produtoLabel, gbc);

        gbc.gridx = 1;
        produtoCombo = new JComboBox<>();
        carregarProdutos();
        mainPanel.add(produtoCombo, gbc);

        // Quantidade
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setForeground(Color.WHITE);
        mainPanel.add(quantidadeLabel, gbc);

        gbc.gridx = 1;
        quantidadeField = new JTextField(10);
        mainPanel.add(quantidadeField, gbc);


        // Botões
        JButton addProdutoBtn = new JButton("Adicionar Produto");
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 1;
        mainPanel.add(addProdutoBtn, gbc);

        JButton finalizarBtn = new JButton("Finalizar Venda");
        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(finalizarBtn, gbc);

        // Tabela carrinho
        carrinhoModel = new DefaultTableModel(new Object[]{"Produto", "Qtd", "Preço", "Subtotal"}, 0);
        carrinhoTable = new JTable(carrinhoModel);
        JScrollPane scroll = new JScrollPane(carrinhoTable);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        mainPanel.add(scroll, gbc);

        // Ações
        addProdutoBtn.addActionListener(e -> adicionarProduto());
        finalizarBtn.addActionListener(e -> {
            try {
                registrarVenda();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });
    }

    private void carregarProdutos() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, nome, preco_venda FROM produtos";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                produtoCombo.addItem(id + " - " + nome);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarProduto() {
        String produtoSelecionado = (String) produtoCombo.getSelectedItem();
        if (produtoSelecionado == null) return;

        int produtoId = Integer.parseInt(produtoSelecionado.split(" - ")[0]);

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT nome, preco_venda, estoque FROM produtos WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, produtoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                BigDecimal preco = rs.getBigDecimal("preco_venda");
                int estoqueAtual = rs.getInt("estoque");
                int qtd = Integer.parseInt(quantidadeField.getText());

                if (qtd > estoqueAtual) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente.");
                    return;
                }

                BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(qtd));
                carrinhoModel.addRow(new Object[]{produtoId + " - " + nome, qtd, preco, subtotal});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarVenda() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Calcula total
            BigDecimal totalVenda = BigDecimal.ZERO;
            for (int i = 0; i < carrinhoModel.getRowCount(); i++) {
                totalVenda = totalVenda.add((BigDecimal) carrinhoModel.getValueAt(i, 3));
            }

            // 2. Insere venda
            String sqlVenda = "INSERT INTO venda (data, valor_total, usuario_id) VALUES (?, ?, ?)";
            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmtVenda.setBigDecimal(2, totalVenda);
            stmtVenda.setLong(3, usuarioLogado.getId());
            stmtVenda.executeUpdate();

            ResultSet keys = stmtVenda.getGeneratedKeys();
            keys.next();
            int vendaId = keys.getInt(1);

            // 3. Insere itens e atualiza estoque
            String sqlItem = "INSERT INTO vendaItens (vendaId, produtoId, descricao, preco_unitario, quantidade) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);

            for (int i = 0; i < carrinhoModel.getRowCount(); i++) {
                String produtoStr = (String) carrinhoModel.getValueAt(i, 0);
                int produtoId = Integer.parseInt(produtoStr.split(" - ")[0]);
                int qtd = (int) carrinhoModel.getValueAt(i, 1);
                BigDecimal preco = (BigDecimal) carrinhoModel.getValueAt(i, 2);

                stmtItem.setInt(1, vendaId);
                stmtItem.setInt(2, produtoId);
                stmtItem.setString(3, produtoStr);
                stmtItem.setBigDecimal(4, preco);
                stmtItem.setInt(5, qtd);
                stmtItem.executeUpdate();

                PreparedStatement stmtUpdate = conn.prepareStatement("UPDATE produto SET estoque = estoque - ? WHERE id=?");
                stmtUpdate.setInt(1, qtd);
                stmtUpdate.setInt(2, produtoId);
                stmtUpdate.executeUpdate();
            }

            // 4. Insere transação financeira
            String sqlTrans = "INSERT INTO transacaofinanceira (data, valor, categoria, usuario_id) VALUES (?, ?, 'ENTRADA', ?)";
            PreparedStatement stmtTrans = conn.prepareStatement(sqlTrans);
            stmtTrans.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmtTrans.setBigDecimal(2, totalVenda);
            stmtTrans.setLong(3, usuarioLogado.getId());
            stmtTrans.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
            dispose();
        }
    }
}
