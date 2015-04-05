import io.dropwizard.Application
import io.dropwizard.setup.Environment
import resources.UserAccountResource

class ReactDemoApplication extends Application<AppConfig> {
  @Override
  void run(AppConfig appConfig, Environment environment) throws Exception {
    environment.jersey().register(new UserAccountResource())
  }

  public static void main(String[] args) {
    new ReactDemoApplication().run(args)
  }
}
