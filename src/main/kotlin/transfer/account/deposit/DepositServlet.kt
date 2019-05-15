package transfer.account.deposit

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton
import transfer.account.AccountRepository
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Singleton
class DepositServlet : HttpServlet() {

    private val gson = Gson()

    @Inject
    lateinit var accountRepository: AccountRepository

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val deposit = gson.fromJson<Deposit>(req?.reader, Deposit::class.java)
        if (deposit == null) {
            resp?.sendError(400, "Missing body")
            return
        }
        if (deposit.amount <= 0) {
            resp?.sendError(422, "Amount must be positive")
            return
        }
        try {
           accountRepository.deposit(deposit)
        } catch (e: Exception) {
            resp?.sendError(500, e.message)
            return
        }
    }

    data class Deposit(val accountId: String, val amount: Long)
}