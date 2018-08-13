package tech.vanbuul.webgoatplus.experiments;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.GenericEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plus/axonJdbcSqlInjection")
@Slf4j
public class AxonJdbcSqlInjection {

    @Autowired
    @Qualifier("plus")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private EventBus eventBus;

    /* Enabling this will make it very hard to detect the vulnerability. */
//    @Autowired
//    public void config(EventProcessingConfiguration eventProcessingConfiguration) {
//        eventProcessingConfiguration.registerTrackingEventProcessor(getClass().getPackage().getName());
//    }

    @PostMapping
    public void insert(@RequestParam String value) {
        log.info("sending command to insert {}", value);
        commandGateway.send(new DummyRecord(UUID.randomUUID().toString(), value));
    }

    @CommandHandler
    public void handle(DummyRecord dummyRecord) {
        log.info("processing command, publishing event {}", dummyRecord);
        eventBus.publish(GenericEventMessage.asEventMessage(dummyRecord));
    }

    @EventHandler
    @Transactional("plus")
    public void on(DummyRecord dummyRecord) {
        log.info("processing event, inserting into db {}", dummyRecord);
        jdbcTemplate.execute("INSERT INTO dummy(id, val) VALUES('"
                + dummyRecord.getId() + "', '" + dummyRecord.getVal() + "')");
    }

    @GetMapping
    @Transactional("plus")
    public List<DummyRecord> select() {
        return jdbcTemplate.query("SELECT * FROM dummy",
                (ResultSet rs, int rowNum) ->
                        new DummyRecord(rs.getString(1), rs.getString(2)));

    }
}
