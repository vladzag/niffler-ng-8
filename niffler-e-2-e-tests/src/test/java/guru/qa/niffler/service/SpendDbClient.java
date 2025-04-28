package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.templates.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    //  private final SpendRepository spendRepository = new SpendRepositoryJdbc(); //TODO: JDBC
    //  private final SpendRepository spendRepository = new SpendRepositorySpringJdbc(); //TODO: Spring
    private final SpendRepository spendRepository = new SpendRepositoryHibernate(); // TODO: Hibernate


    private static final Config CFG = Config.getInstance();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() ->
                SpendJson.fromEntity(
                        spendRepository.create(SpendEntity.fromJson(spend))
                ));
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        return CategoryJson.fromEntity(
                spendRepository.createCategory(CategoryEntity.fromJson(categoryJson))
        );
    }

    public SpendJson update(SpendJson spend) {
        spendRepository.findById(spend.id()).orElseThrow();
        return SpendJson.fromEntity(
                spendRepository.update(SpendEntity.fromJson(spend))
        );
    }

    public Optional<SpendJson> findByUsernameAndSpendDescription(SpendJson spend) {
        if (spend.username() != null && spend.description() != null) {
            Optional<SpendEntity> se = spendRepository.findByUsernameAndSpendDescription(
                    spend.username(),
                    spend.description()
            );
            if (se.isPresent()) {
                return Optional.of(SpendJson.fromEntity(se.get()));
            }
        }
        return Optional.empty();
    }

    public Optional<CategoryJson> findCategoryByUsernameAndName(CategoryJson category) {
        Optional<CategoryEntity> catOpt = spendRepository.findCategoryByUsernameAndName(
                category.username(),
                category.name()
        );
        if (catOpt.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(catOpt.get()));
        }
        return Optional.empty();
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        Optional<SpendEntity> spendOpt = spendRepository.findById(id);
        if (spendOpt.isPresent()) {
            return Optional.of(SpendJson.fromEntity(spendOpt.get()));
        }
        return Optional.empty();
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        Optional<CategoryEntity> catOpt = spendRepository.findCategoryById(id);
        if (catOpt.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(catOpt.get()));
        }
        return Optional.empty();
    }

    public void removeSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.findById(spend.id()).orElseThrow();
            spendRepository.remove(SpendEntity.fromJson(spend));
            return null;
        });
    }

    public void removeCategory(CategoryJson createdCategory) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.findCategoryById(createdCategory.id()).orElseThrow();
            spendRepository.removeCategory(CategoryEntity.fromJson(createdCategory));
            return null;
        });
    }
}
