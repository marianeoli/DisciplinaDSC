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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioFinanceiroFrame extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private JLabel lblSaldo;

    public RelatorioFinanceiroFrame(int mes, int ano) {
        setTitle("Relatório Financeiro - " + mes + "/" + ano);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 120));
        add(mainPanel);

        JLabel titleLabel = new JLabel("💰 Relatório Financeiro", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Data", "Tipo", "Descrição", "Valor"}, 0);
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        carregarVendas(mes, ano);
        carregarSaidas(mes, ano);

        float saldo = calcularSaldo();
        lblSaldo = new JLabel("Saldo do mês: R$ " + saldo);
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainPanel.add(lblSaldo, BorderLayout.SOUTH);

        JButton exportarPDF = new JButton("Exportar PDF");
        exportarPDF.addActionListener(e -> exportarParaPDF(mes, ano));
        mainPanel.add(exportarPDF, BorderLayout.SOUTH);
    }

    private void carregarVendas(int mes, int ano) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT v.id, v.data, SUM(vi.quantidade * vi.precoUnitario) AS total_venda "
                    + "FROM vendas v "
                    + "JOIN vendaItens vi ON v.id = vi.vendaId "
                    + "WHERE MONTH(v.data) = ? AND YEAR(v.data) = ? "
                    + "GROUP BY v.id, v.data ORDER BY v.data ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idVenda = rs.getInt("id");
                Date data = rs.getDate("data");
                float totalVenda = rs.getFloat("total_venda");
                model.addRow(new Object[]{idVenda, data, "ENTRADA", "Venda de produtos", totalVenda});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar vendas: " + e.getMessage());
        }
    }

    private void carregarSaidas(int mes, int ano) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, data, valor, descricao FROM transacaoFinanceira "
                    + "WHERE categoria='SAIDA' AND MONTH(data)=? AND YEAR(data)=? ORDER BY data ASC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date data = rs.getDate("data");
                float valor = rs.getFloat("valor");
                String descricao = rs.getString("descricao");
                model.addRow(new Object[]{id, data, "SAIDA", descricao, valor});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float calcularSaldo() {
        float saldo = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String tipo = (String) model.getValueAt(i, 2);
            float valor = ((Number) model.getValueAt(i, 4)).floatValue();
            if (tipo.equals("ENTRADA")) {
                saldo += valor;
            } else if (tipo.equals("SAIDA")) {
                saldo -= valor;
            }
        }
        return saldo;
    }

    private void exportarParaPDF(int mes, int ano) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("relatorio_financeiro_" + mes + "_" + ano + ".pdf"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();

                Document doc = new Document();
                PdfWriter.getInstance(doc, new java.io.FileOutputStream(caminho));
                doc.open();

                com.itextpdf.text.Font tituloFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.BaseColor.BLACK);
                Paragraph titulo = new Paragraph("💰 Relatório Financeiro - " + mes + "/" + ano + "\n\n", tituloFont);
                titulo.setAlignment(Element.ALIGN_CENTER);
                doc.add(titulo);

                PdfPTable tabelaPDF = new PdfPTable(model.getColumnCount());
                tabelaPDF.setWidthPercentage(100);
                tabelaPDF.setSpacingBefore(10f);
                tabelaPDF.setSpacingAfter(10f);

                com.itextpdf.text.Font headFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.BaseColor.WHITE);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell header = new PdfPCell(new Paragraph(model.getColumnName(i), headFont));
                    header.setBackgroundColor(new BaseColor(0, 128, 0));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    tabelaPDF.addCell(header);
                }

                com.itextpdf.text.Font cellFont = com.itextpdf.text.FontFactory.getFont(
                        com.itextpdf.text.FontFactory.HELVETICA, 11, com.itextpdf.text.BaseColor.BLACK);
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
                        "Saldo do mês: R$ " + calcularSaldo(),
                        com.itextpdf.text.FontFactory.getFont(
                                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, com.itextpdf.text.BaseColor.DARK_GRAY)
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
