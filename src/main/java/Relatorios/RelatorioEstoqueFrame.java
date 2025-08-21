package Relatorios;

import HomePage.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioEstoqueFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField buscaField;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public RelatorioEstoqueFrame() {
        setTitle("📦 Relatório de Estoque");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120)); 
        add(mainPanel);

        JLabel titleLabel = new JLabel("📦 Relatório de Estoque", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Código", "Produto", "Quantidade", "Preço Venda"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(34, 139, 34));
        table.setSelectionBackground(new Color(60, 179, 113));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buscaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscaPanel.setBackground(new Color(34, 139, 120));
        JLabel buscaLabel = new JLabel("🔍 Buscar:");
        buscaLabel.setForeground(Color.WHITE);
        buscaField = new JTextField(20);
        buscaField.setToolTipText("Digite código ou nome do produto");
        buscaPanel.add(buscaLabel);
        buscaPanel.add(buscaField);
        mainPanel.add(buscaPanel, BorderLayout.BEFORE_FIRST_LINE);

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        buscaField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String text = buscaField.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(34, 139, 120));
        JButton exportBtn = new JButton("📄 Exportar PDF");
        estilizarBotao(exportBtn, new Color(0, 128, 0), new Color(144, 238, 144));
        exportBtn.addActionListener(e -> exportarParaPDF());
        buttonPanel.add(exportBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        carregarDados();
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

    private void carregarDados() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, codigo, nome, estoque, preco_venda FROM produtos ORDER BY estoque ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getInt("estoque"),
                        rs.getDouble("preco_venda")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estoque: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarParaPDF() {
    }
}
