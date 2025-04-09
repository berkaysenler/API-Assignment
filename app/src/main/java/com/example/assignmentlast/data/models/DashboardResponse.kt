// app/src/main/java/com/example/assignmentlast/data/models/DashboardResponse.kt
package com.example.assignmentlast.data.models

data class DashboardResponse(
//    val entities: List<Entity>,
//    val entityTotal: Int

    val entities: List<Map<String, Any>>,
    val entityTotal: Int
)