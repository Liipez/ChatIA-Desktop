package br.com.chatia.view;

import javafx.geometry.Insets;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.UserService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class RegisterView {
    private static final String BACKGROUND = "#101116";
    private static final String CARD = "#191A22";
    private static final String GREEN = "#83ED80";
    private static final String WHITE = "#F5F5F7";
    private static final String GRAY = "#A4A6B3";
    private static final String BORDER = "#363744";

    public void show(Stage stage){
        // LOGO
        Label logo = new Label("777");
        logo.setFont(Font.font("System",FontWeight.BOLD, 30));
        logo.setTextFill(Color.web(BACKGROUND));
        logo.setAlignment(Pos.CENTER);
        logo.setPrefSize(48,48);
        logo.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 14; "
        );

        Label appName = new Label("ChatIA");
        appName.setFont(Font.font("System", FontWeight.BOLD, 23));
        appName.setTextFill(Color.web(WHITE));

        Label subtitle = new Label("DESKTOP ASSISTANT");
        subtitle.setFont(Font.font("System", 10));
        subtitle.setTextFill(Color.web(GREEN));

        VBox brandText = new VBox(3,appName, subtitle );
        HBox brand = new HBox(12, logo, brandText);
        brand.setAlignment(Pos.CENTER_LEFT);

        //Título
        Label title = new Label("Crie sua conta");
        title.setFont(Font.font("System", FontWeight.BOLD,28));
        title.setTextFill(Color.web(WHITE));
        Label description = new Label(
                "Comece sua jornada com inteligência artificial."
        );
        description.setFont(Font.font("System", 13));
        description.setTextFill(Color.web(GRAY));
        description.setWrapText(true);

        //CAMPO DE NOME
        Label nameLabel = createLabel("Nome completo");

        TextField nameField = new TextField();
        nameField.setPromptText("Seu nome completo");
        styleInput(nameField);

        //CAMPO DE EMAIL
        Label emailLabel = createLabel("E-mail");
        TextField emailField = new TextField();
        emailField.setPromptText("seuemail@exemplo.com");
        styleInput(emailField);

        // Senha
        Label passwordLabel = createLabel("Senha");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mínimo de 8 caracteres");
        styleInput(passwordField);

        // Confirmação de senha
        Label confirmLabel = createLabel("Confirmar senha");

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Digite a senha novamente");
        styleInput(confirmField);

        //BUTAO DE CADASTRO
        Button registerButton = new Button("Criar minha conta →");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(48);
        registerButton.setFont(
                Font.font("System", FontWeight.BOLD, 14)
        );
        registerButton.setTextFill(Color.web(BACKGROUND));
        registerButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        registerButton.setOnMouseEntered(event ->
                registerButton.setStyle(
                        "-fx-background-color: #A0F59D;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                ));

        registerButton.setOnMouseExited(event ->
                registerButton.setStyle(
                        "-fx-background-color: " + GREEN + ";" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                )
        );


        registerButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmField.getText();

            try {
                UserRepository repository = new UserRepository();
                UserService userService = new UserService(repository);

                userService.register(
                        name,
                        email,
                        password,
                        confirmPassword
                );

                showMessage(
                        stage,
                        "Cadastro realizado!",
                        "Sua conta foi criada com sucesso. Agora você já pode fazer login."
                );

                new LoginView().show(stage);

            } catch (IllegalArgumentException e) {
                showMessage(
                        stage,
                        "Não foi possível cadastrar",
                        e.getMessage()
                );

            } catch (Exception e) {
                showMessage(
                        stage,
                        "Erro no cadastro",
                        "Não foi possível concluir o cadastro. Verifique a conexão com o banco de dados."
                );

                e.printStackTrace();
            }
        });


        //Link para voltar ao login
        Label accountText = new Label("Já tem conta?");
        accountText.setTextFill(Color.web(GRAY));
        accountText.setFont(Font.font("System", FontWeight.BOLD, 12));

        Hyperlink loginLink = new Hyperlink("Fazer login");
        loginLink.setTextFill(Color.web(GREEN));
        loginLink.setFont(Font.font("System", FontWeight.BOLD, 12));
        loginLink.setBorder(null);
        loginLink.setPadding(Insets.EMPTY);

        loginLink.setOnAction(event ->
                new LoginView().show(stage)
        );

        HBox loginBox = new HBox(4, accountText, loginLink);
        loginBox.setAlignment(Pos.CENTER);

        //ORGANIZAÇÃO DOS CAMPOS
        VBox nameBox = new VBox(8, nameLabel, nameField);
        VBox emailBox = new VBox(8, emailLabel, emailField);
        VBox passwordBox = new VBox(8, passwordLabel, passwordField);
        VBox confirmBox = new VBox(8, confirmLabel, confirmField);

        HBox passwordRow = new HBox(14, passwordBox, confirmBox);
        HBox.setHgrow(passwordBox, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(confirmBox, javafx.scene.layout.Priority.ALWAYS);

        passwordBox.setMaxWidth(Double.MAX_VALUE);
        confirmBox.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(
                16,
                nameBox,
                emailBox,
                passwordRow,
                registerButton
        );

        VBox card = new VBox(
                24,
                brand,
                title,
                description,
                form,
                loginBox

        );
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(34));
        card.setMaxWidth(520);
        card.setPrefWidth(520);
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

        Scene scene = new Scene(root, 900, 760);

        stage.setTitle("ChatIA - Criar conta");
        stage.setMinWidth(600);
        stage.setMinHeight(700);
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

        ChatIADialog.Type type;

        if (title.toLowerCase().contains("sucesso")) {
            type = ChatIADialog.Type.SUCCESS;
        } else if (title.toLowerCase().contains("conexão")) {
            type = ChatIADialog.Type.ERROR;
        } else {
            type = ChatIADialog.Type.ERROR;
        }

        ChatIADialog.show(
                stage,
                title,
                message,
                type
        );
    }
}
