package harry.boilerplate.shop.command.infrastructure.repository

import harry.boilerplate.shop.command.domain.aggregate.Shop
import harry.boilerplate.shop.command.domain.aggregate.ShopRepository
import harry.boilerplate.shop.command.domain.valueObject.ShopId
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ShopRepositoryImpl(
    private val shopJpaRepository: ShopJpaRepository
) : ShopRepository {

    override fun save(shop: Shop) {
        requireNotNull(shop) { "Shop cannot be null" }
        shopJpaRepository.save(shop)
    }

    override fun findById(shopId: ShopId): Shop? {
        return shopJpaRepository.findById(shopId.value)
            .orElseThrow { ShopNotFoundException(shopId) }
    }

    override fun existsById(shopId: ShopId): Boolean {
        return shopJpaRepository.existsById(shopId.value)
    }

    override fun delete(shop: Shop) {
        if (shopJpaRepository.existsById(shop.id.value)) {
            shopJpaRepository.delete(shop)
        }
    }

    override fun deleteById(shopId: ShopId) {
        if (shopJpaRepository.existsById(shopId.value)) {
            shopJpaRepository.deleteById(shopId.value)
        }
    }
}
