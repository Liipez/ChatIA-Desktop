package br.com.chatia.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TextField;
import java.util.Optional;
import javafx.stage.Window;

public class ChatIADialog {

    private static final String BACKGROUND = "#101116";
    private static final String CARD = "#191A22";
    private static final String GREEN = "#83ED80";
    private static final String WHITE = "#F5F5F7";
    private static final String GRAY = "#A4A6B3";
    private static final String BORDER = "#363744";

    public enum Type {
        ERROR,
        SUCCESS,
        WARNING,
        INFO
    }

    public static void show(
            Window owner,
            String title,
            String message,
            Type type
    ) {
        Stage dialog = new Stage();

        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            dialog.initOwner(owner);
        }

        String icon;
        String accent;

        switch (type) {
            case ERROR -> {
                icon = "!";
                accent = "#F07178";
            }
            case SUCCESS -> {
                icon = "✓";
                accent = GREEN;
            }
            case WARNING -> {
                icon = "!";
                accent = "#F2C66D";
            }
            default -> {
                icon = "i";
                accent = GREEN;
            }
        }

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(
                "System",
                FontWeight.BOLD,
                22
        ));
        iconLabel.setTextFill(Color.web(accent));
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMinSize(44, 44);
        iconLabel.setPrefSize(44, 44);
        iconLabel.setStyle(
                "-fx-background-color: " + accent + "18;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + accent + "55;" +
                        "-fx-border-radius: 14;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(
                "System",
                FontWeight.BOLD,
                19
        ));
        titleLabel.setTextFill(Color.web(WHITE));
        titleLabel.setWrapText(true);

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setTextFill(Color.web(GRAY));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);

        VBox textBox = new VBox(8, titleLabel, messageLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox content = new HBox(15, iconLabel, textBox);
        content.setAlignment(Pos.CENTER_LEFT);

        Button okButton = new Button("Entendi");
        okButton.setDefaultButton(true);
        okButton.setMinWidth(100);
        okButton.setPrefHeight(38);
        okButton.setFont(Font.font(
                "System",
                FontWeight.BOLD,
                13
        ));
        okButton.setTextFill(Color.web(BACKGROUND));
        okButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 20;"
        );

        okButton.setOnMouseEntered(event ->
                okButton.setStyle(
                        "-fx-background-color: #A0F59D;" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 0 20;"
                )
        );

        okButton.setOnMouseExited(event ->
                okButton.setStyle(
                        "-fx-background-color: " + GREEN + ";" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 0 20;"
                )
        );

        okButton.setOnAction(event -> dialog.close());

        HBox buttons = new HBox(okButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(24, content, buttons);
        card.setPadding(new Insets(26));
        card.setPrefWidth(440);
        card.setMaxWidth(440);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 18;"
        );

        VBox root = new VBox(card);
        root.setPadding(new Insets(2));
        root.setStyle(
                "-fx-background-color: transparent;"
        );

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        dialog.setTitle("ChatIA");
        dialog.setScene(scene);
        dialog.setResizable(false);

        dialog.setOnShown(event -> {
            dialog.sizeToScene();
            dialog.centerOnScreen();
        });

        dialog.showAndWait();
    }
    public static Optional<String> prompt(
            Window owner,
            String title,
            String message,
            String initialValue
    ) {
        Stage dialog = new Stage();

        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            dialog.initOwner(owner);
        }

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 19));
        titleLabel.setTextFill(Color.web(WHITE));

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setTextFill(Color.web(GRAY));
        messageLabel.setWrapText(true);

        TextField input = new TextField(initialValue);
        input.setPromptText("Digite o novo nome...");
        input.setFont(Font.font("System", 14));
        input.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-prompt-text-fill: " + GRAY + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-padding: 12;"
        );

        Button cancelButton = new Button("Cancelar");
        cancelButton.setPrefHeight(38);
        cancelButton.setTextFill(Color.web(WHITE));
        cancelButton.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 18;"
        );

        Button saveButton = new Button("Salvar");
        saveButton.setDefaultButton(true);
        saveButton.setPrefHeight(38);
        saveButton.setTextFill(Color.web(BACKGROUND));
        saveButton.setFont(Font.font("System", FontWeight.BOLD, 13));
        saveButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 22;"
        );

        final String[] result = {null};

        saveButton.setOnAction(event -> {
            String newName = input.getText();

            if (newName != null && !newName.trim().isEmpty()) {
                result[0] = newName.trim();
                dialog.close();
            } else {
                input.setPromptText("Digite um nome válido!");
                input.requestFocus();
            }
        });

        cancelButton.setOnAction(event -> dialog.close());

        HBox buttons = new HBox(10, cancelButton, saveButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(
                14,
                titleLabel,
                messageLabel,
                input,
                buttons
        );

        card.setPadding(new Insets(26));
        card.setPrefWidth(420);
        card.setAlignment(Pos.CENTER_LEFT);

        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 18;"
        );

        VBox root = new VBox(card);
        root.setPadding(new Insets(2));
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        dialog.setTitle("ChatIA");
        dialog.setScene(scene);
        dialog.setResizable(false);

        dialog.setOnShown(event -> {
            dialog.sizeToScene();
            dialog.centerOnScreen();
            input.requestFocus();
            input.selectAll();
        });

        dialog.showAndWait();

        return Optional.ofNullable(result[0]);
    }

    public static boolean confirm(
            Window owner,
            String title,
            String message
    ) {
        Stage dialog = new Stage();

        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            dialog.initOwner(owner);
        }

        Label iconLabel = new Label("!");
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        iconLabel.setTextFill(Color.web("#F07178"));
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMinSize(44, 44);
        iconLabel.setPrefSize(44, 44);
        iconLabel.setStyle(
                "-fx-background-color: #F0717818;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #F0717855;" +
                        "-fx-border-radius: 14;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 19));
        titleLabel.setTextFill(Color.web(WHITE));
        titleLabel.setWrapText(true);

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setTextFill(Color.web(GRAY));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(320);

        VBox textBox = new VBox(8, titleLabel, messageLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox content = new HBox(15, iconLabel, textBox);
        content.setAlignment(Pos.CENTER_LEFT);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setPrefHeight(38);
        cancelButton.setTextFill(Color.web(WHITE));
        cancelButton.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 18;"
        );

        Button deleteButton = new Button("Excluir");
        deleteButton.setPrefHeight(38);
        deleteButton.setFont(Font.font("System", FontWeight.BOLD, 13));
        deleteButton.setTextFill(Color.web("#FFFFFF"));
        deleteButton.setStyle(
                "-fx-background-color: #D94F5C;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 22;"
        );

        final boolean[] confirmed = {false};

        cancelButton.setOnAction(event -> dialog.close());

        deleteButton.setOnAction(event -> {
            confirmed[0] = true;
            dialog.close();
        });

        HBox buttons = new HBox(10, cancelButton, deleteButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(24, content, buttons);
        card.setPadding(new Insets(26));
        card.setPrefWidth(440);
        card.setMaxWidth(440);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 18;"
        );

        VBox root = new VBox(card);
        root.setPadding(new Insets(2));
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        dialog.setTitle("ChatIA");
        dialog.setScene(scene);
        dialog.setResizable(false);

        dialog.setOnShown(event -> {
            dialog.sizeToScene();
            dialog.centerOnScreen();
        });

        dialog.showAndWait();

        return confirmed[0];
    }

}