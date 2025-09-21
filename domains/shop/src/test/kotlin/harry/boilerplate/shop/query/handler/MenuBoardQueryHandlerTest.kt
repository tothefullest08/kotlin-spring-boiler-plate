package harry.boilerplate.shop.query.handler

import harry.boilerplate.shop.query.application.dto.MenuBoardQuery
import harry.boilerplate.shop.query.application.handler.MenuBoardQueryHandler
import harry.boilerplate.shop.query.application.readModel.MenuBoardViewModel
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel
import harry.boilerplate.shop.query.infrastructure.dao.MenuQueryDao
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
@DisplayName("MenuBoardQueryHandler 테스트")
class MenuBoardQueryHandlerTest {

    @MockK
    private lateinit var menuQueryDao: MenuQueryDao

    @InjectMockKs
    private lateinit var menuBoardQueryHandler: MenuBoardQueryHandler

    private lateinit var query: MenuBoardQuery
    private lateinit var viewModel: MenuBoardViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        query = MenuBoardQuery("shop-1")

        val menu1 = MenuSummaryReadModel(
            id = "menu-1",
            shopId = "shop-1",
            name = "삼겹살",
            description = "맛있는 삼겹살",
            basePrice = BigDecimal("15000"),
            isOpen = true,
            optionGroupCount = 2
        )
        val menu2 = MenuSummaryReadModel(
            id = "menu-2",
            shopId = "shop-1",
            name = "냉면",
            description = "시원한 냉면",
            basePrice = BigDecimal("8000"),
            isOpen = true,
            optionGroupCount = 1
        )

        viewModel = MenuBoardViewModel(
            shopId = "shop-1",
            shopName = "맛있는 가게",
            isShopOpen = true,
            openMenus = listOf(menu1, menu2),
            closedMenus = emptyList()
        )
    }

    @Test
    @DisplayName("메뉴보드 조회 성공")
    fun 메뉴보드_조회_성공() {
        every { menuQueryDao.getMenuBoard(any()) } returns viewModel

        val result = menuBoardQueryHandler.handle(query)

        assertThat(result).isNotNull
        assertThat(result?.shopId).isEqualTo("shop-1")
        assertThat(result?.shopName).isEqualTo("맛있는 가게")
        assertThat(result?.isShopOpen).isTrue
        assertThat(result?.menus).hasSize(2)
        assertThat(result?.menus?.get(0)?.name).isEqualTo("삼겹살")
        assertThat(result?.menus?.get(1)?.name).isEqualTo("냉면")

        verify { menuQueryDao.getMenuBoard("shop-1") }
    }

    @Test
    @DisplayName("메뉴가 없는 가게의 메뉴보드 조회")
    fun 메뉴가_없는_가게의_메뉴보드_조회() {
        val emptyViewModel = MenuBoardViewModel(
            shopId = "shop-1",
            shopName = "메뉴 없는 가게",
            isShopOpen = true,
            openMenus = emptyList(),
            closedMenus = emptyList()
        )
        every { menuQueryDao.getMenuBoard(any()) } returns emptyViewModel

        val result = menuBoardQueryHandler.handle(query)

        assertThat(result).isNotNull
        assertThat(result?.shopId).isEqualTo("shop-1")
        assertThat(result?.shopName).isEqualTo("메뉴 없는 가게")
        assertThat(result?.isShopOpen).isTrue
        assertThat(result?.menus).isEmpty()

        verify { menuQueryDao.getMenuBoard("shop-1") }
    }

    @Test
    @DisplayName("존재하지 않는 가게 조회 시 null 반환")
    fun 존재하지_않는_가게_조회_시_null_반환() {
        every { menuQueryDao.getMenuBoard(any()) } returns null

        val result = menuBoardQueryHandler.handle(query)

        assertThat(result).isNull()
        verify { menuQueryDao.getMenuBoard("shop-1") }
    }

    @Test
    @DisplayName("null query로 조회 시 예외 발생")
    fun null_query로_조회_시_예외_발생() {
        assertThatThrownBy { menuBoardQueryHandler.handle(null) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("MenuBoardQuery는 필수입니다")

        verify(exactly = 0) { menuQueryDao.getMenuBoard(any()) }
    }

    @Test
    @DisplayName("DAO 조회 중 예외 발생 시 전파")
    fun DAO_조회_중_예외_발생_시_전파() {
        every { menuQueryDao.getMenuBoard(any()) } throws RuntimeException("데이터베이스 오류")

        assertThatThrownBy { menuBoardQueryHandler.handle(query) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("데이터베이스 오류")

        verify { menuQueryDao.getMenuBoard("shop-1") }
    }

    @Test
    @DisplayName("영업 중이 아닌 가게의 메뉴보드 조회")
    fun 영업_중이_아닌_가게의_메뉴보드_조회() {
        val closedMenu = MenuSummaryReadModel(
            id = "menu-1",
            shopId = "shop-1",
            name = "삼겹살",
            description = "맛있는 삼겹살",
            basePrice = BigDecimal("15000"),
            isOpen = false,
            optionGroupCount = 2
        )
        val closedShopViewModel = MenuBoardViewModel(
            shopId = "shop-1",
            shopName = "문 닫은 가게",
            isShopOpen = false,
            openMenus = emptyList(),
            closedMenus = listOf(closedMenu)
        )
        every { menuQueryDao.getMenuBoard(any()) } returns closedShopViewModel

        val result = menuBoardQueryHandler.handle(query)

        assertThat(result).isNotNull
        assertThat(result?.shopId).isEqualTo("shop-1")
        assertThat(result?.shopName).isEqualTo("문 닫은 가게")
        assertThat(result?.isShopOpen).isFalse
        assertThat(result?.menus).isEmpty()

        verify { menuQueryDao.getMenuBoard("shop-1") }
    }

    @Test
    @DisplayName("MenuBoardViewModel에서 MenuBoardResult로 정확한 매핑")
    fun MenuBoardViewModel에서_MenuBoardResult로_정확한_매핑() {
        val openMenu = MenuSummaryReadModel(
            id = "menu-1",
            shopId = "shop-1",
            name = "삼겹살",
            description = "맛있는 삼겹살",
            basePrice = BigDecimal("15000"),
            isOpen = true,
            optionGroupCount = 2
        )
        val closedMenu = MenuSummaryReadModel(
            id = "menu-2",
            shopId = "shop-1",
            name = "냉면",
            description = "시원한 냉면",
            basePrice = BigDecimal("8000"),
            isOpen = false,
            optionGroupCount = 1
        )
        val complexViewModel = MenuBoardViewModel(
            shopId = "shop-1",
            shopName = "복합 메뉴 가게",
            isShopOpen = true,
            openMenus = listOf(openMenu),
            closedMenus = listOf(closedMenu)
        )
        every { menuQueryDao.getMenuBoard(any()) } returns complexViewModel

        val result = menuBoardQueryHandler.handle(query)

        assertThat(result?.shopId).isEqualTo(complexViewModel.shopId)
        assertThat(result?.shopName).isEqualTo(complexViewModel.shopName)
        assertThat(result?.isShopOpen).isEqualTo(complexViewModel.isShopOpen)
        assertThat(result?.menus).hasSize(1)
    }
}
