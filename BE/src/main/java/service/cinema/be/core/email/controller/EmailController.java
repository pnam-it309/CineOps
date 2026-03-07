package service.cinema.be.core.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.cinema.be.core.common.response.ApiResponse;
import service.cinema.be.core.email.dto.EmailRequest;
import service.cinema.be.core.email.service.IEmailService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
@Tag(name = "Email Management", description = "API for sending emails and testing email functionality")
public class EmailController {

    private final IEmailService emailService;

    @Operation(summary = "Send a single email asynchronously", description = "Send an email using the specified template and variables")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email sent successfully",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid email request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendEmail(
            @Valid @RequestBody EmailRequest emailRequest) {
        
        return emailService.sendEmailAsync(emailRequest)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Email sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send email", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send email: " + e.getMessage()));
                });
    }

    @Operation(summary = "Send email synchronously", description = "Send an email and wait for completion")
    @PostMapping("/send-sync")
    public ResponseEntity<ApiResponse<String>> sendEmailSync(
            @Valid @RequestBody EmailRequest emailRequest) {
        
        try {
            emailService.sendEmailSync(emailRequest);
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
        } catch (Exception e) {
            log.error("Failed to send email synchronously", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Failed to send email: " + e.getMessage()));
        }
    }

    @Operation(summary = "Send bulk emails", description = "Send multiple emails in a single request")
    @PostMapping("/send-bulk")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendBulkEmails(
            @Valid @RequestBody EmailRequest[] emailRequests) {
        
        return emailService.sendBulkEmailsAsync(emailRequests)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Bulk emails sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send bulk emails", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send bulk emails: " + e.getMessage()));
                });
    }

    @Operation(summary = "Test email connection", description = "Test the SMTP connection and configuration")
    @GetMapping("/test-connection")
    public ResponseEntity<ApiResponse<String>> testConnection() {
        boolean isConnected = emailService.testEmailConnection();
        if (isConnected) {
            return ResponseEntity.ok(ApiResponse.success("Email connection test passed"));
        } else {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Email connection test failed"));
        }
    }

    @Operation(summary = "Send test ticket confirmation", description = "Send a sample ticket confirmation email")
    @GetMapping("/test-ticket")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendTestTicket() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("movieTitle", "Avatar: The Way of Water");
        variables.put("showDate", "2026-03-15");
        variables.put("showTime", "19:30");
        variables.put("theaterName", "CineOps Cinema");
        variables.put("screenNumber", "Screen 3");
        variables.put("seatNumber", "A12, A13");
        variables.put("totalPrice", "$30.00");
        variables.put("qrCodeData", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

        EmailRequest emailRequest = EmailRequest.builder()
                .to("test@example.com")
                .subject("🎬 Your Movie Ticket Confirmation")
                .templateName("email/ticket-confirmation")
                .variables(variables)
                .build();

        return emailService.sendEmailAsync(emailRequest)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Test ticket email sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send test ticket email", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send test ticket email: " + e.getMessage()));
                });
    }

    @Operation(summary = "Send test membership welcome", description = "Send a sample membership welcome email")
    @GetMapping("/test-membership")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendTestMembership() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", "John Doe");
        variables.put("membershipLevel", "Gold");
        variables.put("memberId", "CINEOPS123456");
        variables.put("activationLink", "https://cineops.com/activate?token=abc123");

        EmailRequest emailRequest = EmailRequest.builder()
                .to("test@example.com")
                .subject("🎉 Welcome to CineOps - Your Membership is Active!")
                .templateName("email/membership-welcome")
                .variables(variables)
                .build();

        return emailService.sendEmailAsync(emailRequest)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Test membership email sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send test membership email", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send test membership email: " + e.getMessage()));
                });
    }

    @Operation(summary = "Send test password reset", description = "Send a sample password reset email")
    @GetMapping("/test-password-reset")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendTestPasswordReset() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetLink", "https://cineops.com/reset-password?token=xyz789");
        variables.put("expiryHours", "24");

        EmailRequest emailRequest = EmailRequest.builder()
                .to("test@example.com")
                .subject("🔒 Reset Your CineOps Password")
                .templateName("email/password-reset")
                .variables(variables)
                .build();

        return emailService.sendEmailAsync(emailRequest)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Test password reset email sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send test password reset email", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send test password reset email: " + e.getMessage()));
                });
    }

    @Operation(summary = "Send simple text email", description = "Send a simple text email without template")
    @PostMapping("/send-simple")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> sendSimpleEmail(
            @Parameter(description = "Recipient email address") @RequestParam String to,
            @Parameter(description = "Email subject") @RequestParam String subject,
            @Parameter(description = "Email content") @RequestParam String content) {
        
        EmailRequest emailRequest = EmailRequest.simple(to, subject, content);

        return emailService.sendEmailAsync(emailRequest)
                .thenApply(v -> ResponseEntity.ok(ApiResponse.success("Simple email sent successfully")))
                .exceptionally(e -> {
                    log.error("Failed to send simple email", e);
                    return ResponseEntity.internalServerError()
                            .body(ApiResponse.error(500, "Failed to send simple email: " + e.getMessage()));
                });
    }
}
