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

    private final TimeRepository timeRepository;

    public TimeController(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<Void> handleInvalidReservationException() {
        return ResponseEntity.badRequest().build();
    }

//////////////////////////////////////////////////////////////////
    @PostMapping("/times")
    public ResponseEntity<Time> createTime(@RequestBody TimeRequest timeRequest) {

        timeRequest.validate();
        Time time=timeRepository.createTime(timeRequest);

        return ResponseEntity
            .created(URI.create("/times/" + time.getId()))
            .body(time);
    }

    @GetMapping("/times")
    @ResponseBody
    public List<Time> readTimes() {
        return timeRepository.readTimes();
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable long id) {

        timeRepository.deleteTime(id);

        return ResponseEntity.noContent().build();
    }

}
