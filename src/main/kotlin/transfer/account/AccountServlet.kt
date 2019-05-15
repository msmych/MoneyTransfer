package transfer.account

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class AccountServlet : HttpServlet() {

    private val logger = LoggerFactory.getLogger(AccountServlet::class.java)

    private val gson = Gson()

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val id = req?.getParameter("id") ?: return
        resp?.contentType = "application/json"
        resp?.writer?.write(gson.toJson(accountRepository.getById(id)))
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val id = gson.fromJson<AccountId>(req?.reader, AccountId::class.java)?.id ?: return
        accountRepository.create(id)
        logger.info("Created account $id")
    }

    data class AccountId(val id: String)

    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val id = req?.getParameter("id")
        if (id == null) {
            resp?.sendError(400, "Missing id parameter")
            return
        }
        try {
            accountRepository.remove(id)
        } catch (e: Exception) {
            resp?.sendError(500, e.message)
            return
        }
        logger.info("Removed account $id")
    }
}