package loyalty.service.command.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class CommandRestController {

    @Autowired
    private CommandGateway commandGateway;

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public String hello() {
        return "hello";
    }
}


