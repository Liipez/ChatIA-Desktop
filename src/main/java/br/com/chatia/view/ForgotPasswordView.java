package br.com.chatia.view;

import br.com.chatia.controller.ForgotPasswordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Tela "Esqueci minha senha" em duas etapas:
 * 1) informar o e-mail e receber o código;
 * 2) informar o código e a nova senha.
 */
public class ForgotPasswordView {

    private static final String BACKGROUND = "#101116";
    private static final String CARD = "#191A22";
    private static final String GREEN = "#83ED80";
    private static final String WHITE = "#F5F5F7";
    private static final String GRAY = "#A4A6B3";
    private static final String BORDER = "#363744";

    private final ForgotPasswordController controller = new ForgotPasswordController();

    private Stage stage;
    private VBox content;
    private TextField emailField;

    public void show(Stage stage) {
        this.stage = stage;

        Label logo = new Label("✦");
        logo.setFont(Font.font("System", FontWeight.BOLD, 30));
        logo.setTextFill(Color.web(BACKGROUND));
        logo.setAlignment(Pos.CENTER);
        logo.setPrefSize(48, 48);
        logo.setStyle("-fx-background-color: " + GREEN + "; -fx-background-radius: 14;");

        Label appName = new Label("HyvexIA");
        appName.setFont(Font.font("System", FontWeight.BOLD, 23));
        appName.setTextFill(Color.web(WHITE));

        Label subtitle = new Label("DESKTOP ASSISTANT");
        subtitle.setFont(Font.font("System", 10));
        subtitle.setTextFill(Color.web(GRAY));

        HBox brand = new HBox(12, logo, new VBox(3, appName, subtitle));
        brand.setAlignment(Pos.CENTER_LEFT);

        Hyperlink backLink = new Hyperlink("← Voltar para o login");
        backLink.setTextFill(Color.web(GREEN));
        backLink.setFont(Font.font("System", FontWeight.BOLD, 12));
        backLink.setBorder(null);
        backLink.setPadding(Insets.EMPTY);
        backLink.setOnAction(e -> new LoginView().show(stage));

        HBox backBox = new HBox(backLink);
        backBox.setAlignment(Pos.CENTER);

        content = new VBox(16);
        emailField = new TextField();
        showStepEmail();

        VBox card = new VBox(25, brand, content, backBox);
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
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        stage.setTitle("HyvexIA - Recuperar senha");
        stage.setScene(new Scene(root, 900, 650));
        stage.show();
    }

    // ---------- Etapa 1: e-mail ----------
    private void showStepEmail() {
        Label title = createTitle("Esqueceu a senha?");
        Label description = createDescription(
                "Informe o e-mail da sua conta. Enviaremos um código de "
                        + "6 dígitos válido por 15 minutos.");

        emailField.setPromptText("seuemail@exemplo.com");
        styleInput(emailField);

        Button sendButton = createButton("Enviar código  →");
        ProgressIndicator loading = createLoading();

        Runnable action = () -> {
            setBusy(sendButton, loading, true);
            controller.requestCode(
                    emailField.getText(),
                    () -> {
                        setBusy(sendButton, loading, false);
                        ChatIADialog.show(stage, "Verifique seu e-mail",
                                "Se o e-mail estiver cadastrado, você receberá um código "
                                        + "em instantes. Confira também a caixa de spam.",
                                ChatIADialog.Type.INFO);
                        showStepCode();
                    },
                    error -> {
                        setBusy(sendButton, loading, false);
                        ChatIADialog.show(stage, "Não foi possível enviar",
                                error, ChatIADialog.Type.ERROR);
                    });
        };

        sendButton.setOnAction(e -> action.run());
        emailField.setOnAction(e -> action.run());

        content.getChildren().setAll(
                title, description,
                new VBox(8, createLabel("E-mail"), emailField),
                new HBox(12, sendButton, loading)
        );
        HBox.setHgrow(sendButton, Priority.ALWAYS);
    }

    // ---------- Etapa 2: código + nova senha ----------
    private void showStepCode() {
        Label title = createTitle("Redefinir senha");
        Label description = createDescription(
                "Digite o código enviado para " + emailField.getText().trim()
                        + " e escolha uma nova senha.");

        TextField codeField = new TextField();
        codeField.setPromptText("000000");
        styleInput(codeField);
        // Aceita apenas até 6 dígitos
        codeField.textProperty().addListener((obs, old, value) -> {
            if (!value.matches("\\d{0,6}")) {
                codeField.setText(old);
            }
        });

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mínimo de 8 caracteres");
        styleInput(passwordField);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Digite a senha novamente");
        styleInput(confirmField);

        Button resetButton = createButton("Redefinir senha  →");
        ProgressIndicator loading = createLoading();

        resetButton.setOnAction(e -> {
            setBusy(resetButton, loading, true);
            controller.resetPassword(
                    emailField.getText(),
                    codeField.getText(),
                    passwordField.getText(),
                    confirmField.getText(),
                    () -> {
                        setBusy(resetButton, loading, false);
                        ChatIADialog.show(stage, "Senha redefinida com sucesso",
                                "Sua senha foi alterada. Faça login com a nova senha.",
                                ChatIADialog.Type.SUCCESS);
                        new LoginView().show(stage);
                    },
                    error -> {
                        setBusy(resetButton, loading, false);
                        ChatIADialog.show(stage, "Não foi possível redefinir",
                                error, ChatIADialog.Type.ERROR);
                    });
        });

        Hyperlink resendLink = new Hyperlink("Não recebeu? Enviar novo código");
        resendLink.setTextFill(Color.web(GRAY));
        resendLink.setBorder(null);
        resendLink.setPadding(Insets.EMPTY);
        resendLink.setOnAction(e -> showStepEmail());

        content.getChildren().setAll(
                title, description,
                new VBox(8, createLabel("Código"), codeField),
                new VBox(8, createLabel("Nova senha"), passwordField),
                new VBox(8, createLabel("Confirmar nova senha"), confirmField),
                new HBox(12, resetButton, loading),
                resendLink
        );
        HBox.setHgrow(resetButton, Priority.ALWAYS);
    }

    // ---------- Auxiliares visuais ----------
    private void setBusy(Button button, ProgressIndicator loading, boolean busy) {
        button.setDisable(busy);
        loading.setVisible(busy);
    }

    private ProgressIndicator createLoading() {
        ProgressIndicator loading = new ProgressIndicator();
        loading.setPrefSize(32, 32);
        loading.setVisible(false);
        return loading;
    }

    private Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 29));
        label.setTextFill(Color.web(WHITE));
        label.setWrapText(true);
        return label;
    }

    private Label createDescription(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", 13));
        label.setTextFill(Color.web(GRAY));
        label.setWrapText(true);
        return label;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        label.setTextFill(Color.web(WHITE));
        return label;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(48);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(Color.web(BACKGROUND));
        button.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        return button;
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
}
