package transfer.account

import com.google.inject.Inject
import com.google.inject.Singleton
import transfer.account.deposit.DepositServlet.Deposit
import transfer.persistence.EntityManagerHolder
import java.util.function.Consumer

@Singleton
class AccountRepository @Inject constructor(private val entityManagerHolder: EntityManagerHolder) {

    fun getById(id: String): Account? {
        return entityManagerHolder.getById(id, Account::class.java)
    }

    fun create(id: String) {
        entityManagerHolder.executeInTransaction(
            Consumer {
                if (it.find(Account::class.java, id) == null)
                    it.persist(Account(id, 0))
            })
    }

    fun deposit(deposit: Deposit) {
        entityManagerHolder.executeInTransaction(
            Consumer {
                val account = it.find(Account::class.java, deposit.accountId)
                    ?: throw IllegalArgumentException("Account ${deposit.accountId} not exists")
                it.merge(Account(account.id, account.balance + deposit.amount))
            })
    }
}