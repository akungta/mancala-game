package com.akashrungta.game.mancala;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MancalaGameApplication extends Application<MancalaGameConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MancalaGameApplication().run(args);
    }

    @Override
    public String getName() {
        return "MancalaGame";
    }

    @Override
    public void initialize(final Bootstrap<MancalaGameConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final MancalaGameConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
