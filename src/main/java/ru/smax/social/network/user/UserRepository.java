package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Repository
class UserRepository {
    private static final String SQL_USER_SAVE = """
                insert into users.users (username, password, first_name, last_name, birthday, biography, city, gender)
                values (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SQL_USER_FIND_BY_USERNAME = """
                select username, first_name, last_name, birthday, biography, city, gender
                from users.users
                where username = ?
            """;
    private static final RowMapper<User> ROW_MAPPER_USER =
            (rs, _) -> User.builder()
                           .username(rs.getString("username"))
                           .firstName(rs.getString("first_name"))
                           .lastName(rs.getString("last_name"))
                           .birthday(dateOrNull(rs.getString("birthday")))
                           .biography(rs.getString("biography"))
                           .city(rs.getString("city"))
                           .gender(rs.getString("gender"))
                           .build();
    private static final String SQL_ACTIVE_IN_RANGE = """
            select id
            from users.users
            where last_active_at between ? and ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update(
                SQL_USER_SAVE,
                user.username(),
                user.password(),
                user.firstName(),
                user.lastName(),
                user.birthday(),
                user.biography(),
                user.city(),
                user.gender()
        );
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return jdbcTemplate.queryForObject(
                """
                        select username, first_name, last_name, birthday, biography, city, gender
                        from users.users
                        where id = ?
                        """,
                ROW_MAPPER_USER,
                id
        );
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    SQL_USER_FIND_BY_USERNAME,
                    ROW_MAPPER_USER,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalStateException(
                    "Expected 1 user but found multiple for username='%s'".formatted(username),
                    e
            );
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return jdbcTemplate.query(
                "select username, password, first_name, last_name, birthday, biography, city, gender from users.users",
                ROW_MAPPER_USER
        );
    }

    @Transactional(readOnly = true)
    public List<User> findByName(String firstName, String lastName) {
        return jdbcTemplate.query(
                """
                        select username, first_name, last_name, birthday, biography, city, gender
                        from users.users
                        where last_name like ?
                          and first_name like ?
                        order by username
                        """,
                ROW_MAPPER_USER,
                lastName,
                firstName
        );
    }

    public List<Integer> findIdsActiveInRange(LocalDate from, LocalDate to) {
        return jdbcTemplate.queryForList(
                SQL_ACTIVE_IN_RANGE,
                Integer.class,
                from, to
        );
    }

    private static LocalDate dateOrNull(String date) {
        return date == null ? null : LocalDate.parse(date);
    }
}
