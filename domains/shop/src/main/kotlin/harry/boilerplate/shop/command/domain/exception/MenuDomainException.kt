package harry.boilerplate.shop.command.domain.exception

import harry.boilerplate.common.exception.DomainException

open class MenuDomainException(
    override val errorCode: MenuErrorCode,
    cause: Throwable? = null,
    additionalMessage: String? = null
) : DomainException(
    listOfNotNull(errorCode.message, additionalMessage).joinToString(": "),
    cause
)
