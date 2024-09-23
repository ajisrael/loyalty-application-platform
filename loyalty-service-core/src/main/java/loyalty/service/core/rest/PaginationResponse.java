package loyalty.service.core.rest;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class PaginationResponse<T>
{
    private long totalItems;
    private List<T> content;
}
