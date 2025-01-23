package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Repository
class PostRepository {
    private static final String SQL_FIND_FRIENDS_POSTS = """
                        select p.*
                        from posts p
                        join friends f on f.friend_id = p.author_user_id
                        where f.user_id = ?
                         and p.author_user_id = f.friend_id
                        order by p.created_at desc
            """;

    private static final String SQL_FIND_FRIENDS_POSTS_LIMITED = """
                        select p.*
                        from posts p
                        join friends f on f.friend_id = p.author_user_id
                        where f.user_id = ?
                         and p.author_user_id = f.friend_id
                        order by p.created_at desc
                        limit ? offset ?
            """;

    private static final RowMapper<Post> ROW_MAPPER_POST =
            (rs, _) -> Post.builder()
                           .id(UUID.fromString(rs.getString("id")))
                           .text(rs.getString("text"))
                           .authorUserId(rs.getInt("author_user_id"))
                           .build();

    private final JdbcTemplate jdbcTemplate;

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

    public List<Post> findFriendsPosts(Integer userId, Integer offset, Integer limit) {
        return jdbcTemplate.query(
                SQL_FIND_FRIENDS_POSTS_LIMITED,
                this::rowToPost,
                userId, limit, offset
        );
    }

    public List<Post> findFriendsPosts(Integer userId) {
        return jdbcTemplate.query(
                SQL_FIND_FRIENDS_POSTS,
                this::rowToPost,
                userId
        );
    }

    private Post rowToPost(ResultSet rs, int rowNum) throws SQLException {
        return new Post(
                UUID.fromString(rs.getString("id")),
                rs.getString("text"),
                rs.getInt("author_user_id")
        );
    }
}
