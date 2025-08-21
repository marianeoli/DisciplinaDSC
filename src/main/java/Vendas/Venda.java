package Vendas;

import java.util.Date;

public class Venda {
    private long codigo;
    private Date data;
    private float valorTotal;
    private long usuarioId;

    public long getCodigo() { return codigo; }
    public void setCodigo(long codigo) { this.codigo = codigo; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public float getValorTotal() { return valorTotal; }
    public void setValorTotal(float valorTotal) { this.valorTotal = valorTotal; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }
}
