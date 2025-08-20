package Relatorios;

import Financeiro.GerenciaFinanceira;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import Financeiro.TransacaoFinanceira;

public class RelatorioFinanceiroFrame extends JFrame {

    private JTable tabela;
    private JLabel lblSaldo;

    public RelatorioFinanceiroFrame(int mes, int ano) {
        setTitle("Relatório Financeiro - " + mes + "/" + ano);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Código correto:
        GerenciaFinanceira dao = new GerenciaFinanceira();
        List<TransacaoFinanceira> transacoes = dao.listarPorMesAno(mes, ano);

        String[] colunas = {"Código", "Data", "Valor", "Categoria"};
        Object[][] dados = new Object[transacoes.size()][4];
        for (int i = 0; i < transacoes.size(); i++) {
            TransacaoFinanceira t = transacoes.get(i);
            dados[i][0] = t.getCodigo();
            dados[i][1] = t.getData();
            dados[i][2] = t.getValor();
            dados[i][3] = t.getCategoria();
        }

        tabela = new JTable(dados, colunas);
        JScrollPane scroll = new JScrollPane(tabela);

        float saldo = dao.calcularSaldo(mes, ano);
        lblSaldo = new JLabel("Saldo do mês: R$ " + saldo);

        add(scroll, BorderLayout.CENTER);
        add(lblSaldo, BorderLayout.SOUTH);
    }
}
