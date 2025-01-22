package ru.smax.social.network.friend;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
@AllArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;

    @PostConstruct
    public void init() {
        log.info("Создаём друзей");
        var celebs = IntStream.rangeClosed(1000, 1100)
                              .boxed()
                              .collect(toSet());
//        celebs(celebs);
        friends(celebs);

        log.info("Создаём друзей: готово");
    }

    private void celebs(List<Integer> celebs) {
        List<Object[]> friends = new ArrayList<>(10000);

        // каждый 5й подписан на селебу
        for (int i = 2; i <= 999000; i++) {
            // Случайное количество селебрити, на которых подписан пользователь (от 10 до 100)
            int numberOfCelebs = ThreadLocalRandom.current().nextInt(10, 101);

            // Случайно выбираем ID селебрити из списка
            var selectedCelebs = ThreadLocalRandom.current()
                                                  .ints(numberOfCelebs * 5L, 0, celebs.size())
                                                  .distinct()
                                                  .limit(numberOfCelebs)
                                                  .mapToObj(celebs::get)
                                                  .toList();

            // Создаем дружбу с каждым выбранным селебом
            LocalDateTime now = LocalDateTime.now();
            for (int celebId : selectedCelebs) {
//                createFriendship((long) i, (long) celebId);
                friends.add(new Object[]{(long) i, (long) celebId, now});
            }


            if (9_000 <= friends.size() && friends.size() <= 11_000) {
                friendRepository.createFriendships(friends);
                log.info("Сохранили 10 000 дружеских связей");
                friends.clear();
            }
        }

        if (!friends.isEmpty()) {
            friendRepository.createFriendships(friends);
            log.info("Сохранили {} дружеских связей", friends.size());
            friends.clear();
        }
    }

    private void friends(Set<Integer> celebs) {
        List<Object[]> friends = new ArrayList<>(10000);

        int minId = 2;
        int maxId = 999000;
        for (int i = minId; i <= maxId; i++) {
            // Случайное количество селебрити, на которых подписан пользователь (от 10 до 100)
            int numberOfFriends = ThreadLocalRandom.current().nextInt(10, 2_000);

            // Случайно выбираем ID селебрити из списка
            var selectedFriends = ThreadLocalRandom.current()
                                                   .ints(numberOfFriends * 5L, minId, maxId)
                                                   .distinct()
                                                   .filter(o -> !celebs.contains(o))
                                                   .limit(numberOfFriends)
                                                   .boxed()
                                                   .toList();

            // Создаем дружбу с каждым выбранным селебом
            LocalDateTime now = LocalDateTime.now();
            for (int friendId : selectedFriends) {
                friends.add(new Object[]{(long) i, (long) friendId, now});
            }


            if (friends.size() >= 10_000) {
                friendRepository.createFriendships(friends);
                log.info("Сохранили {} дружеских связей", friends.size());
                friends.clear();
            }
        }

        if (!friends.isEmpty()) {
            friendRepository.createFriendships(friends);
            log.info("Сохранили {} дружеских связей", friends.size());
            friends.clear();
        }
    }

    public void createFriendship(Long userId, Long friendId) {
        friendRepository.createFriendship(userId, friendId);
    }

    public List<Friend> getFriends(Long userId) {
        return friendRepository.findByUserId(userId);
    }

    public void deleteFriendship(Long userId, Long friendId) {
        friendRepository.deleteFriendship(userId, friendId);
    }
}

