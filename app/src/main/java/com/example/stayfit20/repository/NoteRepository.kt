package com.example.stayfit20.repository

import com.example.stayfit20.model.NoteModel

interface NoteRepository {
    fun addProduct(product: NoteModel, callback: (Boolean, String) -> Unit)
    fun updateProduct(productId: String, updates: Map<String, Any>, callback: (Boolean, String) -> Unit)
    fun getProduct(productId: String, callback: (NoteModel?) -> Unit)
    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit)
}


