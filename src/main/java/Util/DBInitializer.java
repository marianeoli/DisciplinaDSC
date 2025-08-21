package Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import HomePage.Database;
public class DBInitializer {

    public static void initialize() {
        try (Connection con = Database.getConnection();
             Statement stmt = con.createStatement()) {

            InputStream is = DBInitializer.class.getResourceAsStream("/mercontrole_backup_atual.sql");
            if (is == null) {
                System.out.println("Arquivo db_dump.sql não encontrado!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("--") && !line.isEmpty()) {
                    sql.append(line);
                    // Executa cada comando quando encontrar ';'
                    if (line.endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0); // limpa o buffer
                    }
                }
            }

            System.out.println("Banco de dados inicializado a partir do dump com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
