package Financeiro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import HomePage.Database;

public class GerenciaFinanceira {

    public void inserir(TransacaoFinanceira t, int usuarioId) {
        String sql = "INSERT INTO transacao_financeira (data, valor, categoria, usuario_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(t.getData().getTime()));
            stmt.setFloat(2, t.getValor());
            stmt.setString(3, t.getCategoria());
            stmt.setInt(4, usuarioId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TransacaoFinanceira> listarPorMesAno(int mes, int ano) {
        List<TransacaoFinanceira> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacao_financeira WHERE MONTH(data)=? AND YEAR(data)=?";
        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TransacaoFinanceira t = new TransacaoFinanceira();
                t.setCodigo(rs.getInt("id"));
                t.setData(rs.getDate("data"));
                t.setValor(rs.getFloat("valor"));
                t.setCategoria(rs.getString("categoria"));
                lista.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public float calcularSaldo(int mes, int ano) {
        float saldo = 0;
        for (TransacaoFinanceira t : listarPorMesAno(mes, ano)) {
            if (t.getCategoria().equals("ENTRADA")) {
                saldo += t.getValor();
            } else {
                saldo -= t.getValor();
            }
        }
        return saldo;
    }
}
