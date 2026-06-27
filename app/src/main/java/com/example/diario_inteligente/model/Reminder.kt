package com.example.diario_inteligente.model

data class Reminder (
    val id: String = "",
    val userId: String = "", // Para garantir que cada usuário só veja os seus lembretes
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val prazoData: String = "",  // Data do prazo final (Ex: 29/06/2026)
    val prazoHora: String = "",  // Hora do prazo final (Ex: 14:00)
    val imageBase64: String = "" // Foto tirada pela câmera convertida pelo seu Base64Converter
)