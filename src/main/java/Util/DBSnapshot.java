package Util;

import java.io.File;
import java.io.IOException;

public class DBSnapshot {

    public static void gerarDump() {
        String usuario = "root";  
        String senha = "1234";    

        String caminhoArquivo = "/mercontrole_backup_atual.sql";

        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-u" + usuario,
                "-p" + senha,
                "MerControle",
                "-r",
                caminhoArquivo
        );

        pb.redirectErrorStream(true); 

        try {
            Process processo = pb.start();
            int resultado = processo.waitFor();
            if (resultado == 0) {
                System.out.println("Dump atualizado gerado em: " + caminhoArquivo);
            } else {
                System.out.println("Erro ao gerar dump do banco. Código de saída: " + resultado);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
