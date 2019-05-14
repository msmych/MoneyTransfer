package transfer

import com.google.inject.Guice.createInjector
import com.google.inject.servlet.GuiceFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS
import transfer.module.AppPersistenceModule
import transfer.module.AppServletModule
import java.util.EnumSet.allOf
import javax.servlet.DispatcherType

fun main() {
    createInjector(AppServletModule(), AppPersistenceModule())
    val server = Server(8080)
    val servletContextHandler = ServletContextHandler(server, "/", SESSIONS)
    servletContextHandler.addFilter(GuiceFilter::class.java, "/*", allOf(DispatcherType::class.java))
    servletContextHandler.addServlet(DefaultServlet::class.java, "/")
    server.start()
    server.join()
}