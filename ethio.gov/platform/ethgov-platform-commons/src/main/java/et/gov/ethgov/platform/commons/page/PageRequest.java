package et.gov.ethgov.platform.commons.page;

import java.util.List;
import java.util.Objects;

/**
 * Immutable page request with hard upper bounds suitable for public government APIs.
 */
public final class PageRequest {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private final int page;
    private final int size;

    private PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public static PageRequest of(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
        }
        return new PageRequest(page, size);
    }

    public static PageRequest firstPage() {
        return of(0, DEFAULT_SIZE);
    }

    public int page() {
        return page;
    }

    public int size() {
        return size;
    }

    public long offset() {
        return (long) page * size;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PageRequest that)) {
            return false;
        }
        return page == that.page && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }
}
