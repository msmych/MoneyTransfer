package transfer.account.transfer

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import transfer.account.AccountRepository
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class TransferServlet : HttpServlet() {

    private val logger = LoggerFactory.getLogger(TransferServlet::class.java)

    private val gson = Gson()

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val transfer = gson.fromJson<Transfer>(req?.reader, Transfer::class.java)
        if (transfer == null) {
            resp?.sendError(400, "Missing body")
            logger.error("Missing body")
            return
        }
        if (transfer.sourceId == null) {
            resp?.sendError(400, "$transfer: missing sourceId")
            logger.error("$transfer: missing sourceId")
            return
        }
        if (transfer.targetId == null) {
            resp?.sendError(400, "$transfer: missing targetId")
            logger.error("$transfer: missing targetId")
            return
        }
        if (transfer.sourceId == transfer.targetId) {
            resp?.sendError(422, "$transfer: target must not be the same as source")
            logger.error("$transfer: target must not be the same as source")
            return
        }
        if (transfer.amount <= 0) {
            resp?.sendError(422, "$transfer: amount must be positive")
            logger.error("$transfer: amount must be positive")
            return
        }
        try {
            accountRepository.transfer(transfer)
        } catch (e: Exception) {
            resp?.sendError(500, e.message)
            return
        }
        logger.info("${transfer.sourceId} → ${transfer.amount} → ${transfer.targetId}")
    }

}
