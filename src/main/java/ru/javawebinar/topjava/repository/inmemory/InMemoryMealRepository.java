package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(meal, 0);
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        try {
            return repository.get(id).getUserId() == userId && (repository.remove(id) != null);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        try {
            return meal.getUserId() == userId ? meal : null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // TODO подумай, как можно изменить структуру данных, чтобы было проще доставать еду по userId в методах getAll/фильтр. Данных может быть очень много, и все перебирать все для поиска по userId может быть достаточно долго.
    @Override
    public List<Meal> getAll(int userId) {
        Comparator<Meal> compareByDateReverse = Comparator.comparing(Meal::getDateTime).reversed();
        return repository.values().stream()
                .filter(meal -> userId == meal.getUserId())
                .sorted(compareByDateReverse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllSortedByDate(int userId, LocalDate startDate, LocalDate endDate) {
        return getAll(userId).stream().filter(meal ->
                DateTimeUtil.isBetweenHalfOpen(LocalDateTime.of(meal.getDate(), LocalTime.MIN), LocalDateTime.of(startDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MIN)))
                .collect(Collectors.toList());
    }
}

