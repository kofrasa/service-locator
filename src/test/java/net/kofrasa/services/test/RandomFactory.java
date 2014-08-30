package net.kofrasa.services.test;

import net.kofrasa.services.ServiceFactory;

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