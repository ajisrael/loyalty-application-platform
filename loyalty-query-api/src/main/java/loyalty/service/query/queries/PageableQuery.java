package loyalty.service.query.queries;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

@Getter
@SuperBuilder
public class PageableQuery extends AbstractQuery {

    private Pageable pageable;
}
