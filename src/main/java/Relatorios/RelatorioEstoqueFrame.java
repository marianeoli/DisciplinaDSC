package Relatorios;

import HomePage.Database;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
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

public class RelatorioEstoqueFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public RelatorioEstoqueFrame() {
        setTitle("Relatório de Estoque");
        setSize(700, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120));
        add(mainPanel);

        JLabel titleLabel = new JLabel("📦 Relatório de Estoque", SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Código", "Produto", "Quantidade", "Preço Venda"}, 0);
        table = new JTable(model);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
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
            String sql = "SELECT id, codigo, nome, estoque, preco_venda "
                    + "FROM produtos ORDER BY estoque ASC";

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

                Document doc = new Document();
                PdfWriter.getInstance(doc, new java.io.FileOutputStream(caminho));
                doc.open();

                Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph("📦 Relatório de Estoque\n\n", tituloFont);
                titulo.setAlignment(Element.ALIGN_CENTER);
                doc.add(titulo);

                PdfPTable tabelaPDF = new PdfPTable(model.getColumnCount());
                tabelaPDF.setWidthPercentage(100);
                tabelaPDF.setSpacingBefore(10f);
                tabelaPDF.setSpacingAfter(10f);

                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell header = new PdfPCell(new Paragraph(model.getColumnName(i), headFont));
                    header.setBackgroundColor(new BaseColor(0, 128, 0));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    tabelaPDF.addCell(header);
                }

                Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
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

                Paragraph rodape = new Paragraph(
                        "Total de produtos listados: " + model.getRowCount(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY)
                );
                rodape.setAlignment(Element.ALIGN_RIGHT);
                doc.add(rodape);

                doc.close();
                JOptionPane.showMessageDialog(this, "PDF exportado com sucesso!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage());
        }
    }
}
