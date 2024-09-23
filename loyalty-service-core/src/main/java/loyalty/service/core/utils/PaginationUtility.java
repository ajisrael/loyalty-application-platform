package loyalty.service.core.utils;

import loyalty.service.core.rest.PaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class PaginationUtility {

	private PaginationUtility() {
		throw new IllegalStateException("Utility class");
	}

	public static <T> PaginationResponse<T> toPageResponse(final Page<T> page) {
		return PaginationResponse.<T> builder()
				.content(page.getContent())
				.totalItems(page.getTotalElements())
				.build();
	}

	public static PageRequest buildPageable(final int page, final int pageSize) {
		return PageRequest.of(page, pageSize);
	}
}
