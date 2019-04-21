package com.sun.tunnelmonitoring.db.manager

import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import org.litepal.extension.findAll

//unused
object UserDao{
    fun queryAll():List<User>?{
        return LitePal.findAll<User>()
    }

    fun queryById(id:Long): User? {
        return LitePal.find<User>(id)
    }

    fun queryByName(name:String): List<User>? {
        return LitePal.where("username like ? ",name).find()
    }

    fun deleteAll(){
        LitePal.deleteAll(User::class.java)
    }

    fun deleteByName(name:String): Int {
        return LitePal.deleteAll<User>("username like ?",name)
    }

    fun delete(id:Long){
        LitePal.delete<User>(id)
    }

}