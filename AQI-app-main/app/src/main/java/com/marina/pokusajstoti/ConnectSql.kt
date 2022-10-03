package com.marina.pokusajstoti

import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectSql {

    private val port= 1433
    private val ip="147.91.9.125"
    private val database="AirQualityIndex"
    private val username="sa"
    private val password="123"
    private val Classes="net.sourceforge.jtds.jdbc.Driver"



    fun dbConnect():Connection?{

        val policy=StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var conn:Connection?=null
        val connString : String

        try{
            Class.forName(Classes).newInstance()
            connString= "jdbc:jtds:sqlserver://$ip:$port/$database;user=$username;password=$password"
            conn=DriverManager.getConnection(connString)

        }catch (ex:SQLException){
            Log.i("Error",ex.message!!)

        }catch (ex1: ClassNotFoundException){
            Log.i("Error",ex1.message!!)

        }catch (ex2: Exception){
            Log.i("Error",ex2.message!!)

        }


        return conn
    }

}