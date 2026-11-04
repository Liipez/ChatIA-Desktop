
package br.com.chatia;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.AuthenticationService;
import br.com.chatia.service.UserService;

public class AuthTest {

    public static void main(String[] args) {
        try {
            UserRepository repo = new UserRepository();
            UserService userService = new UserService(repo);
            AuthenticationService auth = new AuthenticationService(repo);

            String email = "teste." + System.currentTimeMillis()
                    + "@email.com";

            userService.register(
                    "Teste",
                    email,
                    "senha1234",
                    "senha1234"
            );
            System.out.println("Cadastro OK: " + email);

            System.out.println(
                    "Login OK: "
                            + auth.login(email.toUpperCase(), "senha1234")
                            .getName()
            );

            try {
                auth.login(email, "errada123");
                System.out.println("ERRO: senha incorreta foi aceita.");
            } catch (IllegalArgumentException e) {
                System.out.println("Senha errada: " + e.getMessage());
            }

            try {
                userService.register(
                        "Teste",
                        email,
                        "senha1234",
                        "senha1234"
                );
                System.out.println("ERRO: e-mail duplicado foi aceito.");
            } catch (IllegalArgumentException e) {
                System.out.println("Duplicado: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoConfig.closeConnection();
        }
    }
}
