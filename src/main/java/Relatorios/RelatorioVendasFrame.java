package Relatorios;

import HomePage.Database;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class RelatorioVendasFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JPanel produtosPanel;
    private List<JCheckBox> checkBoxesProdutos = new ArrayList<>();

    public RelatorioVendasFrame() {
        setTitle("Relatório de Vendas");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120));
        add(mainPanel);

        JLabel titleLabel = new JLabel("📊 Relatório de Vendas", SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtDataInicio = new JTextField(10);
        txtDataFim = new JTextField(10);

        produtosPanel = new JPanel();
        produtosPanel.setLayout(new BoxLayout(produtosPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollProdutos = new JScrollPane(produtosPanel);
        scrollProdutos.setPreferredSize(new Dimension(150, 120));

        filterPanel.add(new JLabel("Data Início (yyyy-mm-dd):"));
        filterPanel.add(txtDataInicio);
        filterPanel.add(new JLabel("Data Fim (yyyy-mm-dd):"));
        filterPanel.add(txtDataFim);
        filterPanel.add(new JLabel("Produtos:"));
        filterPanel.add(scrollProdutos);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltro());
        filterPanel.add(btnFiltrar);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Data/Hora", "Produto", "Quantidade", "Total"}, 0);
        table = new JTable(model);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JButton exportarPDF = new JButton("Exportar PDF");
        exportarPDF.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        exportarPDF.addActionListener(e -> exportarParaPDF());
        mainPanel.add(exportarPDF, BorderLayout.SOUTH);

        carregarProdutos();
        aplicarFiltro();
    }

    private void carregarProdutos() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT nome FROM produtos ORDER BY nome ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            produtosPanel.removeAll();
            checkBoxesProdutos.clear();
            while (rs.next()) {
                String nome = rs.getString("nome");
                JCheckBox cb = new JCheckBox(nome);
                checkBoxesProdutos.add(cb);
                produtosPanel.add(cb);
            }
            produtosPanel.revalidate();
            produtosPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltro() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT v.id, v.data, vi.quantidade, vi.precoUnitario, p.nome AS produto, "
                    + "(vi.quantidade * vi.precoUnitario) AS total "
                    + "FROM vendaItens vi "
                    + "JOIN vendas v ON vi.vendaId = v.id "
                    + "JOIN produtos p ON vi.produtoId = p.id WHERE 1=1 ";

            if (!txtDataInicio.getText().isEmpty()) {
                sql += "AND v.data >= ? ";
            }
            if (!txtDataFim.getText().isEmpty()) {
                sql += "AND v.data <= ? ";
            }

            List<String> produtosSelecionados = new ArrayList<>();
            for (JCheckBox cb : checkBoxesProdutos) {
                if (cb.isSelected()) {
                    produtosSelecionados.add(cb.getText());
                }
            }

            if (!produtosSelecionados.isEmpty()) {
                String placeholders = String.join(",", produtosSelecionados.stream().map(s -> "?").toArray(String[]::new));
                sql += "AND p.nome IN (" + placeholders + ") ";
            }

            sql += "ORDER BY v.data DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 1;

            if (!txtDataInicio.getText().isEmpty()) {
                stmt.setTimestamp(index++, Timestamp.valueOf(txtDataInicio.getText() + " 00:00:00"));
            }
            if (!txtDataFim.getText().isEmpty()) {
                stmt.setTimestamp(index++, Timestamp.valueOf(txtDataFim.getText() + " 23:59:59"));
            }
            for (String p : produtosSelecionados) {
                stmt.setString(index++, p);
            }

            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                Timestamp dataHora = rs.getTimestamp("data");
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    dataHora, 
                    rs.getString("produto"),
                    rs.getInt("quantidade"),
                    rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao filtrar vendas. Verifique o formato da data (yyyy-mm-dd) e se a coluna 'data' no banco de dados é do tipo TIMESTAMP.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarParaPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("relatorio_vendas.pdf"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();

                Document doc = new Document();
                PdfWriter.getInstance(doc, new java.io.FileOutputStream(caminho));
                doc.open();

                com.itextpdf.text.Font tituloFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph("📊 Relatório de Vendas\n\n", tituloFont);
                titulo.setAlignment(Element.ALIGN_CENTER);
                doc.add(titulo);

                PdfPTable tabelaPDF = new PdfPTable(model.getColumnCount());
                tabelaPDF.setWidthPercentage(100);
                tabelaPDF.setSpacingBefore(10f);
                tabelaPDF.setSpacingAfter(10f);

                com.itextpdf.text.Font headFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell header = new PdfPCell(new Paragraph(model.getColumnName(i), headFont));
                    header.setBackgroundColor(new BaseColor(0, 128, 0));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    tabelaPDF.addCell(header);
                }

                com.itextpdf.text.Font cellFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA, 11, BaseColor.BLACK);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                double totalGeral = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String cellValue;
                        if (j == 1) { 
                            Timestamp timestamp = (Timestamp) model.getValueAt(i, j);
                            cellValue = dateFormat.format(timestamp);
                        } else {
                            cellValue = model.getValueAt(i, j).toString();
                        }

                        PdfPCell cell = new PdfPCell(new Paragraph(cellValue, cellFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(5);
                        if (i % 2 == 0) {
                            cell.setBackgroundColor(new BaseColor(230, 255, 230));
                        }
                        tabelaPDF.addCell(cell);
                    }
                    totalGeral += ((Number) model.getValueAt(i, 4)).doubleValue();
                }

                doc.add(tabelaPDF);

                Paragraph rodape = new Paragraph(
                        "Total Geral: R$ " + String.format("%.2f", totalGeral),
                        com.itextpdf.text.FontFactory.getFont(
                                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY)
                );
                rodape.setAlignment(Element.ALIGN_RIGHT);
                doc.add(rodape);

                doc.close();
                JOptionPane.showMessageDialog(this, "PDF exportado com sucesso!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + e.getMessage());
        }
    }
}
