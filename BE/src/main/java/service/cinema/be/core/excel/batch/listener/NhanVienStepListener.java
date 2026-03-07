package service.cinema.be.core.excel.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NhanVienStepListener implements 
    ItemReadListener<Row>, 
    ItemProcessListener<Row, service.cinema.be.entity.NhanVien>, 
    ItemWriteListener<service.cinema.be.entity.NhanVien>,
    SkipListener<Row, service.cinema.be.entity.NhanVien> {

    private int readCount = 0;
    private int processCount = 0;
    private int writeCount = 0;
    private int skipCount = 0;

    @Override
    public void beforeRead() {
        // No action needed
    }

    @Override
    public void afterRead(Row item) {
        readCount++;
        if (readCount % 100 == 0) {
            log.info("NhanVien import progress: {} rows read", readCount);
        }
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Error reading NhanVien Excel row: {}", ex.getMessage());
    }

    @Override
    public void beforeProcess(Row item) {
        // No action needed
    }

    @Override
    public void afterProcess(Row item, service.cinema.be.entity.NhanVien result) {
        processCount++;
        if (processCount % 100 == 0) {
            log.info("NhanVien import progress: {} rows processed", processCount);
        }
    }

    @Override
    public void onProcessError(Row item, Exception e) {
        log.error("Error processing NhanVien row {}: {}", item.getRowNum() + 1, e.getMessage());
    }

    @Override
    public void beforeWrite(Chunk<? extends service.cinema.be.entity.NhanVien> items) {
        log.debug("Writing {} NhanVien records to database", items.size());
    }

    @Override
    public void afterWrite(Chunk<? extends service.cinema.be.entity.NhanVien> items) {
        writeCount += items.size();
        log.info("NhanVien import progress: {} records written to database", writeCount);
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends service.cinema.be.entity.NhanVien> items) {
        log.error("Error writing {} NhanVien records to database: {}", items.size(), exception.getMessage());
    }

    @Override
    public void onSkipInRead(Throwable t) {
        skipCount++;
        log.warn("Skipped reading NhanVien row due to: {}", t.getMessage());
    }

    // Additional methods for monitoring
    public int getReadCount() {
        return readCount;
    }

    public int getProcessCount() {
        return processCount;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public int getSkipCount() {
        return skipCount;
    }
}
