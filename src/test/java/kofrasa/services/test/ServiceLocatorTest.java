package kofrasa.services.test;

import kofrasa.services.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Tests for the {@code ServiceLocator} class
 *
 * @author: francis
 */
public class ServiceLocatorTest {

    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty("random", "java.util.Random");
        props.setProperty("randomFactory", "kofrasa.services.test.RandomFactory");
        ServiceLocator.instance.init(props);
        ServiceLocator.instance.register("date", new Date());
    }

    @Test
    public void testRegisterValues() {
        Date first = ServiceLocator.instance.value("date");
        Date second = ServiceLocator.instance.value("date");
        assertEquals(first, second);
    }

    @Test
    public void testNewInstances() {
        Random first = ServiceLocator.instance.create("random");
        Random second = ServiceLocator.instance.create("random");
        assertNotSame(first, second);
    }

    @Test
    public void testSingletons() {
        Random first = ServiceLocator.instance.singleton("random");
        Random second = ServiceLocator.instance.singleton("random");
        assertEquals(first, second);
    }

    @Test
    public void testServiceFactorySingleton() {
        Random first = ServiceLocator.instance.singleton("randomFactory");
        Random second = ServiceLocator.instance.singleton("randomFactory");
        assertEquals(first, second);
    }

    @Test
    public void testServiceFactoryInstances() {
        Random first = ServiceLocator.instance.create("randomFactory");
        Random second = ServiceLocator.instance.create("randomFactory");
        assertNotSame(first, second);
    }
}
