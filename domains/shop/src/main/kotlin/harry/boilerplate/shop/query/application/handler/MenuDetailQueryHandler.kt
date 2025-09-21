package harry.boilerplate.shop.query.application.handler

import harry.boilerplate.shop.query.application.dto.MenuDetailQuery
import harry.boilerplate.shop.query.application.dto.MenuDetailResult
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class MenuDetailQueryHandler(
    private val menuQueryDao: MenuQueryDao
) {
    fun handle(query: MenuDetailQuery): MenuDetailResult {
        val menu = menuQueryDao.findMenuDetail(query.menuId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 메뉴입니다: ${query.menuId}") }
        return MenuDetailResult.from(menu)
    }
}
