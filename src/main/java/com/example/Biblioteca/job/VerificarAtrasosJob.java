package com.example.Biblioteca.job;

import com.example.Biblioteca.Entity.EmprestimoEntity;
import com.example.Biblioteca.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VerificarAtrasosJob {

    private final EmprestimoRepository emprestimoRepository;
    private final JavaMailSender mailSender;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(VerificarAtrasosJob.class);

    // Roda todo dia as 2:00
    @Scheduled(cron = "0 0 2 * * *")
    public void verificarLivrosVencendo() {

        //Alvo: amanhã
        LocalDate dataAlvo = LocalDate.now().plusDays(1);

        logger.info("[JOB] 🤖 Iniciando checagem de livros a vencer em: {}", dataAlvo);

        List<EmprestimoEntity> vencendoAmanha = emprestimoRepository.buscarEmprestimosQueVencemEm(dataAlvo);

        if (vencendoAmanha.isEmpty()) {
            System.out.println("[JOB] 👍 Nenhum livro vencendo amanhã. Tudo sob controle!");
            return;
        }
        logger.warn("[JOB] ⚠️ ALERTA! {} usuário(s) estão com livros vencendo amanhã! ", vencendoAmanha.size());

        // Processamento paralelo multithread seguro (com JOIN FETCH no repositório)
        vencendoAmanha.parallelStream().forEach(this::enviarEmailCobranca);

        logger.info("[JOB] ✅ Ciclo de notificações finalizado.");


        }

        private void enviarEmailCobranca (EmprestimoEntity emprestimo) {
        String emailUsuario = emprestimo.getUsuario().getLogin(); //supondo que o login seja o gmail "gabriel@gmail.com"
        String nomeUsuario = emprestimo.getUsuario().getNome();
        String tituloLivro = emprestimo.getLivro().getNome(); //nome é o titulo do livro Gabriel

            logger.info("[OPERADOR] -> AVISO DE VENCIMENTO: {} ({}) | LIVRO: '{}'", nomeUsuario, emailUsuario, tituloLivro);

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("seu-email@gmail.com"); // Substitua pelo seu email
            message.setTo(emailUsuario);
            message.setSubject("📚 Lembrete: Livro '" + tituloLivro + "' vence amanhã!");
            message.setText("Olá " + nomeUsuario + ",\n\n" +
                    "Este é um lembrete amigável de que o livro '" + tituloLivro + "' que você emprestou da biblioteca vence amanhã (" + emprestimo.getDataDevolucaoPrevista() + ").\n" +
                    "Por favor, certifique-se de devolver o livro a tempo para evitar multas ou inconvenientes.\n\n" +
                    "Agradecemos por usar nossa biblioteca!\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe da Biblioteca");
            mailSender.send(message);

            logger.info("[EMAIL ENVIADO] -> Lembrete enviado para {} ({}) sobre o livro '{}'", nomeUsuario, emailUsuario, tituloLivro);

        } catch (Exception e){
            logger.error("[ERRO AO ENVIAR EMAIL] -> Falha ao enviar email para {} ({}). Detalhes: {}", nomeUsuario, emailUsuario, e.getMessage());
        }
    }
}