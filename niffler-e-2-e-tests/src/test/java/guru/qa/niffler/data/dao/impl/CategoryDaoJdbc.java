package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, category.getUsername());
                ps.setString(2, category.getName());
                ps.setBoolean(3, category.isArchived());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                category.setId(generatedKey);
                return category;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM category WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setName(rs.getString("name"));
                        ce.setArchived(rs.getBoolean("archived"));
                        return Optional.of(ce);
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
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM category WHERE username=? AND name=?"
            )) {
                ps.setObject(1, username);
                ps.setObject(2, categoryName);

                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setName(rs.getString("name"));
                        ce.setArchived(rs.getBoolean("archived"));
                        return Optional.of(
                                ce
                        );
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM category WHERE username=?"
            )) {
                ps.setObject(1, username);

                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        CategoryEntity ce = new CategoryEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setName(rs.getString("name"));
                        ce.setArchived(rs.getBoolean("archived"));
                        return List.of(
                                ce
                        );
                    }
                    return List.of();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteCategory(CategoryEntity category) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM category WHERE id=?"
            )) {
                ps.setObject(1, category.getId());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?")) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getUsername());
                ps.setBoolean(3, category.isArchived());
                ps.setObject(4, category.getId());

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Updating category failed, no rows affected.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }

        // Возвращаем обновленную сущность
        return category;

    }
}