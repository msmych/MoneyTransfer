package transfer.account

import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class AccountServlet : HttpServlet() {

    private val logger = LoggerFactory.getLogger(AccountServlet::class.java)

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        logger.info(req?.requestURL.toString())
    }
}