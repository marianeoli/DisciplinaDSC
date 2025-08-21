package Produtos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

import HomePage.Database;

public class CadastroProdutoFrame extends JFrame {

    private JTextField nomeField, precoCompraField, precoVendaField, estoqueField;

    public CadastroProdutoFrame() {
        setTitle("Cadastro de Produto");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        
        JPanel painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x00A86B), 
                        0, getHeight(), new Color(0x006B46) 
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        painelPrincipal.setLayout(new GridBagLayout());
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(painelPrincipal);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);

        
        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setForeground(Color.WHITE);
        nomeLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        painelPrincipal.add(nomeLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        nomeField = new JTextField(18);
        nomeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nomeField.setForeground(Color.BLACK);
        nomeField.setBackground(Color.WHITE);
        nomeField.setBorder(BorderFactory.createLineBorder(new Color(0x006B46), 2));
        nomeField.putClientProperty("JTextField.placeholderText", "Digite o nome do produto"); 
        painelPrincipal.add(nomeField, gbc);

        
        JLabel precoCompraLabel = new JLabel("Preço de Compra:");
        precoCompraLabel.setForeground(Color.WHITE);
        precoCompraLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1;
        painelPrincipal.add(precoCompraLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        precoCompraField = new JTextField(18);
        precoCompraField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        precoCompraField.setForeground(Color.BLACK);
        precoCompraField.setBackground(Color.WHITE);
        precoCompraField.setBorder(BorderFactory.createLineBorder(new Color(0x006B46), 2));
        precoCompraField.putClientProperty("JTextField.placeholderText", "Digite o preço de compra");
        painelPrincipal.add(precoCompraField, gbc);

        
        JLabel precoVendaLabel = new JLabel("Preço de Venda:");
        precoVendaLabel.setForeground(Color.WHITE);
        precoVendaLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2;
        painelPrincipal.add(precoVendaLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        precoVendaField = new JTextField(18);
        precoVendaField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        precoVendaField.setForeground(Color.BLACK);
        precoVendaField.setBackground(Color.WHITE);
        precoVendaField.setBorder(BorderFactory.createLineBorder(new Color(0x006B46), 2));
        precoVendaField.putClientProperty("JTextField.placeholderText", "Digite o preço de venda");
        painelPrincipal.add(precoVendaField, gbc);

       
        JLabel estoqueLabel = new JLabel("Quantidade em Estoque:");
        estoqueLabel.setForeground(Color.WHITE);
        estoqueLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 3;
        painelPrincipal.add(estoqueLabel, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        estoqueField = new JTextField(18);
        estoqueField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        estoqueField.setForeground(Color.BLACK);
        estoqueField.setBackground(Color.WHITE);
        estoqueField.setBorder(BorderFactory.createLineBorder(new Color(0x006B46), 2));
        estoqueField.putClientProperty("JTextField.placeholderText", "Digite a quantidade");
        painelPrincipal.add(estoqueField, gbc);

        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton salvarButton = new JButton("Salvar");
        salvarButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        salvarButton.setForeground(Color.WHITE);
        salvarButton.setBackground(new Color(0x00A86B));
        salvarButton.setFocusPainted(false);
        salvarButton.setBorder(BorderFactory.createLineBorder(new Color(0x006B46), 2));
        salvarButton.setPreferredSize(new Dimension(150, 40));
        painelPrincipal.add(salvarButton, gbc);

        salvarButton.addActionListener(e -> salvarProduto());
    }

    private String gerarCodigoProduto(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < tamanho; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    private void salvarProduto() {
        String codigo = gerarCodigoProduto(8);
        String nome = nomeField.getText();
        String precoCompra = precoCompraField.getText().trim().replace(",", ".");
        String precoVenda = precoVendaField.getText().trim().replace(",", ".");
        String estoque = estoqueField.getText();

        if (nome.isEmpty() || precoCompra.isEmpty() || precoVenda.isEmpty() || estoque.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            java.math.BigDecimal precoCompraBD = new java.math.BigDecimal(precoCompra);
            java.math.BigDecimal precoVendaBD = new java.math.BigDecimal(precoVenda);
            int estoqueInt = Integer.parseInt(estoque);

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO produtos (codigo, nome, preco_compra, preco_venda, estoque) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, codigo);
                stmt.setString(2, nome);
                stmt.setBigDecimal(3, precoCompraBD);
                stmt.setBigDecimal(4, precoVendaBD);
                stmt.setInt(5, estoqueInt);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
                dispose();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
