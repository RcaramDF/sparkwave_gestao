package com.sparkwave.login.service;

import com.sparkwave.login.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Serviço para envio de notificações por e-mail.
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@sparkwave.com}")
    private String fromEmail;
    
    @Value("${sparkwave.app.email.enabled:false}")
    private boolean emailEnabled;
    
    /**
     * Envia um e-mail simples.
     *
     * @param to Destinatário
     * @param subject Assunto
     * @param text Conteúdo
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled || mailSender == null) {
            logger.info("E-mail não enviado (desabilitado): Para: {}, Assunto: {}", to, subject);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            logger.info("E-mail simples enviado para: {}", to);
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail simples para: {}", to, e);
        }
    }
    
    /**
     * Envia um e-mail HTML usando um template.
     *
     * @param to Destinatário
     * @param subject Assunto
     * @param templateName Nome do template
     * @param variables Variáveis para o template
     */
    @Async
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled || mailSender == null || templateEngine == null) {
            logger.info("E-mail de template não enviado (desabilitado): Para: {}, Assunto: {}, Template: {}", 
                    to, subject, templateName);
            return;
        }
        
        try {
            // Preparar o contexto do template
            Context context = new Context();
            variables.forEach(context::setVariable);
            
            // Processar o template
            String htmlContent = templateEngine.process(templateName, context);
            
            // Criar a mensagem
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("E-mail de template enviado para: {}, Template: {}", to, templateName);
        } catch (MessagingException e) {
            logger.error("Erro ao enviar e-mail de template para: {}, Template: {}", to, templateName, e);
        }
    }
    
    /**
     * Envia um e-mail de boas-vindas para um novo usuário.
     *
     * @param user Usuário
     * @param password Senha (opcional, apenas para usuários criados pelo administrador)
     */
    @Async
    public void sendWelcomeEmail(User user, String password) {
        if (password != null && !password.isEmpty()) {
            // Usuário criado pelo administrador
            String subject = "Bem-vindo à SparkWave Consultoria Empresarial";
            String text = String.format(
                    "Olá %s,\n\n" +
                    "Sua conta foi criada na plataforma da SparkWave Consultoria Empresarial.\n\n" +
                    "Suas credenciais de acesso são:\n" +
                    "Usuário: %s\n" +
                    "Senha: %s\n\n" +
                    "Recomendamos que altere sua senha após o primeiro acesso.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe SparkWave",
                    user.getFullName(), user.getUsername(), password);
            
            sendSimpleEmail(user.getEmail(), subject, text);
        } else {
            // Usuário se registrou
            String subject = "Bem-vindo à SparkWave Consultoria Empresarial";
            String text = String.format(
                    "Olá %s,\n\n" +
                    "Sua conta foi criada com sucesso na plataforma da SparkWave Consultoria Empresarial.\n\n" +
                    "Você já pode acessar nossos serviços utilizando seu nome de usuário: %s\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe SparkWave",
                    user.getFullName(), user.getUsername());
            
            sendSimpleEmail(user.getEmail(), subject, text);
        }
    }
    
    /**
     * Envia um e-mail de redefinição de senha.
     *
     * @param user Usuário
     * @param newPassword Nova senha
     */
    @Async
    public void sendPasswordResetEmail(User user, String newPassword) {
        String subject = "Redefinição de Senha - SparkWave Consultoria Empresarial";
        String text = String.format(
                "Olá %s,\n\n" +
                "Sua senha foi redefinida na plataforma da SparkWave Consultoria Empresarial.\n\n" +
                "Sua nova senha é: %s\n\n" +
                "Recomendamos que altere esta senha após o próximo acesso.\n\n" +
                "Atenciosamente,\n" +
                "Equipe SparkWave",
                user.getFullName(), newPassword);
        
        sendSimpleEmail(user.getEmail(), subject, text);
    }
    
    /**
     * Envia um e-mail de notificação de alteração de status da conta.
     *
     * @param user Usuário
     * @param active Status de ativação
     */
    @Async
    public void sendAccountStatusEmail(User user, boolean active) {
        String status = active ? "ativada" : "desativada";
        String subject = "Status da Conta - SparkWave Consultoria Empresarial";
        String text = String.format(
                "Olá %s,\n\n" +
                "Informamos que sua conta na plataforma da SparkWave Consultoria Empresarial foi %s.\n\n" +
                (active ? 
                "Você já pode acessar nossos serviços normalmente.\n\n" : 
                "Caso tenha dúvidas, entre em contato com nosso suporte.\n\n") +
                "Atenciosamente,\n" +
                "Equipe SparkWave",
                user.getFullName(), status);
        
        sendSimpleEmail(user.getEmail(), subject, text);
    }
}

