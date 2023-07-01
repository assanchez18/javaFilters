package filters;

public record PagedFilter(int pageSize, int pageNumber) {

    public PagedFilter(int pageSize, int pageNumber) {
        this.pageSize = isNegative(pageSize) ? 0 : pageSize;
        this.pageNumber = isNegative(pageNumber) ? 0 : pageNumber;
    }

    public String getFilterQuery() {
        return String.format(" LIMIT %d OFFSET %d", pageSize, pageSize * pageNumber);
    }

    private boolean isNegative(int value) {
        return value < 0;
    }
}
