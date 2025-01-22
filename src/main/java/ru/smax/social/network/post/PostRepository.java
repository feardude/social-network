package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.smax.social.network.user.User;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Repository
class PostRepository {
    private static final String SQL_POST_SAVE = """
                insert into posts (id, text, author_user_id)
                values (?, ?, ?)
            """;
    private static final String SQL_USER_FIND_BY_USERNAME = """
                select username, first_name, last_name, birthday, biography, city, gender
                from users.users
                where username = ?
            """;
    private static final RowMapper<Post> ROW_MAPPER_POST =
            (rs, _) -> Post.builder()
                           .id(UUID.fromString(rs.getString("id")))
                           .text(rs.getString("text"))
                           .authorUserId(rs.getInt("author_user_id"))
                           .build();

    private final JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update(
                SQL_POST_SAVE,
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

    public void save(List<Object[]> posts) {
        jdbcTemplate.batchUpdate(SQL_POST_SAVE, posts);
        System.out.println("Saved posts: " + posts.size());
    }

    @Transactional(readOnly = true)
    public Post findById(UUID id) {
        return jdbcTemplate.queryForObject(
                """
                        select id, text, author_user_id
                        from posts
                        where id = ?
                        """,
                ROW_MAPPER_POST,
                id
        );
    }

//    @Transactional(readOnly = true)
//    public List<User> findAll() {
//        return jdbcTemplate.query(
//                "select username, password, first_name, last_name, birthday, biography, city, gender from users.users",
//                ROW_MAPPER_POST
//        );
//    }
}
