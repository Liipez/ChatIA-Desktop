package br.com.chatia.controller;

import javafx.scene.Scene;

import java.net.URL;

/**
 * Controla o tema da aplicação trocando as folhas de estilo (light.css / dark.css).
 * O tema escolhido vale para todas as telas enquanto a aplicação estiver aberta.
 */
public final class ThemeController {

    private static final String DARK = "/css/dark.css";
    private static final String LIGHT = "/css/light.css";

    private static boolean dark = true;

    private ThemeController() {
    }

    public static boolean isDark() {
        return dark;
    }

    /** Aplica o tema atual na cena. */
    public static void apply(Scene scene) {
        URL css = ThemeController.class.getResource(dark ? DARK : LIGHT);
        scene.getStylesheets().removeIf(s -> s.endsWith(DARK) || s.endsWith(LIGHT));
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("Folha de estilo não encontrada: " + (dark ? DARK : LIGHT));
        }
    }

    /** Alterna entre claro e escuro e aplica na cena. */
    public static boolean toggle(Scene scene) {
        dark = !dark;
        apply(scene);
        return dark;
    }
}
