package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.templates.Connections.holder;


public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private final String url = Config.getInstance().spendJdbcUrl();


    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ?"
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.executeUpdate();

            return spend;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement preparedStatement = holder(url).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            List<SpendEntity> spendEntities = new ArrayList<>();

            preparedStatement.execute();

            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                while (resultSet.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(resultSet.getObject("id", UUID.class));
                    spendEntity.setUsername(resultSet.getString("username"));
                    spendEntity.setSpendDate(resultSet.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    spendEntity.setAmount(resultSet.getDouble("amount"));
                    spendEntity.setDescription(resultSet.getString("description"));

                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(resultSet.getObject("category_id", UUID.class));
                    spendEntity.setCategory(categoryEntity);

                    spendEntities.add(spendEntity);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return spendEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se =
                            SpendEntityRowMapper.INSTANCE.mapRow(rs, rs.getRow());
                    return Optional.ofNullable(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? AND description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se =
                            SpendEntityRowMapper.INSTANCE.mapRow(rs, rs.getRow());
                    return Optional.ofNullable(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?")) {
            ps.setObject(1, spend.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
