package service.cinema.be.core.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    
    private String to;
    
    private String subject;
    
    private String templateName;
    
    private Map<String, Object> variables;
    
    @Builder.Default
    private List<File> attachments = List.of();
    
    private String cc;
    
    private String bcc;
    
    private boolean isHtml = true;
    
    /**
     * Convenience method for creating simple text emails
     */
    public static EmailRequest simple(String to, String subject, String content) {
        return EmailRequest.builder()
                .to(to)
                .subject(subject)
                .variables(Map.of("content", content))
                .templateName("simple-text")
                .build();
    }
    
    /**
     * Convenience method for creating template-based emails
     */
    public static EmailRequest template(String to, String subject, String templateName, Map<String, Object> variables) {
        return EmailRequest.builder()
                .to(to)
                .subject(subject)
                .templateName(templateName)
                .variables(variables)
                .build();
    }
    
    /**
     * Convenience method for creating emails with attachments
     */
    public EmailRequest withAttachment(File attachment) {
        this.attachments.add(attachment);
        return this;
    }
    
    /**
     * Convenience method for creating emails with multiple attachments
     */
    public EmailRequest withAttachments(List<File> attachments) {
        this.attachments.addAll(attachments);
        return this;
    }
}
