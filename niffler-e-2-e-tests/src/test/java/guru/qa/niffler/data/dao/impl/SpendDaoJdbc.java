package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setDate(2, spend.getSpendDate());
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        SpendEntity se = new SpendEntity();
                        se.setId(rs.getObject("id", UUID.class));
                        se.setUsername(rs.getString("username"));
                        se.setSpendDate(rs.getDate("spend_date"));
                        se.setCurrency(rs.getObject("currency", guru.qa.niffler.model.CurrencyValues.class));
                        se.setAmount(rs.getDouble("amount"));
                        se.setDescription(rs.getString("description"));
                        UUID categoryId = rs.getObject("category_id", UUID.class);
                        CategoryEntity categoryEntity = new CategoryEntity();
                        categoryEntity.setId(categoryId);
                        se.setCategory(categoryEntity);
                        return Optional.of(
                                se
                        );
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE username =?"
            )) {

                ps.setObject(1, username);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        SpendEntity se = new SpendEntity();
                        se.setId(rs.getObject("id", UUID.class));
                        se.setUsername(rs.getString("username"));
                        se.setSpendDate(rs.getDate("spend_date"));
                        se.setCurrency(rs.getObject("currency", guru.qa.niffler.model.CurrencyValues.class));
                        se.setAmount(rs.getDouble("amount"));
                        se.setDescription(rs.getString("description"));
                        UUID categoryId = rs.getObject("category_id", UUID.class);
                        CategoryEntity categoryEntity = new CategoryEntity();
                        categoryEntity.setId(categoryId);
                        se.setCategory(categoryEntity);
                        return List.of(
                                se
                        );
                    }
                    return List.of(
                    );
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM spend WHERE id=?")) {
                ps.setObject(1, spend.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
