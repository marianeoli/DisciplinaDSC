package Vendas;

import Usuarios.Usuario;
import HomePage.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrarVendaFrame extends JFrame {

    private JComboBox<String> produtoCombo;
    private JTextField quantidadeField;
    private JTextField buscaField;
    private DefaultTableModel carrinhoModel;
    private JTable carrinhoTable;
    private Usuario usuarioLogado;
    private JLabel totalLabel;

    private List<String> produtosFullList = new ArrayList<>();

    public RegistrarVendaFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        setTitle("Registrar Venda");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x00A86B), 0, getHeight(), new Color(0x006B46));
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
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel userLabel = new JLabel("Usuário: " + usuarioLogado.getNome(), SwingConstants.CENTER);
        userLabel.setForeground(Color.WHITE);
        mainPanel.add(userLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel buscaLabel = new JLabel("Buscar Produto:");
        buscaLabel.setForeground(Color.WHITE);
        mainPanel.add(buscaLabel, gbc);

        gbc.gridx = 1;
        buscaField = new JTextField(15);
        mainPanel.add(buscaField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel produtoLabel = new JLabel("Produto:");
        produtoLabel.setForeground(Color.WHITE);
        mainPanel.add(produtoLabel, gbc);

        gbc.gridx = 1;
        produtoCombo = new JComboBox<>();
        produtoCombo.setEditable(false);
        mainPanel.add(produtoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setForeground(Color.WHITE);
        mainPanel.add(quantidadeLabel, gbc);

        gbc.gridx = 1;
        quantidadeField = new JTextField(10);
        mainPanel.add(quantidadeField, gbc);

        JButton addProdutoBtn = new JButton("Adicionar Produto");
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(addProdutoBtn, gbc);

        JButton finalizarBtn = new JButton("Finalizar Venda");
        gbc.gridx = 1; gbc.gridy = 5;
        mainPanel.add(finalizarBtn, gbc);

        carrinhoModel = new DefaultTableModel(new Object[]{"Produto", "Qtd", "Preço", "Subtotal"}, 0);
        carrinhoTable = new JTable(carrinhoModel);
        JScrollPane scroll = new JScrollPane(carrinhoTable);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        mainPanel.add(scroll, gbc);

        totalLabel = new JLabel("Total: R$ 0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(totalLabel, gbc);
        gbc.gridwidth = 1;

        carregarProdutos(); 

        buscaField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarProdutos(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarProdutos(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarProdutos(); }
        });

        addProdutoBtn.addActionListener(e -> adicionarProduto());
        finalizarBtn.addActionListener(e -> {
            try { registrarVenda(); }
            catch (SQLException ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
        });
    }

    private void carregarProdutos() {
        produtosFullList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, nome, preco_venda FROM produtos WHERE estoque > 0 ORDER BY nome";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            produtoCombo.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String item = id + " - " + nome;
                produtosFullList.add(item);
                produtoCombo.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarProdutos() {
        String filtro = buscaField.getText().trim().toLowerCase();
        produtoCombo.removeAllItems();
        for (String item : produtosFullList) {
            if (item.toLowerCase().contains(filtro)) {
                produtoCombo.addItem(item);
            }
        }
        if (produtoCombo.getItemCount() > 0) {
            produtoCombo.setSelectedIndex(0);
        }
    }

    private void adicionarProduto() {
        String produtoSelecionado = (String) produtoCombo.getSelectedItem();
        if (produtoSelecionado == null || produtoSelecionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }

        String qtdText = quantidadeField.getText().trim();
        if (qtdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a quantidade.");
            return;
        }

        int qtd;
        try {
            qtd = Integer.parseInt(qtdText);
            if (qtd <= 0) { JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero."); return; }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida."); return;
        }

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

                if (qtd > estoqueAtual) { JOptionPane.showMessageDialog(this, "Estoque insuficiente."); return; }

                BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(qtd));
                carrinhoModel.addRow(new Object[]{produtoId + " - " + nome, qtd, preco, subtotal});
                atualizarTotal();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void atualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < carrinhoModel.getRowCount(); i++)
            total = total.add((BigDecimal) carrinhoModel.getValueAt(i, 3));
        totalLabel.setText("Total: R$ " + total.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    private void registrarVenda() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            String sqlUser = "SELECT COUNT(*) FROM usuarios WHERE id=?";
            PreparedStatement stmtUser = conn.prepareStatement(sqlUser);
            stmtUser.setInt(1, usuarioLogado.getId());
            ResultSet rsUser = stmtUser.executeQuery();
            if (rsUser.next() && rsUser.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Usuário logado não existe mais. Venda cancelada.", "Erro", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            BigDecimal totalVenda = BigDecimal.ZERO;
            for (int i = 0; i < carrinhoModel.getRowCount(); i++)
                totalVenda = totalVenda.add((BigDecimal) carrinhoModel.getValueAt(i, 3));

            String sqlVenda = "INSERT INTO vendas (data, valorTotal, usuarioId) VALUES (?, ?, ?)";
            PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmtVenda.setBigDecimal(2, totalVenda);
            stmtVenda.setInt(3, usuarioLogado.getId());
            stmtVenda.executeUpdate();

            ResultSet keys = stmtVenda.getGeneratedKeys(); keys.next();
            int vendaId = keys.getInt(1);

            String sqlItem = "INSERT INTO vendaItens (vendaId, produtoId, descricao, precoUnitario, quantidade) VALUES (?, ?, ?, ?, ?)";
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

                PreparedStatement stmtUpdate = conn.prepareStatement("UPDATE produtos SET estoque = estoque - ? WHERE id=?");
                stmtUpdate.setInt(1, qtd);
                stmtUpdate.setInt(2, produtoId);
                stmtUpdate.executeUpdate();
            }

            String sqlTrans = "INSERT INTO transacaoFinanceira (data, valor, categoria, usuario_id) VALUES (?, ?, 'ENTRADA', ?)";
            PreparedStatement stmtTrans = conn.prepareStatement(sqlTrans);
            stmtTrans.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmtTrans.setBigDecimal(2, totalVenda);
            stmtTrans.setInt(3, usuarioLogado.getId());
            stmtTrans.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
            dispose();
            carregarProdutos();
        }
    }
}
