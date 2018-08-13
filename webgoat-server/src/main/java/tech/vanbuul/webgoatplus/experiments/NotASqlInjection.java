package tech.vanbuul.webgoatplus.experiments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plus/notASqlInjection")
@Slf4j
public class NotASqlInjection {

    @Component
    @Slf4j
    public static class NotAJDBCDriver {

        public void execute(String notSql) {
            log.info("notSql: {}", notSql);
        }

    }

    @Autowired
    private NotAJDBCDriver notAJDBCDriver;

    @PostMapping
    @Transactional("plus")
    public void insert(@RequestParam String value) {
        notAJDBCDriver.execute("INSERT INTO dummy(id, val) VALUES('"
                + UUID.randomUUID().toString() + "', '" + value + "')");
    }

}
