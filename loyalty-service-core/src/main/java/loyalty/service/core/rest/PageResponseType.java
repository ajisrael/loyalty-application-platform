package loyalty.service.core.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.common.ReflectionUtils;
import org.axonframework.messaging.responsetypes.AbstractResponseType;
import org.springframework.data.domain.Page;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

public class PageResponseType<R> extends AbstractResponseType<Page<R>> {

    @JsonCreator
    @ConstructorProperties({"expectedResponseType"})
    public PageResponseType(@JsonProperty("expectedResponseType") Class<R> expectedResponseType) {
        super(expectedResponseType);
    }

    @Override
    public boolean matches(Type responseType) {
        Type unwrapped = ReflectionUtils.unwrapIfType(responseType, Future.class, Page.class);
        return isGenericAssignableFrom(unwrapped) || isAssignableFrom(unwrapped);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class responseMessagePayloadType() {
        return Page.class;
    }
}
