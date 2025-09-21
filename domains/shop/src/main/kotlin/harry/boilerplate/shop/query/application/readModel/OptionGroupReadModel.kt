package harry.boilerplate.shop.query.application.readModel

data class OptionGroupReadModel(
    val id: String,
    val name: String,
    val required: Boolean,
    val options: List<OptionReadModel>
)
