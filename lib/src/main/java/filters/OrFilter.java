package filters;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OrFilter implements Filter {

    private final List<Filter> filters;

    @Override
    public String getFilterQuery() {
        StringBuilder builder = new StringBuilder();
        final String OR = " OR ";
        builder.append(" (");

        filters.forEach(filter -> builder.append(filter.getFilterQuery()).append(OR));
        builder.delete(builder.length() - OR.length(), builder.length());

        builder.append(") ");
        return builder.toString();
    }

    @Override
    public Map<String, ?> getNamedParam() {
        Map<String, Object> params = new HashMap<>();
        filters.forEach(filter -> params.putAll(filter.getNamedParam()));
        return params;
    }
}
