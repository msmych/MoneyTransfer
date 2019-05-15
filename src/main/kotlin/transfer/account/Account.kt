package transfer.account

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Account(@Id val id: String = "",
                   @Column val balance: Long = 0)