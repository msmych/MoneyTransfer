package transfer.account.deposit

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import transfer.account.AccountRepository
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class DepositServlet : HttpServlet() {

    private val logger = LoggerFactory.getLogger(DepositServlet::class.java)

    private val gson = Gson()

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val deposit = gson.fromJson<Deposit>(req?.reader, Deposit::class.java)
        if (deposit == null) {
            resp?.sendError(400, "Missing body")
            logger.error("Missing body")
            return
        }
        if (deposit.accountId == null) {
            resp?.sendError(400, "$deposit: missing accountId")
            logger.error("$deposit: missing accountId")
            return
        }
        if (deposit.amount <= 0) {
            resp?.sendError(422, "$deposit: amount must be positive")
            logger.error("$deposit: amount must be positive")
            return
        }
        try {
           accountRepository.deposit(deposit)
        } catch (e: Exception) {
            resp?.sendError(500, e.message)
            return
        }
        logger.info("${deposit.accountId} +${deposit.amount}")
    }

}