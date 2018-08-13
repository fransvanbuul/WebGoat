package tech.vanbuul.webgoatplus.experiments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/plus/asyncJdbcSqlInjection")
@Slf4j
public class AsyncJdbcSqlInjection {

    @Autowired
    @Qualifier("plus")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("plus")
    private TransactionTemplate transactionTemplate;

    private final ScheduledExecutorService scheduledExecutorService  = Executors.newScheduledThreadPool(4);

    @PostMapping
    public void insert(@RequestParam String value) {
        scheduledExecutorService.schedule(() -> doInsert(value), 100, TimeUnit.MILLISECONDS);
    }

    private void doInsert(String value) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.execute("INSERT INTO dummy(id, val) VALUES('"
                    + UUID.randomUUID().toString() + "', '" + value + "')");
            return null;
        });
    }

    @GetMapping
    @Transactional("plus")
    public List<DummyRecord> select() {
        return jdbcTemplate.query("SELECT * FROM dummy",
                (ResultSet rs, int rowNum) ->
                        new DummyRecord(rs.getString(1), rs.getString(2)));

    }
}
