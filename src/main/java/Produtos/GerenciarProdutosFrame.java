package Produtos;

import HomePage.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GerenciarProdutosFrame extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private boolean isAdmin;

    public GerenciarProdutosFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setTitle("Gerenciar Produtos");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120)); // verde moderno
        add(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("📦 Gerenciar Produtos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabela
        modelo = new DefaultTableModel(new String[]{"ID", "Código", "Nome", "Preço Compra", "Preço Venda", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tudo não editável aqui
            }
        };
        tabela = new JTable(modelo);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(Color.BLACK);
        tabela.setGridColor(new Color(34, 139, 34));
        tabela.setSelectionBackground(new Color(60, 179, 113));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(34, 139, 120));

        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnExcluir = new JButton("❌ Excluir");

        estilizarBotao(btnEditar, new Color(34, 139, 120), new Color(144, 238, 144));
        estilizarBotao(btnExcluir, new Color(34, 139, 120), new Color(144, 238, 144));

        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);

        if (!isAdmin) {
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ações dos botões
        btnEditar.addActionListener(e -> editarProduto());
        btnExcluir.addActionListener(e -> deletarProduto());

        carregarProdutos();
    }

    private void estilizarBotao(JButton botao, Color corFundo, Color corHover) {
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(corHover);
                botao.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(corFundo);
                botao.setForeground(Color.WHITE);
            }
        });
    }

    private void carregarProdutos() {
        modelo.setRowCount(0);
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
