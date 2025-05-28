package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private final String url = Config.getInstance().spendJdbcUrl();


    @Override
@Nonnull
public SpendEntity create(@Nonnull SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        preparedStatement.setString(1, spend.getUsername());
        preparedStatement.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
        preparedStatement.setString(3, spend.getCurrency().name());
        preparedStatement.setDouble(4, spend.getAmount());
        preparedStatement.setString(5, spend.getDescription());
        preparedStatement.setObject(6, spend.getCategory().getId());

        return preparedStatement;
    }, keyHolder);
    final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
}

@Override
@Nonnull
public SpendEntity update(@Nonnull SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? WHERE id = ?;"
        );
        ps.setString(1, spend.getUsername());
        ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        return ps;
    });

    return spend;
}

@Override
@Nonnull
public List<SpendEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));

    return jdbcTemplate.query(
            "SELECT * FROM spend",
            SpendEntityRowMapper.INSTANCE
    );
}

@Override
@Nonnull
public Optional<SpendEntity> findById(@Nonnull UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM spend WHERE id = ?",
                        SpendEntityRowMapper.INSTANCE,
                        id
                )
        );
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}

@Override
@Nonnull
public Optional<SpendEntity> findByUsernameAndSpendDescription(@Nonnull String username, @Nonnull String description) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM spend WHERE username = ? AND description = ?",
                        SpendEntityRowMapper.INSTANCE,
                        username,
                        description
                )
        );
    } catch (EmptyResultDataAccessException e) {
        return Optional.empty();
    }
}

@Override
public void remove(@Nonnull SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spend.getId());
}

}
