package kofrasa.services.test;

import kofrasa.services.ServiceFactory;

import java.util.Random;

/**
 * @author: francis
 */
public class RandomFactory implements ServiceFactory<Random> {

    @Override
    public Random newInstance() {
        return new Random();
    }
}