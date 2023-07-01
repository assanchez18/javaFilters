package filters;

import filters.tinyTypes.ColumnName;
import filters.tinyTypes.TableName;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RegexFilter implements Filter {

    private final TableName table;
    private final List<ColumnName> columns;
    private final String paramName;
    private final String value;


    @Override
    public String getFilterQuery() {
        StringBuilder builder = new StringBuilder();
        if (!value.isBlank()) {
            final String OR = " OR ";
            builder.append(" (");
            columns.forEach(column -> {
                builder.append(String.format("%s.%s ILIKE :%s ", table.get(), column.get(), value)).append(OR);
            });
            builder.delete(builder.length() - OR.length(), builder.length());
        }
        builder.append(") ");
        return builder.toString();
    }

    @Override
    public Map<String, ?> getNamedParam() {
        return Map.of(paramName, value);
    }

}
