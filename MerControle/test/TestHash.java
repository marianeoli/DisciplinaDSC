/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mariane
 */
public class TestHash {
    public static void main(String[] args) {
        String senha = "admin123";
        String hash = Usuarios.Utils.hashSenha(senha);
        System.out.println(hash);
    }
}
