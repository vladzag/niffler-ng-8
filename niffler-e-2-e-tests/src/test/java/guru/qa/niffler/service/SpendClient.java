package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson update(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    Optional<CategoryJson> findCategoryById(UUID id);

    void removeCategory(CategoryJson category);
}
