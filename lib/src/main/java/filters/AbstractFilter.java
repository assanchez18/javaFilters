package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public abstract class AbstractFilter {

    protected final TableName table;
    protected final ColumnName column;
    protected final String paramName;
}
