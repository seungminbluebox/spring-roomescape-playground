package roomescape;

import java.time.LocalTime;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TimeRepository {

    private final JdbcTemplate jdbcTemplate;

    public TimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Time findTimeById(long id) {
        String sql = "SELECT id, time FROM time WHERE id = ?";

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> Time.create(
                rs.getLong("id"),
                rs.getObject("time", LocalTime.class)
            ),
            id
        );
    }
}
