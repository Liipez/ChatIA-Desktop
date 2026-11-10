package br.com.chatia;

import br.com.chatia.view.LoginView;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class MainApp extends Application {

    private static final double SPLASH_DURATION = 4.2;
    private static final double SPLASH_TIMEOUT = 10.0;

    private boolean loginOpened = false;
    private boolean splashFinished = false;

    private PauseTransition splashTimer;
    private PauseTransition timeoutTimer;

    @Override
    public void start(Stage stage) {
        System.out.println(">>> Iniciando ChatIA...");

        try {
            URL splashUrl = getClass().getResource("/splash/splash.html");

            if (splashUrl == null) {
                System.err.println(">>> splash.html não encontrado.");
                openLogin(stage);
                return;
            }

            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            engine.getLoadWorker().stateProperty().addListener(
                    (observable, oldState, newState) -> {
                        System.out.println(">>> Estado da splash: " + newState);

                        if (newState == Worker.State.SUCCEEDED) {
                            if (splashFinished || loginOpened) {
                                return;
                            }

                            System.out.println(">>> Splash carregada!");

                            stage.setTitle("ChatIA");
                            stage.setScene(new Scene(webView, 900, 600));
                            stage.setResizable(false);
                            stage.centerOnScreen();
                            stage.show();

                            if (timeoutTimer != null) {
                                timeoutTimer.stop();
                            }

                            splashTimer = new PauseTransition(
                                    Duration.seconds(SPLASH_DURATION)
                            );

                            splashTimer.setOnFinished(event -> openLogin(stage));
                            splashTimer.play();

                        } else if (newState == Worker.State.FAILED
                                || newState == Worker.State.CANCELLED) {

                            System.err.println(">>> Falha ao carregar a splash.");
                            openLogin(stage);
                        }
                    }
            );

            timeoutTimer = new PauseTransition(
                    Duration.seconds(SPLASH_TIMEOUT)
            );

            timeoutTimer.setOnFinished(event -> {
                if (!splashFinished && !loginOpened) {
                    System.err.println(
                            ">>> Tempo limite da splash atingido. Abrindo login."
                    );
                    openLogin(stage);
                }
            });

            timeoutTimer.play();

            System.out.println(">>> Arquivo encontrado: " + splashUrl);
            System.out.println(">>> Carregando splash...");

            engine.load(splashUrl.toExternalForm());

        } catch (Exception e) {
            System.err.println(">>> Erro ao iniciar splash:");
            e.printStackTrace();
            openLogin(stage);
        }
    }

    private void openLogin(Stage stage) {
        if (loginOpened) {
            return;
        }

        loginOpened = true;
        splashFinished = true;

        if (splashTimer != null) {
            splashTimer.stop();
        }

        if (timeoutTimer != null) {
            timeoutTimer.stop();
        }

        System.out.println(">>> Abrindo tela de login...");

        Platform.runLater(() -> {
            try {
                LoginView loginView = new LoginView();
                loginView.show(stage);
                stage.setResizable(true);

                System.out.println(">>> Login aberto com sucesso!");

            } catch (Exception e) {
                System.err.println(">>> Erro ao abrir o login:");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        if (splashTimer != null) {
            splashTimer.stop();
        }

        if (timeoutTimer != null) {
            timeoutTimer.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
