package HomePage;

import javax.swing.*;
import java.awt.*;
import Usuarios.CadastroUsuarioFrame;
import Usuarios.GerenciarUsuariosFrame;
import Produtos.CadastroProdutoFrame;
import Produtos.GerenciarProdutosFrame;
import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {

    private String tipoUsuario;

    public MainFrame(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        // ⚡ Aplica tema FlatLaf moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("MerControle - Menu Principal");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MenuBar estilizado
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Menus com cores e fontes mais modernas
        Font menuFont = new Font("Segoe UI", Font.BOLD, 14);
        Font itemFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Menu Gerenciar
        JMenu menuGerenciar = new JMenu("Gerenciar");
        menuGerenciar.setFont(menuFont);

        JMenu submenuUsuarios = new JMenu("Usuários");
        submenuUsuarios.setFont(itemFont);
        JMenuItem cadastrarUsuario = new JMenuItem("Cadastrar");
        JMenuItem listarUsuarios = new JMenuItem("Listar");
        cadastrarUsuario.setFont(itemFont);
        listarUsuarios.setFont(itemFont);
        submenuUsuarios.add(cadastrarUsuario);
        submenuUsuarios.add(listarUsuarios);

        JMenu submenuProdutos = new JMenu("Produtos");
        submenuProdutos.setFont(itemFont);
        JMenuItem cadastrarProduto = new JMenuItem("Cadastrar");
        JMenuItem listarProduto = new JMenuItem("Listar");
        cadastrarProduto.setFont(itemFont);
        listarProduto.setFont(itemFont);
        submenuProdutos.add(cadastrarProduto);
        submenuProdutos.add(listarProduto);

        menuGerenciar.add(submenuUsuarios);
        menuGerenciar.add(submenuProdutos);

        // Menu Vendas
        JMenu menuVendas = new JMenu("Vendas");
        menuVendas.setFont(menuFont);
        JMenuItem novaVenda = new JMenuItem("Nova Venda");
        novaVenda.setFont(itemFont);
        menuVendas.add(novaVenda);

        // Menu Relatórios
        JMenu menuRelatorios = new JMenu("Relatórios");
        menuRelatorios.setFont(menuFont);
        JMenuItem relFinanceiro = new JMenuItem("Financeiro");
        relFinanceiro.setFont(itemFont);
        menuRelatorios.add(relFinanceiro);

        menuBar.add(menuGerenciar);
        menuBar.add(menuVendas);
        menuBar.add(menuRelatorios);

        setJMenuBar(menuBar);

        // Controle de permissões
        if("Funcionario".equals(tipoUsuario)) {
            submenuUsuarios.setVisible(false);
            listarUsuarios.setVisible(false);
            menuRelatorios.setVisible(false);
        }

        // Painel principal central
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bem-vindo ao MerControle", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(45, 45, 45));
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(mainPanel);

        // Ações
        cadastrarUsuario.addActionListener(e -> new CadastroUsuarioFrame().setVisible(true));
        listarUsuarios.addActionListener(e -> new GerenciarUsuariosFrame().setVisible(true));
        cadastrarProduto.addActionListener(e -> new CadastroProdutoFrame().setVisible(true));
        listarProduto.addActionListener(e -> new GerenciarProdutosFrame("Administrador".equals(tipoUsuario)).setVisible(true));
        novaVenda.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir tela de vendas"));
        relFinanceiro.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir relatório financeiro"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame("Administrador").setVisible(true));
    }
}
