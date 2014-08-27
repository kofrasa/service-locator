package net.kofrasa.services.test;

import net.kofrasa.services.ServiceFactory;
import net.kofrasa.services.ServiceLocator;
import org.junit.Test;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Tests for the {@code ServiceLocator} class
 *
 * @author: francis
 */
public class ServiceLocatorTest {

    public void setUp() {
        Properties props = new Properties();
        props.setProperty("random", "java.util.Random");
        ServiceLocator.instance.init(props);
        ServiceLocator.instance.register("date", new Date());
    }

    @Test
    public void testRegisterValues() {

    }
}


class RandomFactory implements ServiceFactory<Random> {
    @Override
    public Random newInstance() {
        return new Random();
    }
}
