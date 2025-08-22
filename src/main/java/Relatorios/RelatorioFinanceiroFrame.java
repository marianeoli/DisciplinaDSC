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
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RelatorioFinanceiroFrame extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private JTextField filtroDescricao;
    private JComboBox<String> filtroTipo;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public RelatorioFinanceiroFrame(int mes, int ano) {
        setTitle("💰 Relatório Financeiro");
        setSize(900, 550);
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

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtroPanel.setBackground(new Color(34, 139, 120));
        filtroPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel tipoLabel = new JLabel("Tipo:");
        tipoLabel.setForeground(Color.WHITE);
        filtroPanel.add(tipoLabel);

        filtroTipo = new JComboBox<>(new String[]{"TODOS", "ENTRADA", "SAIDA"});
        filtroPanel.add(filtroTipo);

        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setForeground(Color.WHITE);
        filtroPanel.add(descLabel);

        filtroDescricao = new JTextField(20);
        filtroDescricao.setToolTipText("Digite a descrição");
        filtroPanel.add(filtroDescricao);

        JButton btnFiltrar = new JButton("Filtrar");
        estilizarBotao(btnFiltrar, new Color(0, 128, 0), new Color(144, 238, 144));
        btnFiltrar.addActionListener(e -> aplicarFiltro());
        filtroPanel.add(btnFiltrar);

        mainPanel.add(filtroPanel, BorderLayout.BEFORE_FIRST_LINE);

        model = new DefaultTableModel(new String[]{"ID", "Data", "Tipo", "Descrição", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(model);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(Color.BLACK);
        tabela.setGridColor(new Color(34, 139, 34));
        tabela.setSelectionBackground(new Color(60, 179, 113));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        rowSorter = new TableRowSorter<>(model);
        tabela.setRowSorter(rowSorter);

        carregarVendas(mes, ano);
        carregarSaidas(mes, ano);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(34, 139, 120));
        JButton exportarPDF = new JButton("📄 Exportar PDF");
        estilizarBotao(exportarPDF, new Color(0, 128, 0), new Color(144, 238, 144));
        exportarPDF.addActionListener(e -> exportarParaPDF(mes, ano));
        buttonPanel.add(exportarPDF);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel lblSaldo = new JLabel("Saldo do mês: R$ " + calcularSaldo());
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldo.setForeground(Color.WHITE);
        lblSaldo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainPanel.add(lblSaldo, BorderLayout.AFTER_LAST_LINE);
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

    private void aplicarFiltro() {
        String tipoSelecionado = filtroTipo.getSelectedItem().toString();
        String descFiltro = filtroDescricao.getText().trim();

        java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
        if (!tipoSelecionado.equals("TODOS")) {
            filtros.add(RowFilter.regexFilter(tipoSelecionado, 2));
        }
        if (!descFiltro.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + descFiltro, 3));
        }

        if (filtros.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filtros));
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
                        com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph titulo = new Paragraph("💰 Relatório Financeiro - " + mes + "/" + ano + "\n\n", tituloFont);
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
                                com.itextpdf.text.FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY)
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
