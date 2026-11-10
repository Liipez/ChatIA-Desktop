
package br.com.chatia.view;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import br.com.chatia.view.MainView;
import javafx.geometry.Pos;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.AuthenticationService;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

    private static final String BACKGROUND = "#101116";
    private static final String CARD = "#191A22";
    private static final String GREEN = "#83ED80";
    private static final String WHITE = "#F5F5F7";
    private static final String GRAY = "#A4A6B3";
    private static final String BORDER = "#363744";

    public void show(Stage stage) {

        Label logo = new Label("✦");
        logo.setFont(Font.font("System", FontWeight.BOLD, 30));
        logo.setTextFill(Color.web(BACKGROUND));
        logo.setAlignment(Pos.CENTER);
        logo.setPrefSize(48, 48);
        logo.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 14;"
        );

        Label appName = new Label("HyvexIA");
        appName.setFont(Font.font("System", FontWeight.BOLD, 23));
        appName.setTextFill(Color.web(WHITE));

        Label subtitle = new Label("DESKTOP ASSISTANT");
        subtitle.setFont(Font.font("System", 10));
        subtitle.setTextFill(Color.web(GRAY));

        VBox brandText = new VBox(3, appName, subtitle);
        HBox brand = new HBox(12, logo, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Bem-vindo de volta.");
        title.setFont(Font.font("System", FontWeight.BOLD, 29));
        title.setTextFill(Color.web(WHITE));
        title.setWrapText(true);

        Label description = new Label(
                "Entre na sua conta para continuar suas\n" +
                        "conversas com inteligência artificial."
        );
        description.setFont(Font.font("System", 13));
        description.setTextFill(Color.web(GRAY));

        Label emailLabel = createLabel("E-mail");
        TextField emailField = new TextField();
        emailField.setPromptText("seuemail@exemplo.com");
        styleInput(emailField);

        Label passwordLabel = createLabel("Senha");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Digite sua senha");
        styleInput(passwordField);

        Hyperlink forgotPassword =
                new Hyperlink("Esqueceu a senha?");
        forgotPassword.setTextFill(Color.web(GREEN));
        forgotPassword.setBorder(null);
        forgotPassword.setPadding(Insets.EMPTY);

        forgotPassword.setOnAction(event ->
                new ForgotPasswordView().show(stage)
        );

        Button loginButton =
                new Button("Entrar na minha conta  →");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(48);
        loginButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginButton.setTextFill(Color.web(BACKGROUND));
        loginButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );


        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showMessage(
                        stage,
                        "Campos obrigatórios",
                        "Preencha o e-mail e a senha para continuar."
                );
                return;
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                ChatIADialog.show(
                        stage,
                        "E-mail inválido",
                        "Digite um e-mail válido, como exemplo@email.com.",
                        ChatIADialog.Type.ERROR
                );
                return;
            }

            if (password.length() < 8) {
                ChatIADialog.show(
                        stage,
                        "Campos obrigatórios",
                        "Preencha o e-mail e a senha para continuar.",
                        ChatIADialog.Type.WARNING
                );
                return;
            }

            try {
                UserRepository repository = new UserRepository();
                AuthenticationService auth =
                        new AuthenticationService(repository);

                auth.login(email, password);

                new MainView().show(stage)
                ;

                // Próxima etapa: abrir a tela principal do ChatIA.

            } catch (IllegalArgumentException e) {
                showMessage(
                        stage,
                        "Não foi possível entrar",
                        e.getMessage()
                );

            } catch (Exception e) {
                showMessage(
                        stage,
                        "Erro de conexão",
                        "Não foi possível realizar o login. Verifique a conexão com o banco de dados."
                );

                e.printStackTrace();
            }
        });


        Label accountText = createLabel("Ainda não tem uma conta?");
        accountText.setFont(Font.font("System", 12));
        accountText.setTextFill(Color.web(GRAY));

        Hyperlink registerLink = new Hyperlink("Criar conta");
        registerLink.setTextFill(Color.web(GREEN));
        registerLink.setFont(Font.font("System", FontWeight.BOLD, 12));
        registerLink.setBorder(null);
        registerLink.setPadding(Insets.EMPTY);

        registerLink.setOnAction(event ->
                new RegisterView().show(stage)
        );

        HBox registerBox = new HBox(4, accountText, registerLink);
        registerBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(
                10,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                forgotPassword,
                loginButton
        );

        VBox card = new VBox(
                25,
                brand,
                title,
                description,
                form,
                registerBox
        );

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36));
        card.setMaxWidth(440);
        card.setPrefWidth(440);
        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 20;"
        );

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(24));
        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        Scene scene = new Scene(root, 900, 650);

        stage.setTitle("Hyvex IA - Login");
        stage.setMinWidth(500);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        label.setTextFill(Color.web(WHITE));
        return label;
    }

    private void styleInput(TextField field) {
        field.setPrefHeight(46);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setFont(Font.font("System", 13));
        field.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-prompt-text-fill: #777B89;" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-padding: 0 12;"
        );
    }

    private void showMessage(
            Stage stage, String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
