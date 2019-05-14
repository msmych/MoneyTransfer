package transfer.module

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence.createEntityManagerFactory

class AppPersistenceModule : AbstractModule() {

    private val emCache = ThreadLocal<EntityManager>()

    @Provides
    @Singleton
    fun getEntityManagerFactory(): EntityManagerFactory {
        return createEntityManagerFactory("money-transfer")
    }

    @Provides
    @Singleton
    fun getEntityManager(emf: EntityManagerFactory): EntityManager {
        var em = emCache.get()
        if (em == null) {
            em = emf.createEntityManager()
            emCache.set(em)
        }
        return em
    }
}
