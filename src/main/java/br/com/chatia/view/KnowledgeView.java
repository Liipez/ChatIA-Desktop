package br.com.chatia.view;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.controller.KnowledgeController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class KnowledgeView {

    private final KnowledgeController knowledgeController;

    private final ListView<KnowledgeDocument> documentList;
    private final TextField titleField;
    private final TextField keywordsField;
    private final TextArea contentArea;

    private final Label statusLabel;

    public KnowledgeView() {

        knowledgeController = new KnowledgeController();

        documentList = new ListView<>();
        titleField = new TextField();
        keywordsField = new TextField();
        contentArea = new TextArea();
        statusLabel = new Label();

        configureFields();
        configureDocumentList();
    }

    public void show(Stage owner) {

        Stage stage = new Stage();

        stage.setTitle("ChatIA - Base de conhecimento");
        stage.initOwner(owner);
        stage.initModality(Modality.NONE);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #101116;");

        Label heading = new Label("Base de conhecimento");
        heading.setStyle(
                "-fx-text-fill: #83ED80;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Gerencie os documentos utilizados pela inteligência artificial."
        );
        subtitle.setStyle("-fx-text-fill: #A8A8B3;");

        VBox header = new VBox(8, heading, subtitle);
        header.setPadding(new Insets(0, 0, 24, 0));

        VBox form = createForm();
        VBox listSection = createListSection();

        HBox content = new HBox(24, form, listSection);
        HBox.setHgrow(listSection, Priority.ALWAYS);

        root.setTop(header);
        root.setCenter(content);

        BorderPane.setMargin(content, new Insets(0));

        Scene scene = new Scene(root, 1050, 680);

        stage.setScene(scene);

        loadDocuments();

        stage.show();
    }

    private VBox createForm() {

        Label titleLabel = createLabel("Título");
        Label contentLabel = createLabel("Conteúdo");
        Label keywordsLabel = createLabel("Palavras-chave");

        titleField.setPromptText("Ex.: Introdução ao Java");
        keywordsField.setPromptText("Ex.: java, programação, backend");
        contentArea.setPromptText(
                "Digite aqui o conteúdo que a IA poderá consultar..."
        );

        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(12);

        Button saveButton = new Button("Salvar documento");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle(
                "-fx-background-color: #83ED80;" +
                        "-fx-text-fill: #101116;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        saveButton.setOnAction(event -> saveDocument());

        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #A8A8B3;");

        VBox form = new VBox(
                10,
                titleLabel,
                titleField,
                contentLabel,
                contentArea,
                keywordsLabel,
                keywordsField,
                saveButton,
                statusLabel
        );

        form.setPadding(new Insets(20));
        form.setPrefWidth(420);
        form.setMinWidth(340);
        form.setStyle(
                "-fx-background-color: #191A22;" +
                        "-fx-background-radius: 12;"
        );

        return form;
    }

    private VBox createListSection() {

        Label heading = createLabel("Documentos cadastrados");

        documentList.setCellFactory(list -> new ListCell<>() {

            @Override
            protected void updateItem(
                    KnowledgeDocument document,
                    boolean empty
            ) {
                super.updateItem(document, empty);

                if (empty || document == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label title = new Label(document.getTitle());
                title.setStyle(
                        "-fx-text-fill: #83ED80;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;"
                );

                String content = document.getContent() == null
                        ? ""
                        : document.getContent().replaceAll("\\s+", " ").trim();

                if (content.length() > 100) {
                    content = content.substring(0, 100) + "...";
                }

                Label preview = new Label(content);
                preview.setWrapText(true);
                preview.setStyle("-fx-text-fill: #A8A8B3;");

                VBox card = new VBox(6, title, preview);
                card.setPadding(new Insets(10));

                setGraphic(card);
                setText(null);
                setStyle("-fx-background-color: #191A22;");
            }
        });

        Button deleteButton = new Button("Excluir selecionado");
        deleteButton.setStyle(
                "-fx-background-color: #D9534F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        deleteButton.setOnAction(event -> deleteSelectedDocument());

        VBox section = new VBox(
                14,
                heading,
                documentList,
                deleteButton
        );

        VBox.setVgrow(documentList, Priority.ALWAYS);

        section.setMinWidth(300);

        return section;
    }

    private void configureFields() {

        titleField.setMaxWidth(Double.MAX_VALUE);
        keywordsField.setMaxWidth(Double.MAX_VALUE);

        contentArea.setMaxWidth(Double.MAX_VALUE);
        contentArea.setPrefHeight(250);

        styleInput(titleField);
        styleInput(keywordsField);
        styleInput(contentArea);
    }

    private void configureDocumentList() {

        documentList.setStyle(
                "-fx-background-color: #101116;" +
                        "-fx-control-inner-background: #101116;" +
                        "-fx-background-radius: 8;"
        );
    }

    private void styleInput(javafx.scene.control.Control control) {

        control.setStyle(
                "-fx-background-color: #101116;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #777783;" +
                        "-fx-border-color: #30313C;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );
    }

    private Label createLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );

        return label;
    }

    private void saveDocument() {

        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String keywordsText = keywordsField.getText().trim();

        if (title.isBlank() || content.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Campos obrigatórios",
                    "Preencha o título e o conteúdo do documento."
            );

            return;
        }

        List<String> keywords = Arrays.stream(
                        keywordsText.split(",")
                )
                .map(String::trim)
                .filter(keyword -> !keyword.isBlank())
                .distinct()
                .toList();

        try {

            knowledgeController.save(title, content, keywords);

            loadDocuments();

            titleField.clear();
            contentArea.clear();
            keywordsField.clear();

            statusLabel.setText("Documento processado com sucesso.");

        } catch (Exception exception) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Erro ao salvar",
                    br.com.chatia.util.ErrorMessages.friendly(exception)
            );
        }
    }

    private void loadDocuments() {

        try {

            documentList.setItems(
                    FXCollections.observableArrayList(
                            knowledgeController.findAll()
                    )
            );

        } catch (Exception exception) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Erro ao carregar",
                    br.com.chatia.util.ErrorMessages.friendly(exception)
            );
        }
    }

    private void deleteSelectedDocument() {

        KnowledgeDocument selected =
                documentList.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Nenhum documento selecionado",
                    "Selecione um documento antes de excluí-lo."
            );

            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Confirmar exclusão");
        confirmation.setHeaderText("Excluir documento?");
        confirmation.setContentText(
                "O documento \"" + selected.getTitle()
                        + "\" será removido permanentemente."
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == javafx.scene.control.ButtonType.OK) {

                try {

                    boolean deleted = knowledgeController.delete(
                            selected.getId()
                    );

                    if (deleted) {

                        loadDocuments();

                        statusLabel.setText("Documento excluído.");

                    } else {

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Documento não encontrado",
                                "O documento não foi encontrado no banco de dados."
                        );
                    }

                } catch (Exception exception) {

                    showAlert(
                            Alert.AlertType.ERROR,
                            "Erro ao excluir",
                            br.com.chatia.util.ErrorMessages.friendly(exception)
                    );
                }
            }
        });
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}