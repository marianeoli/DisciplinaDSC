package Relatorios;

import HomePage.Database;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioVendasFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public RelatorioVendasFrame() {
        setTitle("Relatório de Vendas");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120));
        add(mainPanel);

        JLabel titleLabel = new JLabel("📊 Relatório de Vendas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Data", "Produto", "Quantidade", "Total"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        carregarDados();

        JButton exportarPDF = new JButton("Exportar PDF");
        exportarPDF.addActionListener(e -> exportarParaPDF());
        mainPanel.add(exportarPDF, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT v.id, v.data, vi.quantidade, vi.precoUnitario, p.nome AS produto, "
                    + "(vi.quantidade * vi.precoUnitario) AS total "
                    + "FROM vendaItens vi "
                    + "JOIN vendas v ON vi.vendaId = v.id "
                    + "JOIN produtos p ON vi.produtoId = p.id "
                    + "ORDER BY v.data DESC;";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getTimestamp("data"),
                    rs.getString("produto"),
                    rs.getInt("quantidade"),
                    rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas.", "Erro", JOptionPane.ERROR_MESSAGE);
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
                double totalGeral = 0;
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
                    totalGeral += ((Number) model.getValueAt(i, 4)).doubleValue();
                }

                doc.add(tabelaPDF);

                Paragraph rodape = new Paragraph(
                        "Total Geral: R$ " + totalGeral,
                        com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY)
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
