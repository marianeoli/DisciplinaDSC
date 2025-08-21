package Produtos;

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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

       
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 100, 0),   // verde escuro
                        getWidth(), getHeight(), new Color(144, 238, 144) // verde claro
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setOpaque(false);
        setContentPane(bgPanel);

        
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));

        
        modelo = new DefaultTableModel(new String[]{"ID", "Código", "Nome", "Preço Compra", "Preço Venda", "Estoque"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(Color.BLACK);
        tabela.setGridColor(new Color(34, 139, 34));
        tabela.setSelectionBackground(new Color(60, 179, 113));
        tabela.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(Color.WHITE);
        bgPanel.add(scrollPane, BorderLayout.CENTER);

      
        JPanel botoesPanel = new JPanel();
        botoesPanel.setOpaque(false);

        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        
        Color verdeEscuro = new Color(0, 128, 0);
        Color verdeClaro = new Color(144, 238, 144);
        estilizarBotao(btnEditar, verdeEscuro, verdeClaro);
        estilizarBotao(btnExcluir, verdeEscuro, verdeClaro);

        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);

        if(!isAdmin){
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
        }

        bgPanel.add(botoesPanel, BorderLayout.SOUTH);

        
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
