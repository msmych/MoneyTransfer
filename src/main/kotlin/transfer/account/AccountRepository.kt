package transfer.account

import com.google.inject.Inject
import com.google.inject.Singleton
import transfer.account.deposit.Deposit
import transfer.account.transfer.Transfer
import transfer.account.withdraw.Withdrawal
import transfer.persistence.EntityManagerHolder
import java.util.function.Consumer

@Singleton
class AccountRepository @Inject constructor(private val entityManagerHolder: EntityManagerHolder) {

    fun getById(id: String): Account? {
        return entityManagerHolder.getById(id, Account::class.java)
    }

    fun create(id: String) {
        entityManagerHolder.executeInTransaction(
            Consumer { em ->
                if (em.find(Account::class.java, id) == null)
                    em.persist(Account(id, 0))
            })
    }

    fun remove(id: String) {
        entityManagerHolder.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, id)
                        ?: throw IllegalArgumentException("Account $id not exists")
                em.remove(account)
            })
    }

    fun deposit(deposit: Deposit) {
        entityManagerHolder.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, deposit.accountId)
                    ?: throw IllegalArgumentException("Account ${deposit.accountId} not exists")
                em.merge(Account(account.id, account.balance + deposit.amount))
            })
    }

    fun withdraw(withdrawal: Withdrawal) {
        entityManagerHolder.executeInTransaction(
            Consumer { em ->
                val account = em.find(Account::class.java, withdrawal.accountId)
                    ?: throw IllegalArgumentException("Account ${withdrawal.accountId} not exists")
                if (account.balance < withdrawal.amount)
                    throw IllegalArgumentException("Insufficient balance")
                em.merge(Account(account.id, account.balance - withdrawal.amount))
            })
    }

    fun transfer(transfer: Transfer) {
        entityManagerHolder.executeInTransaction(
            Consumer { em ->
                val source = em.find(Account::class.java, transfer.sourceId)
                    ?: throw IllegalArgumentException("Account ${transfer.sourceId} not exists")
                val target = em.find(Account::class.java, transfer.targetId)
                    ?: throw IllegalArgumentException("Account ${transfer.targetId} not exists")
                if (source.balance < transfer.amount)
                    throw IllegalArgumentException("Insufficient balance")
                em.merge(Account(source.id, source.balance - transfer.amount))
                em.merge(Account(target.id, target.balance + transfer.amount))
            })
    }
}