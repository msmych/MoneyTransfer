package transfer

import com.google.inject.Guice.createInjector
import com.google.inject.servlet.GuiceFilter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import transfer.account.Account
import transfer.account.AccountId
import transfer.account.deposit.Deposit
import transfer.account.transfer.Transfer
import transfer.account.withdraw.Withdrawal
import transfer.module.AppPersistenceModule
import transfer.module.AppServletModule
import java.net.HttpURLConnection
import java.util.*
import javax.servlet.DispatcherType

internal class FunctionalTest {

    companion object {

        @JvmStatic private val server = Server(8080)
        @JvmStatic lateinit var restClient: RestClient

        @JvmStatic
        @BeforeAll
        fun startServer() {
            val injector = createInjector(AppServletModule(), AppPersistenceModule())
            restClient = injector.getInstance(RestClient::class.java)
            val servletContextHandler = ServletContextHandler(server, "/", SESSIONS)
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
        testAccountCreation()
        testDeposit()
        testWithdrawal()
        testTransfer()
        testAccountDeletion()
    }

    private fun testAccountCreation() {
        assertNull(restClient.get("account?id=Billie", Account::class.java))
        createAccount("Billie")
        assertEquals(0, restClient.get("account?id=Billie", Account::class.java)?.balance)
        createAccount("Alice")
        assertEquals(0, restClient.get("account?id=Alice", Account::class.java)?.balance)
    }

    private fun testDeposit() {
        deposit("Billie", 1_000_000)
        assertEquals(1_000_000, restClient.get("account?id=Billie", Account::class.java)?.balance)
        deposit("Alice", 700_000)
        assertEquals(700_000, restClient.get("account?id=Alice", Account::class.java)?.balance)
        assertResponseCode(422, restClient.post("account/deposit", Deposit("Alice", -1_000_000)))
        assertResponseCode(500, restClient.post("account/deposit", Deposit("Charlie", 1_000_000)))
    }

    private fun testWithdrawal() {
        withdraw("Billie", 800_000)
        assertEquals(200_000, restClient.get("account?id=Billie", Account::class.java)?.balance)
        assertResponseCode(422, restClient.post("account/withdraw", Withdrawal("Alice", -100_000)))
        assertResponseCode(500, restClient.post("account/withdraw", Withdrawal("Charlie", 1_000_000)))
        assertResponseCode(500, restClient.post("account/withdraw", Withdrawal("Alice", 1_000_000)))
    }

    private fun testTransfer() {
        transfer("Alice", "Billie", 600_000)
        assertEquals(800_000, restClient.get("account?id=Billie", Account::class.java)?.balance)
        assertEquals(100_000, restClient.get("account?id=Alice", Account::class.java)?.balance)
        transfer("Billie", "Alice", 100_000)
        assertEquals(700_000, restClient.get("account?id=Billie", Account::class.java)?.balance)
        assertEquals(200_000, restClient.get("account?id=Alice", Account::class.java)?.balance)
        assertResponseCode(422, restClient.post("account/transfer", Transfer("Billie", "Billie", 100_000)))
        assertResponseCode(422, restClient.post("account/transfer", Transfer("Billie", "Alice", -100_000)))
        assertResponseCode(500, restClient.post("account/transfer", Transfer("Charlie", "Alice", 100_000)))
        assertResponseCode(500, restClient.post("account/transfer", Transfer("Billie", "Charlie", 100_000)))
        assertResponseCode(500, restClient.post("account/transfer", Transfer("Billie", "Alice", 1_000_000)))
    }

    private fun testAccountDeletion() {
        deleteAccount("Billie")
        assertNull(restClient.get("account?id=Billie", Account::class.java))
        assertResponseCode(500, restClient.delete("account?id=Charlie"))
        deleteAccount("Alice")
        assertNull(restClient.get("account?id=Alice", Account::class.java))
    }

    private fun createAccount(id: String) {
        val http = restClient.post("account", AccountId(id))
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun deposit(accountId: String, amount: Long) {
        val http = restClient.post("account/deposit", Deposit(accountId, amount))
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun withdraw(accountId: String, amount: Long) {
        val http = restClient.post("account/withdraw", Withdrawal(accountId, amount))
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun transfer(sourceId: String, targetId: String, amount: Long) {
        val http = restClient.post("account/transfer", Transfer(sourceId, targetId, amount))
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun deleteAccount(id: String) {
        val http = restClient.delete("account?id=$id")
        http.connect()
        assertEquals(200, http.responseCode)
    }

    private fun assertResponseCode(expectedCode: Int, http: HttpURLConnection) {
        http.connect()
        assertEquals(expectedCode, http.responseCode)
    }
}