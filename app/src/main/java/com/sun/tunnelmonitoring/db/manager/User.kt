package com.sun.tunnelmonitoring.db.manager

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class User(
    @Column(nullable = false,unique = true)
    var username: String,
    var company: String?,
    var department: String?,
    var post: String?,
    var email: String?,
    var mobile: String
) : LitePalSupport(){
    override fun toString(): String {
        return "username: $username, company: $company"
    }
}