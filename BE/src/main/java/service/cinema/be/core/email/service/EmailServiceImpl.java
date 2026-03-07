package service.cinema.be.core.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import service.cinema.be.core.email.dto.EmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String from;
    
    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendEmailAsync(EmailRequest emailRequest) {
        try {
            sendEmail(emailRequest);
            log.info("Email sent successfully to: {}", emailRequest.getTo());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailRequest.getTo(), e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Override
    public void sendEmailSync(EmailRequest emailRequest) {
        sendEmail(emailRequest);
    }
    
    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendBulkEmailsAsync(EmailRequest... emailRequests) {
        try {
            for (EmailRequest request : emailRequests) {
                sendEmail(request);
                log.info("Bulk email sent to: {}", request.getTo());
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send bulk emails: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendEmailWithValidationAsync(EmailRequest emailRequest) {
        try {
            validateEmailRequest(emailRequest);
            sendEmail(emailRequest);
            log.info("Validated email sent successfully to: {}", emailRequest.getTo());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send validated email to {}: {}", emailRequest.getTo(), e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Override
    public boolean testEmailConnection() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("test@example.com");
            message.setSubject("Connection Test");
            message.setText("Test email");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Email connection test failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private void sendEmail(EmailRequest emailRequest) {
        try {
            if (emailRequest.getTemplateName() != null && !emailRequest.getTemplateName().isEmpty()) {
                sendTemplateEmail(emailRequest);
            } else {
                sendSimpleEmail(emailRequest);
            }
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private void sendTemplateEmail(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        
        helper.setFrom(from);
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        
        if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
            helper.setCc(emailRequest.getCc());
        }
        
        if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
            helper.setBcc(emailRequest.getBcc());
        }
        
        // Process Thymeleaf template
        Context context = new Context();
        if (emailRequest.getVariables() != null) {
            context.setVariables(emailRequest.getVariables());
        }
        
        String htmlContent = templateEngine.process(emailRequest.getTemplateName(), context);
        helper.setText(htmlContent, emailRequest.isHtml());
        
        // Add attachments
        if (emailRequest.getAttachments() != null && !emailRequest.getAttachments().isEmpty()) {
            for (File attachment : emailRequest.getAttachments()) {
                helper.addAttachment(attachment.getName(), attachment);
            }
        }
        
        mailSender.send(message);
    }
    
    private void sendSimpleEmail(EmailRequest emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(emailRequest.getTo());
        message.setSubject(emailRequest.getSubject());
        
        if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
            message.setCc(emailRequest.getCc());
        }
        
        if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
            message.setBcc(emailRequest.getBcc());
        }
        
        // Extract content from variables if it's a simple text email
        String content = "";
        if (emailRequest.getVariables() != null && emailRequest.getVariables().containsKey("content")) {
            content = (String) emailRequest.getVariables().get("content");
        }
        message.setText(content);
        
        mailSender.send(message);
    }
    
    private void validateEmailRequest(EmailRequest emailRequest) {
        if (emailRequest.getTo() == null || emailRequest.getTo().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email address is required");
        }
        
        if (emailRequest.getSubject() == null || emailRequest.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Email subject is required");
        }
        
        if (emailRequest.getTemplateName() != null && !emailRequest.getTemplateName().isEmpty()) {
            if (emailRequest.getVariables() == null || emailRequest.getVariables().isEmpty()) {
                log.warn("Template email requested but no variables provided for template: {}", emailRequest.getTemplateName());
            }
        }
        
        // Validate email format
        if (!isValidEmail(emailRequest.getTo())) {
            throw new IllegalArgumentException("Invalid recipient email address: " + emailRequest.getTo());
        }
        
        if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty() && !isValidEmail(emailRequest.getCc())) {
            throw new IllegalArgumentException("Invalid CC email address: " + emailRequest.getCc());
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
