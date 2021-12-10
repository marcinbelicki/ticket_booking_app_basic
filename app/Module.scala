import com.google.inject.AbstractModule
import start.ApplicationStart

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
  }
}
