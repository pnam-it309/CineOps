package service.cinema.be.core.excel.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import service.cinema.be.core.excel.batch.listener.NhanVienJobListener;
import service.cinema.be.core.excel.batch.listener.NhanVienStepListener;
import service.cinema.be.core.excel.batch.listener.PhimJobListener;
import service.cinema.be.core.excel.batch.listener.PhimStepListener;
import service.cinema.be.core.excel.batch.processor.GenericExcelProcessor;
import service.cinema.be.core.excel.batch.reader.ExcelItemReader;
import service.cinema.be.entity.Phim;
import service.cinema.be.entity.NhanVien;
import service.cinema.be.repository.PhimRepository;
import service.cinema.be.repository.NhanVienRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@EnableBatchProcessing
public class ExcelBatchConfig {

    private final PhimRepository phimRepository;
    private final NhanVienRepository nhanVienRepository;

    @Autowired
    public ExcelBatchConfig(@Qualifier("phimRepository") PhimRepository phimRepository,
                           @Qualifier("nhanVienRepository") NhanVienRepository nhanVienRepository) {
        this.phimRepository = phimRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    // ===== PHIM (MOVIE) BATCH CONFIGURATION =====

    @Bean
    @JobScope
    public ExcelItemReader phimItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        ExcelItemReader reader = new ExcelItemReader();
        reader.setResource(new FileSystemResource(filePath));
        reader.setSheetName("Phim");
        reader.setHeaderRow(0);
        reader.setDataStartRow(1);
        log.debug("Created Phim Excel reader for file: {}", filePath);
        return reader;
    }

    @Bean
    @JobScope
    public GenericExcelProcessor<Phim> phimItemProcessor(@Value("#{jobParameters['filePath']}") String filePath) {
        // Create header mapping based on Phim entity annotations
        Map<String, Integer> headerMap = createPhimHeaderMap();
        GenericExcelProcessor<Phim> processor = new GenericExcelProcessor<>(Phim.class, headerMap);
        log.debug("Created Phim Excel processor");
        return processor;
    }

    @Bean
    @JobScope
    public BeanValidatingItemProcessor<Phim> phimValidator() {
        BeanValidatingItemProcessor<Phim> validator = new BeanValidatingItemProcessor<>();
        try {
            validator.afterPropertiesSet();
        } catch (Exception e) {
            log.error("Failed to initialize Phim validator", e);
        }
        return validator;
    }

    @Bean
    public RepositoryItemWriter<Phim> phimItemWriter() {
        RepositoryItemWriter<Phim> writer = new RepositoryItemWriter<>();
        writer.setRepository(phimRepository);
        writer.setMethodName("saveAll");
        log.debug("Created Phim Excel writer");
        return writer;
    }

    @Bean
    public Step importPhimStep(JobRepository jobRepository, 
                              PlatformTransactionManager transactionManager,
                              ExcelItemReader phimItemReader,
                              GenericExcelProcessor<Phim> phimItemProcessor,
                              BeanValidatingItemProcessor<Phim> phimValidator,
                              RepositoryItemWriter<Phim> phimItemWriter) {
        
        CompositeItemProcessor<Row, Phim> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(List.of(phimItemProcessor, phimValidator));

        return new StepBuilder("importPhimStep", jobRepository)
                .<Row, Phim>chunk(500, transactionManager) // Process 500 records at a time
                .reader(phimItemReader)
                .processor(compositeProcessor)
                .writer(phimItemWriter)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(100) // Skip up to 100 validation errors
                .listener((org.springframework.batch.core.ItemProcessListener<Row, Phim>) new PhimStepListener())
                .listener((org.springframework.batch.core.SkipListener<Row, Phim>) new PhimStepListener())
                .build();
    }

    @Bean
    public Job importPhimJob(JobRepository jobRepository, Step importPhimStep) {
        return new JobBuilder("importPhimJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importPhimStep)
                .listener(new PhimJobListener())
                .build();
    }

    // ===== NHAN VIEN (STAFF) BATCH CONFIGURATION =====

    @Bean
    @JobScope
    public ExcelItemReader nhanVienItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        ExcelItemReader reader = new ExcelItemReader();
        reader.setResource(new FileSystemResource(filePath));
        reader.setSheetName("Nhân Viên");
        reader.setHeaderRow(0);
        reader.setDataStartRow(1);
        log.debug("Created NhanVien Excel reader for file: {}", filePath);
        return reader;
    }

    @Bean
    @JobScope
    public GenericExcelProcessor<NhanVien> nhanVienItemProcessor(@Value("#{jobParameters['filePath']}") String filePath) {
        Map<String, Integer> headerMap = createNhanVienHeaderMap();
        GenericExcelProcessor<NhanVien> processor = new GenericExcelProcessor<>(NhanVien.class, headerMap);
        log.debug("Created NhanVien Excel processor");
        return processor;
    }

    @Bean
    @JobScope
    public BeanValidatingItemProcessor<NhanVien> nhanVienValidator() {
        BeanValidatingItemProcessor<NhanVien> validator = new BeanValidatingItemProcessor<>();
        try {
            validator.afterPropertiesSet();
        } catch (Exception e) {
            log.error("Failed to initialize NhanVien validator", e);
        }
        return validator;
    }

    @Bean
    public RepositoryItemWriter<NhanVien> nhanVienItemWriter() {
        RepositoryItemWriter<NhanVien> writer = new RepositoryItemWriter<>();
        writer.setRepository(nhanVienRepository);
        writer.setMethodName("saveAll");
        log.debug("Created NhanVien Excel writer");
        return writer;
    }

    @Bean
    public Step importNhanVienStep(JobRepository jobRepository, 
                                 PlatformTransactionManager transactionManager,
                                 ExcelItemReader nhanVienItemReader,
                                 GenericExcelProcessor<NhanVien> nhanVienItemProcessor,
                                 BeanValidatingItemProcessor<NhanVien> nhanVienValidator,
                                 RepositoryItemWriter<NhanVien> nhanVienItemWriter) {
        
        CompositeItemProcessor<Row, NhanVien> compositeProcessor = new CompositeItemProcessor<>();
        compositeProcessor.setDelegates(List.of(nhanVienItemProcessor, nhanVienValidator));

        return new StepBuilder("importNhanVienStep", jobRepository)
                .<Row, NhanVien>chunk(500, transactionManager)
                .reader(nhanVienItemReader)
                .processor(compositeProcessor)
                .writer(nhanVienItemWriter)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(100)
                .listener((org.springframework.batch.core.ItemProcessListener<Row, NhanVien>) new NhanVienStepListener())
                .listener((org.springframework.batch.core.SkipListener<Row, NhanVien>) new NhanVienStepListener())
                .build();
    }

    @Bean
    public Job importNhanVienJob(JobRepository jobRepository, Step importNhanVienStep) {
        return new JobBuilder("importNhanVienJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importNhanVienStep)
                .listener(new NhanVienJobListener())
                .build();
    }

    // ===== HEADER MAPPING METHODS =====

    private Map<String, Integer> createPhimHeaderMap() {
        Map<String, Integer> headerMap = new HashMap<>();
        // Map based on Phim entity ExcelColumn annotations
        headerMap.put("mã phim", 0);
        headerMap.put("tên phim", 1);
        headerMap.put("thời lượng (phút)", 2);
        headerMap.put("ngày khởi chiếu", 3);
        headerMap.put("ngày kết thúc", 4);
        headerMap.put("lịch chiếu", 5);
        headerMap.put("trailer", 6);
        headerMap.put("poster", 7);
        headerMap.put("ngôn ngữ", 8);
        headerMap.put("độ tuổi", 9);
        headerMap.put("mô tả", 10);
        headerMap.put("đánh giá", 11);
        headerMap.put("trạng thái", 12);
        headerMap.put("giá phim", 13);
        headerMap.put("loại phim", 14);
        headerMap.put("phụ phí loại phim", 15);
        return headerMap;
    }

    private Map<String, Integer> createNhanVienHeaderMap() {
        Map<String, Integer> headerMap = new HashMap<>();
        // Map based on NhanVien entity ExcelColumn annotations
        headerMap.put("mã nhân viên", 0);
        headerMap.put("tên nhân viên", 1);
        headerMap.put("cccd", 2);
        headerMap.put("ngày sinh", 3);
        headerMap.put("quê quán", 4);
        headerMap.put("giới tính", 5);
        headerMap.put("email", 6);
        headerMap.put("số điện thoại", 7);
        headerMap.put("chức vụ", 8);
        headerMap.put("ảnh nhân viên", 9);
        headerMap.put("trạng thái", 10);
        headerMap.put("mật khẩu", 11);
        return headerMap;
    }

    // ===== VALIDATION FACTORY =====

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    @Bean
    public Validator validator(ValidatorFactory validatorFactory) {
        return validatorFactory.getValidator();
    }
}
