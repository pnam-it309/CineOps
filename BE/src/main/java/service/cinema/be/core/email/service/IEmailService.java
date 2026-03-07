package service.cinema.be.core.email.service;

import service.cinema.be.core.email.dto.EmailRequest;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for email service operations
 */
public interface IEmailService {
    
    /**
     * Send email asynchronously
     * @param emailRequest email request details
     * @return CompletableFuture indicating completion status
     */
    CompletableFuture<Void> sendEmailAsync(EmailRequest emailRequest);
    
    /**
     * Send email synchronously (blocking)
     * @param emailRequest email request details
     */
    void sendEmailSync(EmailRequest emailRequest);
    
    /**
     * Send multiple emails asynchronously
     * @param emailRequests list of email requests
     * @return CompletableFuture indicating completion status
     */
    CompletableFuture<Void> sendBulkEmailsAsync(EmailRequest... emailRequests);
    
    /**
     * Send email with template validation
     * @param emailRequest email request details
     * @return CompletableFuture indicating completion status
     */
    CompletableFuture<Void> sendEmailWithValidationAsync(EmailRequest emailRequest);
    
    /**
     * Test email connectivity
     * @return true if connection is successful
     */
    boolean testEmailConnection();
}
