package ua.anastasiia.finesapp.web.model

data class Results(
    var plate: String? = null,
    var model_make: List<ModelMake> = arrayListOf(),
    var color: List<Color> = arrayListOf()
)
