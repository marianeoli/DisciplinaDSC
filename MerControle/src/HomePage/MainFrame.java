package HomePage;

import javax.swing.*;
import Usuarios.CadastroUsuarioFrame;
import Usuarios.GerenciarUsuariosFrame;
import Produtos.CadastroProdutoFrame;
import Produtos.GerenciarProdutosFrame;

public class MainFrame extends JFrame {

    private String tipoUsuario; // Administrador ou Funcionario

    public MainFrame(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;

        setTitle("MerControle - Menu Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();

        // Menu Gerenciar
        JMenu menuGerenciar = new JMenu("Gerenciar");

        // Submenu Usuários
        JMenu submenuUsuarios = new JMenu("Usuários");
        JMenuItem cadastrarUsuario = new JMenuItem("Cadastrar");
        JMenuItem listarUsuarios = new JMenuItem("Listar");
        submenuUsuarios.add(cadastrarUsuario);
        submenuUsuarios.add(listarUsuarios);

        // Submenu Produtos
        JMenu submenuProdutos = new JMenu("Produtos");
        JMenuItem cadastrarProduto = new JMenuItem("Cadastrar");
        JMenuItem listarProduto = new JMenuItem("Listar");
        
        submenuProdutos.add(cadastrarProduto);
        submenuProdutos.add(listarProduto);

        menuGerenciar.add(submenuUsuarios);
        menuGerenciar.add(submenuProdutos);

        // Menu Vendas
        JMenu menuVendas = new JMenu("Vendas");
        JMenuItem novaVenda = new JMenuItem("Nova Venda");
        menuVendas.add(novaVenda);

        // Menu Relatórios
        JMenu menuRelatorios = new JMenu("Relatórios");
        JMenuItem relFinanceiro = new JMenuItem("Financeiro");
        menuRelatorios.add(relFinanceiro);

        menuBar.add(menuGerenciar);
        menuBar.add(menuVendas);
        menuBar.add(menuRelatorios);

        setJMenuBar(menuBar);

        // ⚠️ Controle de permissões
        if(tipoUsuario.equals("Funcionario")) {
            // Funcionário não pode acessar usuários nem relatórios
            submenuUsuarios.setVisible(false);
            listarUsuarios.setVisible(false);
            menuRelatorios.setVisible(false);
        }

        // Ações
        cadastrarUsuario.addActionListener(e -> new CadastroUsuarioFrame().setVisible(true));
        listarUsuarios.addActionListener(e -> new GerenciarUsuariosFrame().setVisible(true));
        cadastrarProduto.addActionListener(e -> new CadastroProdutoFrame().setVisible(true));
        listarProduto.addActionListener(e -> new GerenciarProdutosFrame(tipoUsuario.equals("Administrador")).setVisible(true));
        novaVenda.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir tela de vendas"));
        relFinanceiro.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir relatório financeiro"));
    }
}
