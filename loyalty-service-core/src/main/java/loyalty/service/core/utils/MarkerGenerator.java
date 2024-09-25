package loyalty.service.core.utils;

import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class MarkerGenerator {

    private MarkerGenerator() {
        throw new IllegalStateException("utility class");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkerGenerator.class);

    public static Marker generateMarker(Object object) {
        Marker marker = null;

        try {
            // Traverse the class hierarchy, starting from the given object's class
            Class<?> currentClass = object.getClass();

            while (currentClass != null && !currentClass.equals(Object.class)) {
                // Use JavaBeans Introspector to get property descriptors for the current class
                for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(currentClass, Object.class).getPropertyDescriptors()) {
                    Method getter = propertyDescriptor.getReadMethod(); // Get the getter method
                    if (getter != null) {
                        Object value = getter.invoke(object); // Invoke the getter method to get the value
                        String fieldName = propertyDescriptor.getName(); // Get the field name

                        if (marker == null) {
                            marker = Markers.append(fieldName, value);
                        } else {
                            marker.add(Markers.append(fieldName, value));
                        }
                    }
                }
                currentClass = currentClass.getSuperclass(); // Move to the superclass
            }
        } catch (Exception e) {
            LOGGER.error(
                    String.format("Failed to generate marker for %s. Reason: %s",
                            object.getClass().getSimpleName(),  // Correct class name
                            e.getLocalizedMessage()
                    )
            );
        }

        return marker;
    }
}
