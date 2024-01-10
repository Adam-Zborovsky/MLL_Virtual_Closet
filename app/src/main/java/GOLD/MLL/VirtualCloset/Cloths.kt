package GOLD.MLL.VirtualCloset

import java.io.Serializable

data class Cloths(
    val name: String,
    val typeCloth: String,
    val sLike: Int,
    val aLike: Int,
    val photoUrl: String,
    val backsideUrl: String,
    val matching: ArrayList<String>
): Serializable{
    override fun toString(): String {
            return "$name,$typeCloth,$sLike,$aLike,$photoUrl,$backsideUrl,$matching"
    }
}