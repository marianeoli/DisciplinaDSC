package HomePage;

import Produtos.CadastroProdutoFrame;
import Produtos.GerenciarProdutosFrame;
import Usuarios.CadastroUsuarioFrame;
import Usuarios.GerenciarUsuariosFrame;
import Vendas.RegistrarVendaFrame;
import Usuarios.Usuario;
import Relatorios.RelatorioEstoqueFrame;
import Relatorios.RelatorioFinanceiroFrame;
import Relatorios.RelatorioVendasFrame;
import Financeiro.CadastroTransacaoFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {

    private Usuario usuarioLogado;

    public MainFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("MerControle - Menu Principal");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(criarBotaoAtalho("👥 Usuários", e -> new GerenciarUsuariosFrame().setVisible(true)), gbc);
        gbc.gridx = 1;
        mainPanel.add(criarBotaoAtalho("📦 Produtos", e -> new GerenciarProdutosFrame(true).setVisible(true)), gbc);
        gbc.gridx = 2;
        mainPanel.add(criarBotaoAtalho("🛒 Vendas", e -> new RegistrarVendaFrame(usuarioLogado).setVisible(true)), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(criarBotaoAtalho("➕ Novo Usuário", e -> new CadastroUsuarioFrame().setVisible(true)), gbc);
        gbc.gridx = 1;
        mainPanel.add(criarBotaoAtalho("➕ Novo Produto", e -> new CadastroProdutoFrame().setVisible(true)), gbc);
        gbc.gridx = 2;
        mainPanel.add(criarBotaoAtalho("💰 Nova Transação", e -> new CadastroTransacaoFrame(usuarioLogado).setVisible(true)), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(criarBotaoAtalho("📊 Relatórios", e -> abrirRelatorios()), gbc);

        add(mainPanel, BorderLayout.CENTER);

        JButton sairButton = new JButton("❌ Sair");
        sairButton.setBackground(new Color(0xE74C3C));
        sairButton.setForeground(Color.WHITE);
        sairButton.setFocusPainted(false);
        sairButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sairButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sairButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente sair do sistema?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(sairButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

 private JButton criarBotaoAtalho(String texto, java.awt.event.ActionListener acao) {
    JButton botao = new JButton(texto);
    botao.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
    botao.setBackground(new Color(0x00A86B));
    botao.setForeground(Color.WHITE);
    botao.setFocusPainted(false);
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

    botao.setPreferredSize(new Dimension(220, 140));
    botao.setMinimumSize(new Dimension(220, 140));
    botao.setMaximumSize(new Dimension(220, 140));
    botao.setBorder(BorderFactory.createLineBorder(new Color(0x007A50), 2, true));

    botao.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
            botao.setBackground(new Color(0x00995C));
        }
        public void mouseExited(java.awt.event.MouseEvent e) {
            botao.setBackground(new Color(0x00A86B));
        }
    });

    botao.addActionListener(acao);
    return botao;
}

    private void abrirRelatorios() {
        Object[] opcoes = {"📦 Estoque", "💵 Financeiro", "🛒 Vendas"};
        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Escolha um relatório:",
                "Relatórios",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );
        if (escolha != null) {
            switch (escolha) {
                case "📦 Estoque" -> new RelatorioEstoqueFrame().setVisible(true);
                case "💵 Financeiro" -> new RelatorioFinanceiroFrame(8, 2025).setVisible(true);
                case "🛒 Vendas" -> new RelatorioVendasFrame().setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setTipo("Administrador");

            new MainFrame(admin).setVisible(true);
        });
    }
}
