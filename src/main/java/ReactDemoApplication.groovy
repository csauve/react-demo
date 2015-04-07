import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import resources.UserAccountResource

class ReactDemoApplication extends Application<AppConfig> {
  @Override
  void initialize(Bootstrap<AppConfig> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/app", "index.html"))
  }

  @Override
  void run(AppConfig appConfig, Environment environment) throws Exception {
    environment.jersey().register(new UserAccountResource())
  }

  public static void main(String[] args) {
    new ReactDemoApplication().run(args)
  }
}
