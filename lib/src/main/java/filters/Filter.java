package filters;

import java.util.Map;

public interface Filter {

    String getFilterQuery();

    Map<String, ?> getNamedParam();
}
