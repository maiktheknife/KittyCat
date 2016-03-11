package net.kivitro.kittycat.model

/**
 * Created by Max on 11.03.2016.
 */
data class CatCategory(var data: DataCategory? = null)

data class DataCategory(var categories: List<Category>? = null)

data class Category(var id: Int, var name: String)