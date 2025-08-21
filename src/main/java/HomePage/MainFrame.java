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
    private final Color VERDE_ESCURO = new Color(0x0B420E);

    public MainFrame(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("MerControle - Menu Principal");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); // Fundo branco

        // --- Faixa verde na parte superior ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(VERDE_ESCURO);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JLabel welcomeLabel = new JLabel("Bem-vindo(a) ao MerControle, " + usuarioLogado.getNome() + "!");
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 36)); // Fonte moderna
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonGridPanel = new JPanel(new GridLayout(2, 3, 50, 50));
        buttonGridPanel.setBackground(Color.WHITE);
        buttonGridPanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 20, 80));

        if ("Administrador".equalsIgnoreCase(usuarioLogado.getTipo())) {
            buttonGridPanel.add(criarBotaoAtalho("👤 Gestão de Usuários", "icons/user.png", e -> new GerenciarUsuariosFrame().setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("🛍️ Controle de Produtos", "icons/product.png", e -> new GerenciarProdutosFrame(true).setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("🛒 Registro de Vendas", "icons/sale.png", e -> new RegistrarVendaFrame(usuarioLogado).setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("➕ Novo Usuário", "icons/add_user.png", e -> new CadastroUsuarioFrame().setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("📦 Novo Produto", "icons/add_product.png", e -> new CadastroProdutoFrame().setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("💰 Registro Financeiro", "icons/finance.png", e -> new CadastroTransacaoFrame(usuarioLogado).setVisible(true)));
        } else if ("Funcionario".equalsIgnoreCase(usuarioLogado.getTipo())) {
            buttonGridPanel.add(criarBotaoAtalho("🛍️ Controle de Produtos", "icons/product.png", e -> new GerenciarProdutosFrame(true).setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("🛒 Registro de Vendas", "icons/sale.png", e -> new RegistrarVendaFrame(usuarioLogado).setVisible(true)));
            buttonGridPanel.add(criarBotaoAtalho("📦 Relatório de Estoque", "icons/stock.png", e -> new RelatorioEstoqueFrame().setVisible(true)));
            buttonGridPanel.add(new JPanel() {{ setBackground(Color.WHITE); }});
            buttonGridPanel.add(new JPanel() {{ setBackground(Color.WHITE); }});
            buttonGridPanel.add(new JPanel() {{ setBackground(Color.WHITE); }});
        }

        mainPanel.add(buttonGridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        bottomPanel.setBackground(Color.WHITE);

        JButton relatoriosButton = criarBotaoAcao("📊 Relatórios", null, new Color(41, 128, 185)); 
        relatoriosButton.addActionListener(e -> abrirRelatorios());
        
        JButton sairButton = criarBotaoAcao("🚪 Sair", null, new Color(231, 76, 60));
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

        if ("Administrador".equalsIgnoreCase(usuarioLogado.getTipo())) {
            bottomPanel.add(relatoriosButton);
        }
        bottomPanel.add(sairButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton criarBotaoAtalho(String texto, String caminhoIcone, java.awt.event.ActionListener acao) {
        ImageIcon originalIcon = new ImageIcon(caminhoIcone);
        Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton botao = new JButton("<html><center>" + texto.replace(" ", "<br>") + "</center></html>", scaledIcon);
        botao.setVerticalTextPosition(SwingConstants.BOTTOM);
        botao.setHorizontalTextPosition(SwingConstants.CENTER);
        botao.setFont(new Font("Inter", Font.BOLD, 18));
        botao.setForeground(Color.WHITE);
        botao.setBackground(VERDE_ESCURO);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(220, 150));
        botao.putClientProperty("FlatLaf.style", "arc: 40");

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(VERDE_ESCURO.darker());
            }
            public void mouseExited(MouseEvent e) {
                botao.setBackground(VERDE_ESCURO);
            }
        });

        botao.addActionListener(acao);
        return botao;
    }

    private JButton criarBotaoAcao(String texto, String caminhoIcone, Color cor) {
        ImageIcon originalIcon = new ImageIcon(caminhoIcone);
        Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton botao = new JButton(texto, scaledIcon);
        botao.setFont(new Font("Inter", Font.BOLD, 16)); 
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        botao.putClientProperty("FlatLaf.style", "arc: 10");

        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(cor.darker());
            }
            public void mouseExited(MouseEvent e) {
                botao.setBackground(cor);
            }
        });
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
}