package et.gov.ethgov.platform.commons.page;

import java.util.List;
import java.util.Objects;

/**
 * Immutable page of results.
 */
public final class PageResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    private PageResult(List<T> content, int page, int size, long totalElements) {
        this.content = List.copyOf(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public static <T> PageResult<T> of(List<T> content, PageRequest request, long totalElements) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(request, "request");
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be >= 0");
        }
        if (content.size() > request.size()) {
            throw new IllegalArgumentException("content size exceeds page size");
        }
        return new PageResult<>(content, request.page(), request.size(), totalElements);
    }

    public List<T> content() {
        return content;
    }

    public int page() {
        return page;
    }

    public int size() {
        return size;
    }

    public long totalElements() {
        return totalElements;
    }

    public int totalPages() {
        if (size == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / (double) size);
    }

    public boolean hasNext() {
        return page + 1 < totalPages();
    }
}
