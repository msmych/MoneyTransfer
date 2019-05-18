package transfer.account

import com.google.inject.Inject
import com.google.inject.Singleton
import transfer.account.deposit.Deposit
import transfer.account.transfer.Transfer
import transfer.account.withdraw.Withdrawal
import transfer.persistence.TransactionManager
import java.util.function.Consumer

@Singleton
class AccountRepository @Inject constructor(private val transactionManager: TransactionManager) {

    fun getById(id: String): Account? {
        return transactionManager.getById(id, Account::class.java)
    }

    fun create(id: String) {
        transactionManager.executeInTransaction(
            Consumer { em ->
                if (em.find(Account::class.java, id) == null)
                    em.persist(Account(id, 0))
            })
    }

    fun remove(id: String) {
        transactionManager.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, id)
                        ?: throw IllegalArgumentException("Account $id not exists")
                em.remove(account)
            })
    }

    fun deposit(deposit: Deposit) {
        transactionManager.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, deposit.accountId)
                    ?: throw IllegalArgumentException("$deposit: account ${deposit.accountId} not exists")
                em.merge(Account(account.id, account.balance + deposit.amount))
            })
    }

    fun withdraw(withdrawal: Withdrawal) {
        transactionManager.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, withdrawal.accountId)
                    ?: throw IllegalArgumentException("$withdrawal: account ${withdrawal.accountId} not exists")
                if (account.balance < withdrawal.amount)
                    throw IllegalArgumentException("$withdrawal: insufficient balance")
                em.merge(Account(account.id, account.balance - withdrawal.amount))
            })
    }

    fun transfer(transfer: Transfer) {
        transactionManager.executeInTransaction(
            Consumer { em ->
                val source = em.find(Account::class.java, transfer.sourceId)
                    ?: throw IllegalArgumentException("$transfer: account ${transfer.sourceId} not exists")
                val target = em.find(Account::class.java, transfer.targetId)
                    ?: throw IllegalArgumentException("$transfer: account ${transfer.targetId} not exists")
                if (source.balance < transfer.amount)
                    throw IllegalArgumentException("$transfer: insufficient balance")
                em.merge(Account(source.id, source.balance - transfer.amount))
                em.merge(Account(target.id, target.balance + transfer.amount))
            })
    }
}