package br.com.chatia.service;

import br.com.chatia.model.PasswordResetToken;
import br.com.chatia.model.User;
import br.com.chatia.repository.PasswordResetRepository;
import br.com.chatia.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * Recuperação de senha com token temporário.
 *
 * Fluxo:
 * 1. Usuário informa o e-mail -> é gerado um código de 6 dígitos aleatório (SecureRandom).
 * 2. O código é enviado por e-mail; no banco fica apenas o hash SHA-256.
 * 3. O código vale 15 minutos e aceita no máximo 5 tentativas.
 * 4. Ao redefinir, a nova senha é salva com BCrypt e o token é invalidado.
 *
 * A senha antiga nunca é recuperada nem exibida.
 */
public class PasswordRecoveryService {

    public static final Duration VALIDADE = Duration.ofMinutes(15);
    private static final Duration INTERVALO_MINIMO = Duration.ofSeconds(60);
    private static final int MAX_TENTATIVAS = 5;
    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final String CODIGO_INVALIDO = "Código inválido ou expirado.";

    private final UserRepository userRepository;
    private final PasswordResetRepository tokenRepository;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public PasswordRecoveryService() {
        this(new UserRepository(), new PasswordResetRepository(), new EmailService());
    }

    public PasswordRecoveryService(UserRepository userRepository,
                                   PasswordResetRepository tokenRepository,
                                   EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    /**
     * Solicita a recuperação. Não informa se o e-mail existe ou não
     * (evita que alguém descubra quais e-mails estão cadastrados).
     */
    public void requestReset(String email) throws Exception {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o seu e-mail.");
        }

        if (!emailService.isConfigured()) {
            throw new IllegalStateException(
                    "O envio de e-mail não está configurado. Contate o administrador."
            );
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            return; // resposta idêntica para e-mail inexistente
        }

        PasswordResetToken ultimo = tokenRepository.findLatestActiveByUserId(user.getId());
        if (ultimo != null && ultimo.getCreatedAt()
                .plus(INTERVALO_MINIMO).isAfter(LocalDateTime.now())) {
            return; // evita reenvio em sequência
        }

        String codigo = String.format("%06d", random.nextInt(1_000_000));

        tokenRepository.invalidateAllByUserId(user.getId());
        tokenRepository.save(new PasswordResetToken(
                user.getId(),
                hash(codigo),
                LocalDateTime.now().plus(VALIDADE)
        ));

        emailService.send(
                user.getEmail(),
                "ChatIA - Código de recuperação de senha",
                "Olá, " + user.getName() + "!\n\n"
                        + "Seu código para redefinir a senha é: " + codigo + "\n\n"
                        + "Ele é válido por " + VALIDADE.toMinutes() + " minutos.\n"
                        + "Se você não solicitou, ignore este e-mail."
        );
    }

    public void resetPassword(String email, String codigo,
                              String novaSenha, String confirmacao) {
        if (email == null || email.isBlank()
                || codigo == null || codigo.isBlank()
                || novaSenha == null || novaSenha.isEmpty()
                || confirmacao == null || confirmacao.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }
        if (novaSenha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (!novaSenha.equals(confirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }

        PasswordResetToken token = tokenRepository.findLatestActiveByUserId(user.getId());
        if (token == null || token.isExpired() || token.getAttempts() >= MAX_TENTATIVAS) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }

        boolean codigoCorreto = MessageDigest.isEqual(
                hash(codigo.trim()).getBytes(StandardCharsets.UTF_8),
                token.getTokenHash().getBytes(StandardCharsets.UTF_8)
        );

        if (!codigoCorreto) {
            tokenRepository.incrementAttempts(token.getId());
            int restantes = MAX_TENTATIVAS - token.getAttempts() - 1;
            throw new IllegalArgumentException(restantes > 0
                    ? "Código incorreto. Tentativas restantes: " + restantes + "."
                    : "Número máximo de tentativas atingido. Solicite um novo código.");
        }

        String novoHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt(12));
        userRepository.updatePasswordHash(user.getId(), novoHash);
        tokenRepository.invalidateAllByUserId(user.getId());
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                    digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
