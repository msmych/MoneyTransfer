package transfer.module

import com.google.inject.servlet.ServletModule
import transfer.account.AccountServlet
import transfer.account.deposit.DepositServlet

class AppServletModule : ServletModule() {

    override fun configureServlets() {
        serve("/account").with(AccountServlet::class.java)
        serve("/account/deposit").with(DepositServlet::class.java)
    }
}
