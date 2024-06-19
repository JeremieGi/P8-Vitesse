package com.openclassrooms.p8vitesse

// Met la première lettre en majuscule (Fonction d'extension de la classe String)
fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.uppercase()
        else it.toString()
    }
}