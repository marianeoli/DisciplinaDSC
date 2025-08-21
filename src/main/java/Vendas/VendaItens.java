package Vendas;

public class VendaItens {
    private long idVenda;
    private long idProduto;
    private String descricao;
    private int quantidade;
    private float precoUnitario;

    public long getIdVenda() { return idVenda; }
    public void setIdVenda(long idVenda) { this.idVenda = idVenda; }

    public long getIdProduto() { return idProduto; }
    public void setIdProduto(long idProduto) { this.idProduto = idProduto; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public float getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(float precoUnitario) { this.precoUnitario = precoUnitario; }
}
