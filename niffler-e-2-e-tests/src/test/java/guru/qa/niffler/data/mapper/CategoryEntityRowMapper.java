package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

    public CategoryEntityRowMapper() {
    }

    @Override
    public CategoryEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setId(resultSet.getObject("id", UUID.class));
        categoryEntity.setName(resultSet.getString("name"));
        categoryEntity.setUsername(resultSet.getString("username"));
        categoryEntity.setArchived(resultSet.getBoolean("archived"));

        return categoryEntity;
    }
}
