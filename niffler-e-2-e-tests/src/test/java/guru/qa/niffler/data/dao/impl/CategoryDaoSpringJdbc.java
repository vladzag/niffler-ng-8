package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    public final String url = Config.getInstance().spendJdbcUrl();


@Override
@Nonnull
public CategoryEntity create(@Nonnull CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
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
public void update(@Nonnull CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
                "UPDATE category SET " +
                        "name = ?, " +
                        "username = ?, " +
                        "archived = ? " +
                        "WHERE id = ?"
        );
        ps.setString(1, category.getName());
        ps.setString(2, category.getUsername());
        ps.setBoolean(3, category.isArchived());
        ps.setObject(4, category.getId());

        return ps;
    });
}

@Override
@Nonnull
public List<CategoryEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return jdbcTemplate.query(
            "SELECT * FROM category",
            CategoryEntityRowMapper.INSTANCE);
}

@Override
@Nonnull
public Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));

    try {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"category\" WHERE id = ?",
                        new Object[]{id},
                        CategoryEntityRowMapper.INSTANCE
                )
        );
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}

@Override
@Nonnull
public Optional<CategoryEntity> findCategoryByUsernameAndName(@Nonnull String username, @Nonnull String name) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        new Object[]{username, name},
                        CategoryEntityRowMapper.INSTANCE
                )
        );
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}

@Override
public void removeCategory(@Nonnull CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());
}

}