package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Type 'T' refers to the unique key identifier for each of your filters.
 * Ideally, this will be one enum class 'XyzFilterField' where Xyz is replaced by the name of the repository you aim to filter
 * E.g.:
 * Entity           -> Product (id, name, cost)
 * Repository       -> ProductRepository
 * XyzFilterField   -> ProductFilterField { ID, NAME, COST} // I'll have one filter per each field
 * <p>
 * <p>
 * To use this filters you need to:
 * - Extend Filters class with the required T
 * - Extend the FiltersBuilder class to suit your needs
 * - Implement as many filters you need for your use case. You can use the provided abstract filters to simplify the most common use cases
 */

@ToString
@Slf4j
public abstract class Filters<T> {

    private final OrderFilter<T> orderBy;
    private final PagedFilter pagination;
    private final TableName tableName;

    private final boolean isValid;
    private final String whereClause;
    private MapSqlParameterSource params;

    //private QueryBuilder queryBuilder;

    // Anadimos table name a la que pertenecen los filtros y luego montamos todas las queries en el queryBuilder.
    // Como solucionar los join??
    // how to create remote repository??
    //    this.queryBuilder = new QueryBuilder();
    protected Filters(Map<T, Filter> filters, OrderFilter<T> orderBy, PagedFilter pagination, TableName tableName) {
        this.tableName = tableName;
        this.orderBy = orderBy;
        this.pagination = pagination;

        this.isValid = !filters.values().isEmpty()
            && filters.values().stream().allMatch(Objects::nonNull);

        this.whereClause = buildWhereClauseAndParams(filters);

        if (!this.isValid) {
            log.warn("Filters: {} are built with invalid values!", filters);
        }
    }

    /**
     * @return Sql params belonging to the query
     */
    public MapSqlParameterSource getParams() {
        return this.params;
    }


    /**
     * @param countColumn The column we want to count
     * @return select query count with output column name as "count"
     */
    protected String getSelectCount(ColumnName countColumn) {
        return "SELECT COUNT(DISTINCT %s.%s) as count ".formatted(tableName.get(), countColumn.get()) + buildFromClause(tableName) + whereClause;
    }

    /**
     * @return select query count with output column name as "count"
     */
    protected String getSelectCount() {
        return "SELECT COUNT(*) as count " + buildFromClause(tableName) + whereClause;
    }

    /**
     * @return select all query with all the filters defined
     */
    protected String getSelectAll() {
        return "SELECT %s.* ".formatted(tableName.get()) + buildFromClause(tableName) + whereClause + builderOrderByClause() + builderPaginationClause();
    }

    /**
     * If you need join tables, override buildFromClaus
     *
     * @return from query section
     */
    protected String buildFromClause(TableName tableName) {
        return " FROM %s ".formatted(tableName.get());
    }

    /**
     * Validates if all the filters are correctly setup and creates where clause with SQL Parameters.
     * Includes all parameters into the query
     * <p>
     * In case there is any wrong filter setup, includes a failing where clause.
     * No value will be retrieved from the database in this case without extra checks.
     *
     * @return Where query section
     */
    private String buildWhereClauseAndParams(Map<T, Filter> filters) {
        if (!isValid) {
            return " WHERE FALSE ";
        }
        this.params = new MapSqlParameterSource();
        StringBuilder sqlBuilder = new StringBuilder(" WHERE ");
        final String AND = " AND ";
        filters.values().forEach(filter -> {
            sqlBuilder.append("(").append(filter.getFilterQuery()).append(")").append(AND);
            this.params.addValues(filter.getNamedParam());
        });
        sqlBuilder.delete(sqlBuilder.length() - AND.length(), sqlBuilder.length());
        return sqlBuilder.toString();
    }

    private String builderOrderByClause() {
        String query = "";
        if (null != this.orderBy) {
            query = this.orderBy.getFilterQuery();
        }
        return query;
    }

    private String builderPaginationClause() {
        String query = "";
        if (null != this.pagination) {
            query = this.pagination.getFilterQuery();
        }
        return query;
    }


    public static abstract class FiltersBuilder<T> {
        protected final TableName tableName;
        protected Map<T, Filter> filters;
        protected PagedFilter pagination;
        protected OrderFilter<T> orderBy;

        protected FiltersBuilder(TableName tableName) {
            this.filters = new HashMap<>();
            this.orderBy = defaultOrderFilter();
            this.pagination = null;
            this.tableName = tableName;
        }

        public abstract Filters<T> build();

        public abstract FiltersBuilder<T> orderBy(T field, OrderFilter.OrderType type);

        protected abstract OrderFilter<T> defaultOrderFilter();

        public FiltersBuilder<T> paginated(int pageSize, int pageNumber) {
            this.pagination = new PagedFilter(pageSize, pageNumber);
            return this;
        }

        protected void nullFilterValue(T filterField) {
            this.filters.put(filterField, null);
            log.debug("{} added to filters with NULL value", filterField);
        }

        protected void addFilter(Collection<?> value, T field, Filter filter) {
            if (isInvalid(value)) {
                nullFilterValue(field);
            } else {
                this.filters.put(field, filter);
            }
        }

        protected <Z> void addFilter(Z value, T field, Filter filter) {
            if (isInvalid(value)) {
                nullFilterValue(field);
            } else {
                this.filters.put(field, filter);
            }
        }

        private <Z> boolean isInvalid(Collection<Z> values) {
            return CollectionUtils.isEmpty(values) || values.stream().allMatch(Objects::isNull);
        }

        private <Z> boolean isInvalid(Z value) {
            return null == value;
        }
    }
}
