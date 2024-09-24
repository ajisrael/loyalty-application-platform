package loyalty.service.command.commands;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractCommand {
    private String requestId;
}
