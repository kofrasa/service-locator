package net.kofrasa.services.test;

import net.kofrasa.services.ServiceLocator;
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
        ServiceLocator.INSTANCE.init(props);
        ServiceLocator.INSTANCE.register("date", new Date());
    }

    @Test
    public void testRegisterValues() {
        Date first = ServiceLocator.INSTANCE.value("date");
        Date second = ServiceLocator.INSTANCE.value("date");
        assertEquals("Retrieve the same registered values", first, second);
    }

    @Test
    public void testNewInstances() {
        Random first = ServiceLocator.INSTANCE.create("random");
        Random second = ServiceLocator.INSTANCE.create("random");
        assertNotSame("Create new instances of objects", first, second);
    }

    @Test
    public void testSingletons() {
        Random first = ServiceLocator.INSTANCE.singleton("random");
        Random second = ServiceLocator.INSTANCE.singleton("random");
        assertEquals("Create and retrieve a singleton INSTANCE", first, second);
    }

    @Test
    public void testServiceFactorySingleton() {
        Random first = ServiceLocator.INSTANCE.singleton("randomFactory");
        Random second = ServiceLocator.INSTANCE.singleton("randomFactory");
        assertEquals("Create and retrieve singleton instances with factory", first, second);
    }

    @Test
    public void testServiceFactoryInstances() {
        Random first = ServiceLocator.INSTANCE.create("randomFactory");
        Random second = ServiceLocator.INSTANCE.create("randomFactory");
        assertNotSame("Create and retrieve new instances with factory", first, second);
    }
}
