package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;

import java.util.HashMap;
import java.util.Map;

public class RangeFilter<T> extends AbstractFilter implements Filter {
    private final T from;
    private final T to;
    private final String COLUMN_FROM;
    private final String COLUMN_TO;

    public RangeFilter(TableName table, ColumnName column, String paramName, T from, T to) {
        super(table, column, paramName);
        this.COLUMN_FROM = paramName + "From";
        this.COLUMN_TO = paramName + "To";
        this.from = from;
        this.to = to;
    }

    @Override
    public String getFilterQuery() {
        String query;
        final String fieldName = table.get() + "." + column;
        if (from != null && to != null) {
            query = String.format("%s BETWEEN :%s AND :%s", fieldName, COLUMN_FROM, COLUMN_TO);
        } else if (from != null) {
            query = String.format("%s >= :%s", fieldName, COLUMN_FROM);
        } else if (to != null) {
            query = String.format("%s < :%s", fieldName, COLUMN_TO);
        } else {
            query = String.format("%s IS NULL", fieldName);
        }
        return query;
    }

    @Override
    public Map<String, ?> getNamedParam() {
        Map<String, ?> params = new HashMap<>();
        if (from != null) {
            params = Map.of(COLUMN_FROM, from);
        }
        if (to != null) {
            params = Map.of(COLUMN_TO, to);
        }
        return params;
    }
}
