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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
            // Agora todos os produtos, inclusive com estoque 0
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
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("relatorio_estoque.pdf"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();

                // Criar documento PDF
                Document doc = new Document();
                PdfWriter.getInstance(doc, new java.io.FileOutputStream(caminho));
                doc.open();

                // Fonte do título
                com.itextpdf.text.Font tituloFont = com.itextpdf.text.FontFactory.getFont(
                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.BaseColor.BLACK);
                Paragraph titulo = new Paragraph("📦 Relatório de Estoque\n\n", tituloFont);
                titulo.setAlignment(Element.ALIGN_CENTER);
                doc.add(titulo);

                // Criar tabela PDF com mesmas colunas
                PdfPTable tabelaPDF = new PdfPTable(model.getColumnCount());
                tabelaPDF.setWidthPercentage(100);
                tabelaPDF.setSpacingBefore(10f);
                tabelaPDF.setSpacingAfter(10f);

                // Cabeçalho
                com.itextpdf.text.Font headFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell header = new PdfPCell(new Paragraph(model.getColumnName(i), headFont));
                    header.setBackgroundColor(new BaseColor(0, 128, 0));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    tabelaPDF.addCell(header);
                }

                // Dados da tabela
                com.itextpdf.text.Font cellFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA, 11, BaseColor.BLACK);
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Paragraph(model.getValueAt(i, j).toString(), cellFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5);
                        if (i % 2 == 0) {
                            cell.setBackgroundColor(new BaseColor(230, 255, 230));
                        }
                        tabelaPDF.addCell(cell);
                    }
                }

                doc.add(tabelaPDF);

                doc.close();
                JOptionPane.showMessageDialog(this, "PDF do estoque exportado com sucesso!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage());
        }
    }

}
