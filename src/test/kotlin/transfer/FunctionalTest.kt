package transfer

import com.google.inject.Guice.createInjector
import com.google.inject.servlet.GuiceFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import transfer.account.Account
import transfer.account.AccountId
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
        assertNull(restClient.get("account?id=Bill", Account::class.java))
        createAccount("Billie")
        assertAccountEquals("Billie", 0, restClient.get("account?id=Billie", Account::class.java))
        createAccount("Alice")
        assertAccountEquals("Alice", 0, restClient.get("account?id=Alice", Account::class.java))
    }

    private fun createAccount(id: String) {
        val http = restClient.post("account", AccountId(id))
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun assertAccountEquals(id: String, balance: Long, account: Account?) {
        assertNotNull(account)
        assertEquals(id, account?.id)
        assertEquals(balance, account?.balance)
    }
}