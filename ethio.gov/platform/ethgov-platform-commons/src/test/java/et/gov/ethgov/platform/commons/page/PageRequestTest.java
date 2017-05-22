package et.gov.ethgov.platform.commons.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageRequestTest {

    @Test
    void computesOffsetAndBounds() {
        PageRequest request = PageRequest.of(2, 50);
        assertThat(request.offset()).isEqualTo(100);
        assertThat(PageRequest.firstPage().size()).isEqualTo(20);
    }

    @Test
    void rejectsInvalidSize() {
        assertThatThrownBy(() -> PageRequest.of(0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageRequest.of(0, 101)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageRequest.of(-1, 10)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pageResultTracksTotals() {
        PageResult<String> page = PageResult.of(List.of("a", "b"), PageRequest.of(0, 2), 5);
        assertThat(page.totalPages()).isEqualTo(3);
        assertThat(page.hasNext()).isTrue();
        assertThatThrownBy(() -> PageResult.of(List.of("a", "b", "c"), PageRequest.of(0, 2), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
