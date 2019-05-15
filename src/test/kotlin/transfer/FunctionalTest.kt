package transfer

import com.google.inject.Guice.createInjector
import com.google.inject.servlet.GuiceFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import transfer.account.AccountServlet
import transfer.module.AppPersistenceModule
import transfer.module.AppServletModule
import java.util.*
import javax.servlet.DispatcherType

class FunctionalTest {

    companion object {

        @JvmStatic private val server = Server(8080)
        @JvmStatic lateinit var restClient: RestClient

        @JvmStatic
        @BeforeAll
        fun startServer() {
            val injector = createInjector(AppServletModule(), AppPersistenceModule())
            restClient = injector.getInstance(RestClient::class.java)
            val servletContextHandler = ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
            servletContextHandler.addFilter(GuiceFilter::class.java, "/*", EnumSet.allOf(DispatcherType::class.java))
            servletContextHandler.addServlet(DefaultServlet::class.java, "/")
            server.start()
        }

        @JvmStatic
        @AfterAll
        fun stopServer() {
            server.stop()
        }
    }

    @Test
    fun test() {
        assertEquals(200, restClient.post("account", AccountServlet.AccountId("accountId")).responseCode)
    }
}