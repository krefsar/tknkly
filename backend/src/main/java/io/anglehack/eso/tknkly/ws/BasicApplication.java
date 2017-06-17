package io.anglehack.eso.tknkly.ws;

import io.anglehack.eso.tknkly.models.Person;
import io.anglehack.eso.tknkly.ws.db.PersonDAO;
import io.anglehack.eso.tknkly.ws.resources.BasicResource;
import io.anglehack.eso.tknkly.ws.resources.PeopleResource;
import io.anglehack.eso.tknkly.ws.resources.ReadSerialResource;
import io.anglehack.eso.tknkly.ws.tasks.EchoTask;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class BasicApplication extends Application<BasicConfiguration> {
    public static void main(String[] args) throws Exception {
        new BasicApplication().run(args);
    }

    private final HibernateBundle<BasicConfiguration> hibernateBundle =
        new HibernateBundle<BasicConfiguration>(Person.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(BasicConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };

    @Override
    public String getName() {
        return "tknk-ly";
    }

    @Override
    public void initialize(Bootstrap<BasicConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<BasicConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BasicConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(BasicConfiguration configuration, Environment environment) {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());

        environment.admin().addTask(new EchoTask());
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new BasicResource());
        environment.jersey().register(new ReadSerialResource());
    }
}
