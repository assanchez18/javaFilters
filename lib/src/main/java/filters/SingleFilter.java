package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
public abstract class SingleFilter<T> extends AbstractFilter implements Filter {

    private final T value;

    protected SingleFilter(TableName table, ColumnName column, String paramName, T value) {
        super(table, column, paramName);
        this.value = value;
    }

    @Override
    public String getFilterQuery() {
        return String.format("(%s.%s = :%s)", table.get(), column.get(), paramName);
    }

    @Override
    public Map<String, ?> getNamedParam() {
        return Map.of(paramName, value.toString());
    }

}
