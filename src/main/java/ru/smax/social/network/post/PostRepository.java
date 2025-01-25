package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                        limit ?
            """;

    private static final RowMapper<Post> ROW_MAPPER_POST =
            (rs, _) -> Post.builder()
                           .id(UUID.fromString(rs.getString("id")))
                           .text(rs.getString("text"))
                           .authorUserId(rs.getInt("author_user_id"))
                           .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                           .build();

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Post findById(UUID id) {
        return jdbcTemplate.queryForObject(
                """
                        select id, text, author_user_id, created_at
                        from posts
                        where id = ?
                        """,
                ROW_MAPPER_POST,
                id
        );
    }

    public List<Post> findFriendsPosts(Integer userId, Integer limit) {
        return jdbcTemplate.query(
                SQL_FIND_FRIENDS_POSTS,
                ROW_MAPPER_POST,
                userId, limit
        );
    }

    public Map<Integer, List<Post>> findFriendPosts(List<Integer> userIds) {
        var sql = """ 
                select user_id, id, author_user_id, text, created_at
                from (
                    select f.user_id as user_id,
                        p.id,
                        p.author_user_id,
                        p.text,
                        p.created_at,
                        row_number() over (partition by f.user_id order by p.created_at desc) as rn
                    from posts p
                          join friends f on f.friend_id = p.author_user_id
                    where f.user_id in (:userIds)
                    and p.author_user_id = f.friend_id
                ) subquery
                where rn <= 100
                """;

        Map<Integer, List<Post>> userIdToPostIds = HashMap.newHashMap(userIds.size());
        var rows = namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource("userIds", userIds));
        for (var row : rows) {
            var userId = (Integer) row.get("user_id");
            var post = Post.builder()
                           .id((UUID) row.get("id"))
                           .text((String) row.get("text"))
                           .authorUserId((int) row.get("author_user_id"))
                           .createdAt(((Timestamp) row.get("created_at")).toLocalDateTime())
                           .build();
            userIdToPostIds.computeIfAbsent(userId, _ -> new ArrayList<>())
                           .add(post);
        }

        return userIdToPostIds;
    }

    public void savePost(Post newPost) {
        var sql = "insert into posts (id, text, author_user_id, created_at) values (?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                newPost.id(),
                newPost.text(),
                newPost.authorUserId(),
                newPost.createdAt()
        );
    }
}
