package br.com.chatia.view;

import br.com.chatia.model.User;
import br.com.chatia.util.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Label;
import br.com.chatia.service.KnowledgeService;
import br.com.chatia.model.Conversation;
import br.com.chatia.model.Message;
import br.com.chatia.service.ConversationService;
import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainView {

    private static final String BACKGROUND = "#101116";
    private static final String SIDEBAR = "#15161D";
    private static final String CARD = "#191A22";
    private static final String GREEN = "#83ED80";
    private static final String WHITE = "#F5F5F7";
    private static final String GRAY = "#A4A6B3";
    private static final String BORDER = "#30313B";

    private VBox messagesBox;
    private ScrollPane messagesScroll;
    private TextArea messageInput;
    private Stage stage;
    private Label welcomeLabel;
    private ConversationService conversationService;
    private Conversation currentConversation;
    private VBox conversationList;
    private boolean temaEscuro = true;

    public void show(Stage stage) {
        this.stage = stage;
        conversationService = new ConversationService();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setCenter(createChatArea());
        loadConversations();

        Scene scene = new Scene(root, 1100, 720);

        scene.getStylesheets().add(
                getClass().getResource("/css/dark.css").toExternalForm()
        );

        stage.setTitle("ChatIA Desktop");
        stage.setTitle("ChatIA Desktop");
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(18);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setPrefWidth(250);
        sidebar.setStyle(
                "-fx-background-color: " + SIDEBAR + ";" +
                        "-fx-border-color: transparent " + BORDER +
                        " transparent transparent;"
        );

        Label logo = new Label("✳  ChatIA");
        logo.setFont(Font.font("Segoe UI", 25));
        logo.setTextFill(Color.web(GREEN));

        Button newChatButton = new Button("+   Nova conversa");
        newChatButton.setMaxWidth(Double.MAX_VALUE);
        newChatButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-text-fill: #101116;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-padding: 12;"
        );

        newChatButton.setOnAction(event -> {
            currentConversation = null;
            clearChat();
            messageInput.clear();
        });
        newChatButton.setOnAction(event -> {
            currentConversation = null;
            clearChat();
            messageInput.clear();
        });

        Button knowledgeButton = new Button("📚  Base de conhecimento");

        knowledgeButton.setMaxWidth(Double.MAX_VALUE);

        knowledgeButton.setStyle(
                "-fx-background-color: #191A22;" +
                        "-fx-text-fill: #83ED80;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 9;" +
                        "-fx-border-color: #30313B;" +
                        "-fx-border-radius: 9;" +
                        "-fx-padding: 12;" +
                        "-fx-cursor: hand;"
        );

        knowledgeButton.setOnAction(event -> {
            new KnowledgeView().show(stage);
        });

        Label historyTitle = new Label("SUAS CONVERSAS");
        historyTitle.setTextFill(Color.web(GRAY));
        historyTitle.setFont(Font.font("Segoe UI", 10));

        conversationList = new VBox(8);
        conversationList.setFillWidth(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox profile = new VBox(5);

        User currentUser = SessionManager.getCurrentUser();
        String userName = currentUser != null
                ? currentUser.getName()
                : "Usuário";

        Label nameLabel = new Label(userName);
        nameLabel.setTextFill(Color.web(WHITE));
        nameLabel.setFont(Font.font("Segoe UI", 13));

        Label accountLabel = new Label("Conta ChatIA");
        accountLabel.setTextFill(Color.web(GRAY));
        accountLabel.setFont(Font.font("Segoe UI", 11));

        Button logoutButton = new Button("Sair da conta");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + GRAY + ";" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 9 0 0 0;"
        );

        logoutButton.setOnAction(event -> {
            SessionManager.logout();
            new LoginView().show(stage);
        });

        profile.getChildren().addAll(
                nameLabel, accountLabel, logoutButton
        );

        sidebar.getChildren().addAll(
                logo,
                newChatButton,
                knowledgeButton,
                historyTitle,
                conversationList,
                spacer,
                profile
        );

        return sidebar;
    }

    private BorderPane createChatArea() {
        BorderPane chatArea = new BorderPane();
        chatArea.setStyle("-fx-background-color: " + BACKGROUND + ";");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 28, 20, 28));
        header.setStyle(
                "-fx-border-color: transparent transparent " +
                        BORDER + " transparent;"
        );

        Label headerTitle = new Label("ChatIA");
        headerTitle.setTextFill(Color.web(WHITE));
        headerTitle.setFont(Font.font("Segoe UI", 18));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label status = new Label("●  Assistente");
        status.setTextFill(Color.web(GREEN));
        status.setFont(Font.font("Segoe UI", 12));

        Button themeButton = new Button("☀ Tema claro");
        themeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;" +
                        "-fx-cursor: hand;"
        );

        themeButton.setOnAction(event -> {
            Scene scene = themeButton.getScene();

            if (scene != null) {
                alternarTema(scene, themeButton);
            }
        });

        header.getChildren().addAll(
                headerTitle, headerSpacer, themeButton, status
        );

        VBox center = new VBox(18);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(25));

        welcomeLabel = new Label("Como posso ajudar você?");
        welcomeLabel.setTextFill(Color.web(WHITE));
        welcomeLabel.setFont(Font.font("Segoe UI", 28));
        welcomeLabel.setWrapText(true);

        Label subtitle = new Label(
                "Converse, explore ideias e transforme suas perguntas em soluções."
        );
        subtitle.setTextFill(Color.web(GRAY));
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setWrapText(true);

        VBox welcome = new VBox(12, welcomeLabel, subtitle);
        welcome.setAlignment(Pos.CENTER);

        messagesBox = new VBox(18);
        messagesBox.setPadding(new Insets(20));
        messagesBox.setFillWidth(true);

        messagesScroll = new ScrollPane(messagesBox);
        messagesScroll.setFitToWidth(true);
        messagesScroll.setStyle(
                "-fx-background: " + BACKGROUND + ";" +
                        "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-border-color: transparent;"
        );
        VBox.setVgrow(messagesScroll, Priority.ALWAYS);

        center.getChildren().addAll(welcome, messagesScroll);

        messageInput = new TextArea();
        messageInput.setPromptText("Escreva sua mensagem...");
        messageInput.setWrapText(true);
        messageInput.setPrefRowCount(2);
        messageInput.setMaxHeight(100);
        messageInput.setStyle(
                "-fx-control-inner-background: " + CARD + ";" +
                        "-fx-background-color: " + CARD + ";" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-prompt-text-fill: " + GRAY + ";" +
                        "-fx-highlight-fill: " + GREEN + ";" +
                        "-fx-highlight-text-fill: " + BACKGROUND + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-font-size: 14px;"
        );

        Button sendButton = new Button("Enviar  ➤");
        sendButton.setStyle(
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-text-fill: " + BACKGROUND + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 13 18;"
        );

        sendButton.setOnAction(event -> sendMessage());

        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER
                    && !event.isShiftDown()) {
                event.consume();
                sendMessage();
            }
        });

        HBox inputRow = new HBox(12, messageInput, sendButton);
        inputRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageInput, Priority.ALWAYS);

        Label footer = new Label(
                "."
        );
        footer.setTextFill(Color.web("#777986"));
        footer.setFont(Font.font("Segoe UI", 10));

        VBox bottom = new VBox(10, inputRow, footer);
        bottom.setPadding(new Insets(15, 28, 18, 28));
        bottom.setAlignment(Pos.CENTER);
        bottom.setStyle(
                "-fx-border-color: " + BORDER + " transparent " +
                        "transparent transparent;"
        );

        chatArea.setTop(header);
        chatArea.setCenter(center);
        chatArea.setBottom(bottom);

        return chatArea;
    }

    private void sendMessage() {
        String text = messageInput.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        if (welcomeLabel != null) {
            welcomeLabel.setVisible(false);
            welcomeLabel.setManaged(false);
        }

        if (currentConversation == null) {
            currentConversation =
                    conversationService.createConversation(
                            text.length() > 35
                                    ? text.substring(0, 35)
                                    : text
                    );

            loadConversations();
        }

        // Recupera as mensagens anteriores antes de salvar a pergunta atual.
        List<Message> historicoAnterior =
                conversationService.getMessages(
                        currentConversation.getId()
                );

        conversationService.saveMessage(
                currentConversation.getId(),
                "user",
                text
        );

        addMessage("Você", text, true);

        messageInput.clear();

        String conversationId = currentConversation.getId();

        addMessage(
                "ChatIA",
                "Pensando... 🤖",
                false
        );

        javafx.concurrent.Task<String> task =
                new javafx.concurrent.Task<>() {

                    @Override
                    protected String call() throws Exception {
                        br.com.chatia.service.GroqService groqService =
                                new br.com.chatia.service.GroqService();

                        br.com.chatia.service.KnowledgeService knowledgeService =
                                new br.com.chatia.service.KnowledgeService();

                        String context = knowledgeService.buildContext(text);

                        String memoriaAnterior =
                                conversationService.getPreviousConversationMemory(conversationId);

                        if (!memoriaAnterior.isBlank()) {
                            context += "\n\nMEMÓRIA DE OUTRAS CONVERSAS DO USUÁRIO:\n"
                                    + memoriaAnterior;
                        }

                        List<br.com.chatia.service.GroqService.MessageHistory> historico =
                                historicoAnterior.stream()
                                        .map(message -> new br.com.chatia.service.GroqService.MessageHistory(
                                                message.getRole(), message.getContent()
                                        ))
                                        .toList();

                        return groqService.chat(text, context, historico);
                    }
                };

        task.setOnSucceeded(event -> {
            String resposta = task.getValue();

            try {
                conversationService.saveMessage(
                        conversationId,
                        "assistant",
                        resposta
                );

                messagesBox.getChildren().remove(
                        messagesBox.getChildren().size() - 1
                );

                addMessage("ChatIA", resposta, false);

            } catch (Exception e) {
                System.err.println(
                        "Erro ao salvar a resposta: " + e.getMessage()
                );

                addMessage(
                        "ChatIA",
                        resposta + "\n\nNão foi possível salvar a resposta no histórico.",
                        false
                );
            }
        });

        task.setOnFailed(event -> {
            Throwable erro = task.getException();

            System.err.println(
                    "Erro ao consultar a Groq: "
                            + (erro != null ? erro.getMessage() : "Erro desconhecido")
            );

            messagesBox.getChildren().remove(
                    messagesBox.getChildren().size() - 1
            );

            addMessage(
                    "ChatIA",
                    "Desculpe, não consegui gerar uma resposta agora. Tente novamente.",
                    false
            );
        });

        Thread thread = new Thread(task, "groq-request");
        thread.setDaemon(true);
        thread.start();
    }

    private void addMessage(String sender, String text, boolean fromUser) {
        Label senderLabel = new Label(sender);
        senderLabel.setTextFill(
                Color.web(fromUser ? GREEN : WHITE)
        );
        senderLabel.setFont(Font.font("Segoe UI", 12));

        Label contentLabel = new Label(text);
        contentLabel.setTextFill(Color.web(WHITE));
        contentLabel.setWrapText(true);
        contentLabel.setFont(Font.font("Segoe UI", 14));

        VBox bubble = new VBox(8, senderLabel, contentLabel);
        bubble.setPadding(new Insets(14));
        bubble.setMaxWidth(600);
        bubble.setStyle(
                "-fx-background-color: " +
                        (fromUser ? "#20251F" : CARD) + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " +
                        (fromUser ? "#354A34" : BORDER) + ";" +
                        "-fx-border-radius: 12;"
        );

        HBox row = new HBox(bubble);
        row.setAlignment(
                fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT
        );

        messagesBox.getChildren().add(row);

        messagesScroll.layout();
        messagesScroll.setVvalue(1.0);
    }

    private void clearChat() {
        messagesBox.getChildren().clear();

        welcomeLabel.setVisible(true);
        welcomeLabel.setManaged(true);
    }
    private void loadConversations() {
        conversationList.getChildren().clear();

        List<Conversation> conversations =
                conversationService.getUserConversations();

        for (Conversation conversation : conversations) {
            Button conversationButton =
                    new Button(conversation.getTitle());

            conversationButton.setMaxWidth(Double.MAX_VALUE);
            conversationButton.setWrapText(true);
            conversationButton.setAlignment(Pos.CENTER_LEFT);

            conversationButton.setFont(Font.font("Segoe UI", 12));

            aplicarEstiloConversa(conversationButton);

            conversationButton.setOnMouseEntered(event -> {
                boolean selecionada = currentConversation != null
                        && currentConversation.getTitle() != null
                        && currentConversation.getTitle().equals(conversation.getTitle());

                if (!selecionada) {
                    conversationButton.setStyle(
                            "-fx-background-color: " + (temaEscuro ? "#252630" : "#E1E5ED") + ";" +
                                    "-fx-text-fill: " + (temaEscuro ? "#F5F5F7" : "#191A22") + ";" +
                                    "-fx-alignment: CENTER_LEFT;" +
                                    "-fx-padding: 10;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-cursor: hand;"
                    );
                }
            });

            conversationButton.setOnMouseExited(event ->
                    aplicarEstiloConversa(conversationButton)
            );

            conversationButton.setOnAction(event -> {
                currentConversation = conversation;
                messagesBox.getChildren().clear();

                welcomeLabel.setVisible(false);
                welcomeLabel.setManaged(false);

                List<Message> messages =
                        conversationService.getMessages(
                                conversation.getId()
                        );

                for (Message message : messages) {
                    boolean fromUser = "user".equals(message.getRole());

                    addMessage(
                            fromUser ? "Você" : "ChatIA",
                            message.getContent(),
                            fromUser
                    );
                }

                loadConversations();
            });

            Button optionsButton = new Button("⋯");
            optionsButton.setOpacity(0);
            optionsButton.setMouseTransparent(true);
            optionsButton.setMinSize(32, 36);
            optionsButton.setMaxSize(32, 36);
            optionsButton.setFocusTraversable(false);

            optionsButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + (temaEscuro ? "#A4A6B3" : "#646875") + ";" +
                            "-fx-font-size: 20px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 0;" +
                            "-fx-cursor: hand;"
            );

            optionsButton.setOnMouseEntered(event ->
                    optionsButton.setStyle(
                            "-fx-background-color: " + (temaEscuro ? "#30313B" : "#E1E5ED") + ";" +
                                    "-fx-text-fill: " + (temaEscuro ? "#FFFFFF" : "#191A22") + ";" +
                                    "-fx-font-size: 20px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-padding: 0;" +
                                    "-fx-cursor: hand;"
                    )
            );

            optionsButton.setOnMouseExited(event ->
                    optionsButton.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-text-fill: " + (temaEscuro ? "#A4A6B3" : "#646875") + ";" +
                                    "-fx-font-size: 20px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 8;" +
                                    "-fx-padding: 0;" +
                                    "-fx-cursor: hand;"
                    )
            );

            MenuItem renameItem = new MenuItem("✏   Renomear");
            MenuItem deleteItem = new MenuItem("🗑   Excluir");

            ContextMenu contextMenu = new ContextMenu(renameItem, deleteItem);

            contextMenu.setStyle(
                    "-fx-background-color: " + (temaEscuro ? "#191A22" : "#FFFFFF") + ";" +
                            "-fx-border-color: " + (temaEscuro ? "#363744" : "#D9DCE5") + ";" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;"
            );

            renameItem.setStyle(
                    "-fx-text-fill: " + (temaEscuro ? "#F5F5F7" : "#191A22") + ";" +
                            "-fx-font-size: 13px;"
            );

            deleteItem.setStyle(
                    "-fx-text-fill: #E57373;" +
                            "-fx-font-size: 13px;"
            );

            optionsButton.setOnAction(event -> {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                } else {
                    contextMenu.show(
                            optionsButton,
                            javafx.geometry.Side.BOTTOM,
                            0,
                            0
                    );
                }
            });

            renameItem.setOnAction(event -> {
                ChatIADialog.prompt(
                        stage,
                        "Renomear conversa",
                        "Digite o novo nome para esta conversa.",
                        conversation.getTitle()
                ).ifPresent(newTitle -> {
                    try {
                        Conversation renamed = conversationService.renameConversation(
                                conversation.getId(),
                                newTitle
                        );

                        conversation.setTitle(renamed.getTitle());
                        loadConversations();

                    } catch (IllegalArgumentException ex) {
                        ChatIADialog.show(
                                stage,
                                "Não foi possível renomear",
                                ex.getMessage(),
                                ChatIADialog.Type.ERROR
                        );
                    }
                });
            });

            deleteItem.setOnAction(event -> {
                boolean confirmed = ChatIADialog.confirm(
                        stage,
                        "Excluir conversa?",
                        "Esta ação excluirá a conversa e todas as mensagens associadas. Essa operação não pode ser desfeita."
                );

                if (confirmed) {
                    try {
                        conversationService.deleteConversation(conversation.getId());

                        if (currentConversation != null
                                && currentConversation.getId().equals(conversation.getId())) {

                            currentConversation = null;
                            messagesBox.getChildren().clear();
                            welcomeLabel.setVisible(true);
                            welcomeLabel.setManaged(true);
                        }

                        loadConversations();

                        ChatIADialog.show(
                                stage,
                                "Conversa excluída",
                                "A conversa foi excluída com sucesso.",
                                ChatIADialog.Type.SUCCESS
                        );

                    } catch (IllegalArgumentException ex) {
                        ChatIADialog.show(
                                stage,
                                "Não foi possível excluir",
                                ex.getMessage(),
                                ChatIADialog.Type.ERROR
                        );
                    }
                }
            });

            HBox conversationRow = new HBox(4);
            conversationRow.setAlignment(Pos.CENTER_LEFT);
            conversationRow.setMaxWidth(Double.MAX_VALUE);

            conversationButton.setMaxWidth(Double.MAX_VALUE);
            conversationButton.setMinHeight(38);
            conversationButton.setPrefHeight(38);
            conversationButton.setFocusTraversable(false);

            HBox.setHgrow(
                    conversationButton,
                    javafx.scene.layout.Priority.ALWAYS
            );

            conversationRow.getChildren().addAll(
                    conversationButton,
                    optionsButton
            );

            conversationRow.setOnMouseEntered(event -> {
                optionsButton.setOpacity(1);
                optionsButton.setMouseTransparent(false);
            });

            conversationRow.setOnMouseExited(event -> {
                if (!contextMenu.isShowing()) {
                    optionsButton.setOpacity(0);
                    optionsButton.setMouseTransparent(true);
                }
            });

            conversationList.getChildren().add(conversationRow);
        }
    }

    private void aplicarEstiloConversa(Button button) {
        boolean selecionada = currentConversation != null
                && currentConversation.getTitle() != null
                && button.getText() != null
                && button.getText().equals(currentConversation.getTitle());

        String background;

        if (selecionada) {
            background = temaEscuro ? "#30313B" : "#E1E5ED";
        } else {
            background = "transparent";
        }

        button.setStyle(
                "-fx-background-color: " + background + ";" +
                        "-fx-text-fill: " + (temaEscuro ? "#F5F5F7" : "#191A22") + ";" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
    }




    private void alternarTema(Scene scene, Button themeButton) {
        String caminhoTema;

        if (temaEscuro) {
            caminhoTema = "/css/light.css";
            temaEscuro = false;
            themeButton.setText("☾ Tema escuro");
        } else {
            caminhoTema = "/css/dark.css";
            temaEscuro = true;
            themeButton.setText("☀ Tema claro");
        }
        themeButton.setStyle(
                "-fx-background-color: "
                        + (temaEscuro ? "#191A22" : "#FFFFFF") + ";" +
                        "-fx-text-fill: "
                        + (temaEscuro ? "#F5F5F7" : "#191A22") + ";" +
                        "-fx-border-color: "
                        + (temaEscuro ? "#30313B" : "#D8DAE3") + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12;" +
                        "-fx-cursor: hand;"
        );

        scene.getStylesheets().clear();

        var arquivoTema = getClass().getResource(caminhoTema);

        if (arquivoTema == null) {
            System.err.println("Não foi possível encontrar: " + caminhoTema);
            return;
        }

        scene.getStylesheets().add(arquivoTema.toExternalForm());

        atualizarCores(scene.getRoot());

        scene.getStylesheets().add(arquivoTema.toExternalForm());

        atualizarCores(scene.getRoot());

        loadConversations();
    }

    private void atualizarCores(javafx.scene.Node node) {
        boolean claro = !temaEscuro;

        String estilo = node.getStyle();

        if (estilo != null && !estilo.isEmpty()) {
            if (claro) {
                estilo = estilo
                        .replace("#101116", "#F5F6FA")
                        .replace("#15161D", "#E9EBF2")
                        .replace("#191A22", "#FFFFFF")
                        .replace("#F5F5F7", "#191A22")
                        .replace("#A4A6B3", "#666A78")
                        .replace("#30313B", "#D8DAE3")
                        .replace("#20251F", "#E6F7E4")
                        .replace("#354A34", "#B8DDB4")
                        .replace("#777986", "#858796");
            } else {
                estilo = estilo
                        .replace("#F5F6FA", "#101116")
                        .replace("#E9EBF2", "#15161D")
                        .replace("#FFFFFF", "#191A22")
                        .replace("#191A22", "#101116")
                        .replace("#666A78", "#A4A6B3")
                        .replace("#D8DAE3", "#30313B")
                        .replace("#E6F7E4", "#20251F")
                        .replace("#B8DDB4", "#354A34")
                        .replace("#858796", "#777986");
            }

            node.setStyle(estilo);
        }

        if (node instanceof Label label) {
            if (Color.web(GREEN).equals(label.getTextFill())) {
                label.setTextFill(Color.web(GREEN));
            } else if (Color.web(WHITE).equals(label.getTextFill())) {
                label.setTextFill(Color.web(claro ? "#191A22" : WHITE));
            } else if (Color.web(GRAY).equals(label.getTextFill())) {
                label.setTextFill(Color.web(claro ? "#666A78" : GRAY));
            }
        }

        if (node instanceof TextArea area) {
            area.setStyle(
                    "-fx-control-inner-background: "
                            + (claro ? "#FFFFFF" : CARD) + ";" +
                            "-fx-background-color: "
                            + (claro ? "#FFFFFF" : CARD) + ";" +
                            "-fx-text-fill: "
                            + (claro ? "#191A22" : WHITE) + ";" +
                            "-fx-prompt-text-fill: "
                            + (claro ? "#858796" : GRAY) + ";" +
                            "-fx-highlight-fill: " + GREEN + ";" +
                            "-fx-highlight-text-fill: #101116;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: "
                            + (claro ? "#D8DAE3" : BORDER) + ";" +
                            "-fx-border-radius: 12;" +
                            "-fx-font-size: 14px;"
            );
        }

        if (node instanceof javafx.scene.Parent parent) {
            for (javafx.scene.Node filho : parent.getChildrenUnmodifiable()) {
                atualizarCores(filho);
            }
        }
    }

}

