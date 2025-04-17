package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import guru.qa.niffler.data.templates.DataSources;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();


    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder keyholder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO category (username, name, archived) " +
                                    "VALUES (?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setString(1, category.getName());
                    preparedStatement.setString(2, category.getUsername());
                    preparedStatement.setBoolean(3, category.isArchived());

                    return preparedStatement;
                },
                keyholder);
        final UUID generatedKey = (UUID) keyholder.getKeys().get("id");
        category.setId(generatedKey);

        return category;
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.INSTANCE);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        CategoryEntityRowMapper.INSTANCE,
                        id
                )
        );
    }
}