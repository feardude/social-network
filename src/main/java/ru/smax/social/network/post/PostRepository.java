package ru.smax.social.network.post;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
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
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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

    public Map<Integer, List<UUID>> findFriendPostIds(List<Integer> userIds) {
        var sql = """
                select f.user_id as user_id,
                       p.id      as post_id
                from posts p
                join friends f on f.friend_id = p.author_user_id
                where f.user_id in (:userIds)
                  and p.author_user_id = f.friend_id
                """;

        var rows = namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource("userIds", userIds));
        log.info("Found {} rows", rows.size());

        Map<Integer, List<UUID>> userIdToPostIds = HashMap.newHashMap(userIds.size());
        for (var row : rows) {
            var userId = (Integer) row.get("user_id");
            var postId = (UUID) row.get("post_id");
            userIdToPostIds.computeIfAbsent(
                                   userId,
                                   _ -> new ArrayList<>())
                           .add(postId);
        }

        return userIdToPostIds;
    }

    public List<Post> findById(List<UUID> postIds) {
        var sql = """
                select id, text, author_user_id
                        from posts
                        where id in (:ids)
                """;

        return namedParameterJdbcTemplate.query(sql, Map.of("ids", postIds), ROW_MAPPER_POST);
    }
}
