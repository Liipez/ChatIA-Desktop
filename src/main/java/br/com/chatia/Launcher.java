package br.com.chatia;

/**
 * Ponto de entrada do executável empacotado (jpackage / jar).
 *
 * Quando a classe principal estende javafx.application.Application,
 * o Java exige o JavaFX no module-path e falha ao abrir o .jar com
 * "JavaFX runtime components are missing". Esta classe, que NÃO estende
 * Application, contorna esse problema.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        MainApp.main(args);
    }
}
