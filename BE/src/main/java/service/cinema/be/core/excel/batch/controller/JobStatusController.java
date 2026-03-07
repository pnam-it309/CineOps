package service.cinema.be.core.excel.batch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/excel/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Status Monitoring", description = "Monitor and manage Excel import jobs")
@Slf4j
public class JobStatusController {

    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobLauncher jobLauncher;

    @GetMapping("/status/{jobExecutionId}")
    @Operation(summary = "Get job execution status", description = "Returns detailed status of a specific job execution")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long jobExecutionId) {
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            if (jobExecution == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = createJobStatusResponse(jobExecution);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving job status for ID: {}", jobExecutionId, e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Failed to retrieve job status: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    @Operation(summary = "List all job executions", description = "Returns a list of all job executions with optional filtering")
    public ResponseEntity<Map<String, Object>> listJobs(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) BatchStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<JobExecution> allExecutions;
            
            if (jobName != null) {
                allExecutions = getJobExecutionsByName(jobName);
            } else {
                allExecutions = getAllJobExecutions();
            }

            // Filter by status if provided
            if (status != null) {
                allExecutions = allExecutions.stream()
                    .filter(exec -> exec.getStatus() == status)
                    .collect(Collectors.toList());
            }

            // Sort by start time descending
            allExecutions.sort((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()));

            // Pagination
            int total = allExecutions.size();
            int start = page * size;
            int end = Math.min(start + size, total);
            
            List<JobExecution> pageExecutions = start < total 
                ? allExecutions.subList(start, end) 
                : Collections.emptyList();

            List<Map<String, Object>> jobList = pageExecutions.stream()
                .map(this::createJobStatusResponse)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobList);
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("totalElements", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error listing jobs", e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Failed to list jobs: " + e.getMessage()));
        }
    }

    @GetMapping("/running")
    @Operation(summary = "Get all running jobs", description = "Returns all currently running job executions")
    public ResponseEntity<List<Map<String, Object>>> getRunningJobs() {
        try {
            List<JobExecution> runningJobs = getAllJobExecutions().stream()
                .filter(exec -> exec.getStatus() == BatchStatus.STARTED || 
                             exec.getStatus() == BatchStatus.STARTING)
                .collect(Collectors.toList());

            List<Map<String, Object>> response = runningJobs.stream()
                .map(this::createJobStatusResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving running jobs", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/stop/{jobExecutionId}")
    @Operation(summary = "Stop a running job", description = "Attempts to stop a currently running job execution")
    public ResponseEntity<Map<String, Object>> stopJob(@PathVariable Long jobExecutionId) {
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            if (jobExecution == null) {
                return ResponseEntity.notFound().build();
            }

            if (jobExecution.getStatus() != BatchStatus.STARTED && 
                jobExecution.getStatus() != BatchStatus.STARTING) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Job is not running and cannot be stopped"));
            }

            // Request job to stop
            jobExecution.setStatus(BatchStatus.STOPPING);
            jobRepository.update(jobExecution);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stop request sent to job");
            response.put("jobId", jobExecutionId);
            response.put("status", jobExecution.getStatus().name());

            log.info("Stop request sent to job execution: {}", jobExecutionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error stopping job: {}", jobExecutionId, e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Failed to stop job: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get job statistics", description = "Returns overall statistics about job executions")
    public ResponseEntity<Map<String, Object>> getJobStatistics() {
        try {
            List<JobExecution> allExecutions = getAllJobExecutions();
            
            Map<BatchStatus, Long> statusCounts = allExecutions.stream()
                .collect(Collectors.groupingBy(
                    JobExecution::getStatus,
                    Collectors.counting()
                ));

            Map<String, Long> jobTypeCounts = allExecutions.stream()
                .collect(Collectors.groupingBy(
                    exec -> exec.getJobInstance().getJobName(),
                    Collectors.counting()
                ));

            // Calculate success rate
            long total = allExecutions.size();
            long completed = statusCounts.getOrDefault(BatchStatus.COMPLETED, 0L);
            double successRate = total > 0 ? (double) completed / total * 100 : 0;

            // Get recent jobs (last 24 hours)
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            long recentJobs = allExecutions.stream()
                .filter(exec -> exec.getStartTime() != null && exec.getStartTime().isAfter(yesterday))
                .count();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalJobs", total);
            statistics.put("statusBreakdown", statusCounts);
            statistics.put("jobTypeBreakdown", jobTypeCounts);
            statistics.put("successRate", Math.round(successRate * 100.0) / 100.0);
            statistics.put("recentJobs24h", recentJobs);
            statistics.put("runningJobs", statusCounts.getOrDefault(BatchStatus.STARTED, 0L));
            statistics.put("failedJobs", statusCounts.getOrDefault(BatchStatus.FAILED, 0L));

            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            log.error("Error retrieving job statistics", e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("Failed to retrieve statistics: " + e.getMessage()));
        }
    }

    private Map<String, Object> createJobStatusResponse(JobExecution jobExecution) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("jobId", jobExecution.getJobId());
        response.put("jobName", jobExecution.getJobInstance().getJobName());
        response.put("status", jobExecution.getStatus().name());
        response.put("startTime", jobExecution.getStartTime());
        response.put("endTime", jobExecution.getEndTime());
        response.put("exitStatus", jobExecution.getExitStatus().getExitCode());
        response.put("exitDescription", jobExecution.getExitStatus().getExitDescription());
        
        // Calculate duration
        if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
            long startMillis = jobExecution.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endMillis = jobExecution.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long duration = endMillis - startMillis;
            response.put("durationMs", duration);
            response.put("durationSeconds", duration / 1000);
        } else if (jobExecution.getStartTime() != null) {
            long startMillis = jobExecution.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long duration = System.currentTimeMillis() - startMillis;
            response.put("durationMs", duration);
            response.put("durationSeconds", duration / 1000);
        }

        // Step information
        List<Map<String, Object>> steps = jobExecution.getStepExecutions().stream()
            .map(step -> {
                Map<String, Object> stepInfo = new HashMap<>();
                stepInfo.put("stepName", step.getStepName());
                stepInfo.put("status", step.getStatus().name());
                stepInfo.put("readCount", step.getReadCount());
                stepInfo.put("writeCount", step.getWriteCount());
                stepInfo.put("skipCount", step.getSkipCount());
                stepInfo.put("commitCount", step.getCommitCount());
                stepInfo.put("rollbackCount", step.getRollbackCount());
                return stepInfo;
            })
            .collect(Collectors.toList());
        
        response.put("steps", steps);

        // Job parameters
        Map<String, Object> parameters = new HashMap<>();
        jobExecution.getJobParameters().getParameters().forEach((key, param) -> {
            parameters.put(key, param.getValue());
        });
        response.put("parameters", parameters);

        return response;
    }

    private List<JobExecution> getAllJobExecutions() {
        List<JobExecution> allExecutions = new ArrayList<>();
        List<String> jobNames = jobExplorer.getJobNames();
        for (String name : jobNames) {
            allExecutions.addAll(getJobExecutionsByName(name));
        }
        return allExecutions;
    }

    private List<JobExecution> getJobExecutionsByName(String jobName) {
        List<JobExecution> executions = new ArrayList<>();
        int start = 0;
        int count = 100;
        List<JobInstance> instances;
        do {
            instances = jobExplorer.getJobInstances(jobName, start, count);
            for (JobInstance instance : instances) {
                executions.addAll(jobExplorer.getJobExecutions(instance));
            }
            start += count;
        } while (instances.size() == count);
        return executions;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
