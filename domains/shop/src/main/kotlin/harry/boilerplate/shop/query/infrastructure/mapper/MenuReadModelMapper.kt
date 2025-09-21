package harry.boilerplate.shop.query.infrastructure.mapper

import harry.boilerplate.shop.command.domain.aggregate.Menu
import harry.boilerplate.shop.command.domain.entity.OptionGroup
import harry.boilerplate.shop.command.domain.valueObject.Option
import harry.boilerplate.shop.query.application.readModel.MenuDetailReadModel
import harry.boilerplate.shop.query.application.readModel.MenuSummaryReadModel
import harry.boilerplate.shop.query.application.readModel.OptionGroupReadModel
import harry.boilerplate.shop.query.application.readModel.OptionReadModel
import org.springframework.stereotype.Component

@Component
class MenuReadModelMapper {
    fun toSummaryReadModel(menu: Menu): MenuSummaryReadModel = MenuSummaryReadModel(
        id = menu.id.value,
        shopId = menu.shopId.value,
        name = menu.name,
        description = menu.description,
        basePrice = menu.basePrice.amount,
        isOpen = menu.isOpen(),
        optionGroupCount = menu.optionGroups.size
    )

    fun toDetailReadModel(menu: Menu): MenuDetailReadModel = MenuDetailReadModel(
        id = menu.id.value,
        shopId = menu.shopId.value,
        name = menu.name,
        description = menu.description,
        basePrice = menu.basePrice.amount,
        isOpen = menu.isOpen(),
        optionGroups = menu.optionGroups.map(::toOptionGroupReadModel),
        createdAt = menu.createdAt,
        updatedAt = menu.updatedAt
    )

    fun toOptionGroupReadModel(optionGroup: OptionGroup): OptionGroupReadModel = OptionGroupReadModel(
        id = optionGroup.id.value,
        name = optionGroup.name,
        required = optionGroup.isRequired,
        options = optionGroup.options.map(::toOptionReadModel)
    )

    fun toOptionReadModel(option: Option): OptionReadModel = OptionReadModel(
        name = option.name,
        price = option.price.amount
    )
}
