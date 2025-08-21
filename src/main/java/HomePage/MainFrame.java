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

import javax.swing.*;
import java.awt.*;
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

   
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(new Color(245, 245, 245));

        mainPanel.add(criarBotaoAtalho("👥 Usuários", e -> new GerenciarUsuariosFrame().setVisible(true)));
        mainPanel.add(criarBotaoAtalho("📦 Produtos", e -> new GerenciarProdutosFrame("Administrador".equals(usuarioLogado.getTipo())).setVisible(true)));
        mainPanel.add(criarBotaoAtalho("🛒 Vendas", e -> new RegistrarVendaFrame(usuarioLogado).setVisible(true)));
        mainPanel.add(criarBotaoAtalho("➕ Novo Produto", e -> new CadastroProdutoFrame().setVisible(true)));
        mainPanel.add(criarBotaoAtalho("➕ Novo Usuário", e -> new CadastroUsuarioFrame().setVisible(true)));
        mainPanel.add(criarBotaoAtalho("📊 Relatórios", e -> {
            
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
        }));

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton criarBotaoAtalho(String texto, java.awt.event.ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        botao.setBackground(new Color(0x00A86B)); // verde lésbico
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addActionListener(acao);
        return botao;
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
