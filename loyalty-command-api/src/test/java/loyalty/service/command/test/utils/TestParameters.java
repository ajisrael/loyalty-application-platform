package loyalty.service.command.test.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class TestParameters {

    public static Stream<Arguments> invalidStringParams() {
        return Stream.of(
                Arguments.arguments((String) null),
                Arguments.arguments(""),
                Arguments.arguments(" ")
        );
    }

    public static Stream<Arguments> invalidPointsParams() {
        return Stream.of(
                Arguments.arguments(-100),
                Arguments.arguments(0)
        );
    }
}
