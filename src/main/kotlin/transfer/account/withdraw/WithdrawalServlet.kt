package transfer.account.withdraw

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import transfer.account.AccountRepository
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class WithdrawalServlet : HttpServlet() {

    private val logger = LoggerFactory.getLogger(WithdrawalServlet::class.java)

    private val gson = Gson()

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val withdrawal = gson.fromJson<Withdrawal>(req?.reader, Withdrawal::class.java)
        if (withdrawal == null) {
            resp?.sendError(400, "Missing body")
            logger.error("Missing body")
            return
        }
        if (withdrawal.accountId == null) {
            resp?.sendError(400, "$withdrawal: missing accountId")
            logger.error("$withdrawal: missing accountId")
            return
        }
        if (withdrawal.amount <= 0) {
            resp?.sendError(422, "$withdrawal: amount must be positive")
            logger.error("$withdrawal: amount must be positive")
            return
        }
        try {
            accountRepository.withdraw(withdrawal)
        } catch (e: Exception) {
            resp?.sendError(500, e.message)
            return
        }
        logger.info("${withdrawal.accountId} -${withdrawal.amount}")
    }

}
