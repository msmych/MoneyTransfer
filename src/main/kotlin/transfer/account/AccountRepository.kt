package transfer.account

import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import javax.persistence.EntityManager

@Singleton
class AccountRepository @Inject constructor(private val em: EntityManager) {

    private val logger = LoggerFactory.getLogger(AccountRepository::class.java)

    @Synchronized private fun executeInTransaction(runnable: Runnable) {
        val transaction = em.transaction
        transaction.begin()
        try {
            runnable.run()
            transaction.commit()
        } catch (e: Exception) {
            logger.error(e.message, e)
            transaction.rollback()
        }
    }

    fun getById(id: String): Account? {
        return em.find(Account::class.java, id)
    }

    fun create(id: String) {
        executeInTransaction(
            Runnable {
                if (em.find(Account::class.java, id) == null)
                    em.persist(Account(id, 0))
            })
    }
}