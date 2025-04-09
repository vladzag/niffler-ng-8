package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import static guru.qa.niffler.data.Databases.transaction;


public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                    CategoryEntity category2 = new CategoryDaoJdbc(connection).create(
                            CategoryEntity.fromJson(category)
                    );
                    return CategoryJson.fromEntity(category2);
                },
                CFG.spendJdbcUrl());
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).update(categoryEntity));
                },
                CFG.spendJdbcUrl()
        );

    }
}