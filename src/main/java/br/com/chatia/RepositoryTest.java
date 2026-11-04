
package br.com.chatia;

import br.com.chatia.repository.UserRepository;

public class RepositoryTest {

    public static void main(String[] args) {
        try {
            UserRepository repository = new UserRepository();

            long total = repository.countUsers();

            System.out.println("Conexão com o banco funcionando!");
            System.out.println("Total de usuários: " + total);

        } catch (Exception e) {
            System.out.println("Não foi possível acessar o banco.");
            System.out.println("Erro: " + e.getMessage());

        }
    }
}
