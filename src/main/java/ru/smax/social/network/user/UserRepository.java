package ru.smax.social.network.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
class UserRepository {
    private static final String SQL_USER_SAVE = "insert into users.users (username, password) values (?, ?)";
    private static final String SQL_USER_FIND_BY_USERNAME = "select username, password from users.users where username = ?";
    private static final RowMapper<User> ROW_MAPPER_USER = (rs, _) -> User.builder()
                                                                          .username(rs.getString("username"))
                                                                          .password(rs.getString("password"))
                                                                          .build();

    private final JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update(
                SQL_USER_SAVE,
                user.getUsername(),
                user.getPassword()
        );
    }

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

    public List<User> findAll() {
        return jdbcTemplate.query(
                "select username, password from users.users",
                ROW_MAPPER_USER
        );
    }
}
