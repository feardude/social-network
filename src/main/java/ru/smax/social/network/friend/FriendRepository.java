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
        String sql = "INSERT INTO friends (user_id, friend_id, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, LocalDateTime.now());
    }

    public List<Friend> findByUserId(Long userId) {
        String sql = "SELECT * FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriend, userId);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private Friend mapRowToFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getLong("user_id"),
                rs.getLong("friend_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
