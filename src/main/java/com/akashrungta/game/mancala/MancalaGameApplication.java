package com.akashrungta.game.mancala;

import com.akashrungta.game.mancala.api.GameApi;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

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
    bootstrap.addBundle(
        new SwaggerBundle<MancalaGameConfiguration>() {
          @Override
          protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
              MancalaGameConfiguration configuration) {
            return configuration.swaggerBundleConfiguration;
          }
        });
  }

  @Override
  public void run(final MancalaGameConfiguration configuration, final Environment environment) {
    environment.jersey().register(new GameApi());
    environment
        .healthChecks()
        .register(
            "healthcheck",
            new HealthCheck() {
              @Override
              protected HealthCheck.Result check() throws Exception {
                return Result.healthy();
              }
            });
  }
}
