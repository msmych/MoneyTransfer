package transfer.module

import com.google.inject.servlet.ServletModule
import transfer.account.AccountServlet

class AppServletModule : ServletModule() {

    override fun configureServlets() {
        serve("/account").with(AccountServlet::class.java)
    }
}
