package roomescape;

import java.net.URI;
import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.NotFoundException;

@Controller
public class TimeController {

    private final ReservationService reservationService;
    private final JdbcTemplate jdbcTemplate;

    public TimeController(ReservationService reservationService, JdbcTemplate jdbcTemplate) {
        this.reservationService = reservationService;
        this.jdbcTemplate = jdbcTemplate;
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<Void> handleInvalidReservationException() {
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/times")
    public ResponseEntity<Time> createTime(@RequestBody TimeRequest timeRequest) {

        timeRequest.validate();

        Time newTime = Time.create(timeRequest.getTime());

        String sql = "INSERT INTO time(time) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                sql,
                new String[]{"id"}
            );

            ps.setObject(1, newTime.getTime());

            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();

        Time time = newTime.withId(id);

        return ResponseEntity
            .created(URI.create("/times/" + time.getId()))
            .body(time);
    }

    @GetMapping("/times")
    @ResponseBody
    public List<Time> readTimes() {
        String sql = "SELECT id, time FROM time";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> Time.create(
                rs.getLong("id"),
                rs.getObject("time", LocalTime.class)
            )
        );
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {

        int deletedCount = jdbcTemplate.update(
            "DELETE FROM time WHERE id = ?",
            id
        );
        if (deletedCount == 0) {
            throw new NotFoundException("Reservation not found: id=" + id);
        }

        return ResponseEntity.noContent().build();
    }

}
