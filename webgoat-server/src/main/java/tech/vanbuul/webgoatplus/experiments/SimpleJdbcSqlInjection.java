package tech.vanbuul.webgoatplus.experiments;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plus/simpleJdbcSqlInjection")
@Slf4j
public class SimpleJdbcSqlInjection {

    @Autowired
    @Qualifier("plus")
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    @Transactional("plus")
    public void insert(@RequestParam String value) {
        jdbcTemplate.execute("INSERT INTO dummy(id, val) VALUES('"
                + UUID.randomUUID().toString() + "', '" + value + "')");
    }

    @GetMapping
    @Transactional("plus")
    public List<DummyRecord> select() {
        return jdbcTemplate.query("SELECT * FROM dummy",
                (ResultSet rs, int rowNum) ->
                        new DummyRecord(rs.getString(1), rs.getString(2)));

    }
}
