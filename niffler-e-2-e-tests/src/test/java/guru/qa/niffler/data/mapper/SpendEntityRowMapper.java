package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendEntityRowMapper INSTANCE = new SpendEntityRowMapper();
    private SpendEntityRowMapper() {
    }

    @Override
    public SpendEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
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

        return spendEntity;
    }
}
