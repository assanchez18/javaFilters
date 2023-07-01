package filters;

import filters.tinyTypes.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public abstract class OrderFilter<T> {

    protected final T field;
    private final OrderType type;
    private final TableName table;

    public String getFilterQuery() {
        return String.format(" ORDER BY %s.%s %s", table.get(), getFieldName(), type);
    }

    public abstract String getFieldName();

    public enum OrderType {
        ASC, DESC
    }
}
