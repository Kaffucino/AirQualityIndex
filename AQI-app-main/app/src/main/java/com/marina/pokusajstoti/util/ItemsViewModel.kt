package com.marina.pokusajstoti.util

data class ItemsViewModel(val image:Int,val name:String?, val MAC: String?){
//    override fun equals(other: Any?): Boolean {
//        if(other == null) return false
//        var item : ItemsViewModel = (ItemsViewModel) other
//    }
        override fun equals(other: Any?)
        = (other is ItemsViewModel)
        && name.equals(other.name)
        && MAC.equals(other.MAC)
}
