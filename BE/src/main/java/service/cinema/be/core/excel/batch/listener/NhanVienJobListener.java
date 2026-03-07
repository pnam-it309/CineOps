package service.cinema.be.core.excel.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NhanVienJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        log.info("Starting NhanVien import job for file: {}", filePath);
        log.info("Job ID: {}", jobExecution.getJobId());
        jobExecution.getExecutionContext().putString("jobType", "NhanVien");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("NhanVien import job FAILED for file: {}", filePath);
            log.error("Job status: {}", jobExecution.getStatus());
            log.error("Exit status: {}", jobExecution.getExitStatus());
            
            // Log all exceptions
            for (Throwable exception : jobExecution.getAllFailureExceptions()) {
                log.error("Job failure exception", exception);
            }
        } else {
            log.info("NhanVien import job COMPLETED successfully for file: {}", filePath);
            log.info("Job status: {}", jobExecution.getStatus());
            log.info("Exit status: {}", jobExecution.getExitStatus());
            
            // Log step statistics
            jobExecution.getStepExecutions().forEach(step -> {
                log.info("Step '{}' - Read: {}, Write: {}, Skip: {}, Commit: {}", 
                    step.getStepName(),
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getSkipCount(),
                    step.getCommitCount());
            });
        }
    }
}
