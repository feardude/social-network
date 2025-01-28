package ru.smax.social.network.friend;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Repository
class FriendRepository {

    private final JdbcTemplate jdbcTemplate;

    public void createFriendship(Long userId, Long friendId) {
        String sql = "insert into friends (user_id, friend_id, created_at) values (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, LocalDateTime.now());
    }

    public List<Friend> findByUserId(Long userId) {
        String sql = "select * from friends where user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriend, userId);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private Friend mapRowToFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getLong("user_id"),
                rs.getLong("friend_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public List<Integer> findFollowersIds(Integer userId) {
        String sql = "select user_id from friends where friend_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    public List<Integer> findAuthors(Integer followerId) {
        String sql = "select friend_id from friends where user_id = ? limit 100";
        return jdbcTemplate.queryForList(sql, Integer.class, followerId);
    }
}
