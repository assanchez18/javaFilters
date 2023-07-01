package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;

@ToString(callSuper = true)
public abstract class CollectionFilter<T> extends AbstractFilter implements Filter {

    private final Collection<T> values;

    protected CollectionFilter(TableName table, ColumnName column, String paramName, Collection<T> values) {
        super(table, column, paramName);
        this.values = values;
    }

    @Override
    public String getFilterQuery() {
        return String.format("(%s.%s in (:%s))", table.get(), column.get(), paramName);
    }

    @Override
    public Map<String, ?> getNamedParam() {
        return Map.of(paramName, values.stream().map(T::toString).toList());
    }

}
