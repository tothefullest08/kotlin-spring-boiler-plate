package harry.boilerplate.shop.query.application.handler

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery
import harry.boilerplate.shop.query.application.dto.MenuBoardResult
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class MenuBoardQueryHandler(
    private val menuQueryDao: MenuQueryDao
) {
    fun handle(query: MenuBoardQuery?): MenuBoardResult? {
        requireNotNull(query) { "MenuBoardQuery는 필수입니다" }
        val viewModel = menuQueryDao.getMenuBoard(query.shopId) ?: return null
        return MenuBoardResult.from(viewModel)
    }
}
