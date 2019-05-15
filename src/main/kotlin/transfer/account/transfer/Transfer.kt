package transfer.account.transfer

data class Transfer(val sourceId: String?,
                    val targetId: String?,
                    val amount: Long)