package transfer.account.deposit

import com.google.inject.Inject
import com.google.inject.Singleton
import transfer.account.AccountRepository
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class DepositServlet : HttpServlet() {

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        super.doPost(req, resp)
    }
}