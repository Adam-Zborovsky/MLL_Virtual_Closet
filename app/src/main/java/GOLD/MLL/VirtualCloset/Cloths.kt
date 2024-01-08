package GOLD.MLL.VirtualCloset

data class Cloths(
    val name: String,
    val typeCloth: String,
    val sLike: Int,
    val aLike: Int,
    val photoUrl: String,
    val backsideUrl: String,
    val matching: List<String>
)

