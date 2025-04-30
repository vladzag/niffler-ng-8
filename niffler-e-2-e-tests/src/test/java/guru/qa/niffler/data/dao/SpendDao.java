package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);

    SpendEntity update(SpendEntity spend);

    List<SpendEntity> findAll();

    Optional<SpendEntity> findById(UUID id);

    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    void remove(SpendEntity spend);
}
