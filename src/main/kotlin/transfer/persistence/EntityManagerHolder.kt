package transfer.persistence

import com.google.inject.Inject
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import transfer.account.AccountRepository
import java.util.function.Consumer
import javax.persistence.EntityManager

@Singleton
class EntityManagerHolder @Inject constructor(private val em: EntityManager) {

    private val logger = LoggerFactory.getLogger(AccountRepository::class.java)

    @Synchronized fun executeInTransaction(execution: Consumer<EntityManager>) {
        val transaction = em.transaction
        transaction.begin()
        try {
            execution.accept(em)
            transaction.commit()
        } catch (e: Exception) {
            logger.error(e.message, e)
            transaction.rollback()
            throw e
        }
    }

    fun <T> getById(id: String, type: Class<T>): T? {
        return em.find(type, id)
    }
}