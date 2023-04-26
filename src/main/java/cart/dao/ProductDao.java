package cart.dao;

import cart.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Product> rowMapper = (resultSet, rowNum) -> new Product(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("image"),
            resultSet.getLong("price")
    );

    public ProductDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("product")
                .usingGeneratedKeyColumns("id");
    }

    public Optional<Long> saveAndGetId(final Product product) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(product);
        return Optional.of(jdbcInsert.executeAndReturnKey(params).longValue());
    }

    public List<Product> findAll() {
        final String sql = "select * from product";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void update(final Product product) {
        final String sql = "update product set name = ?, image = ?, price = ? where id = ?";
        jdbcTemplate.update(sql, product.getName(), product.getImage(), product.getPrice(), product.getId());
    }
}