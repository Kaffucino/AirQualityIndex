package com.marina.pokusajstoti.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.marina.pokusajstoti.ConnectSql
import com.marina.pokusajstoti.R
import com.marina.pokusajstoti.databinding.FragmentHomeBinding
import java.lang.Exception
import java.sql.*
import java.util.jar.Manifest

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var connection : Connection
    private val timer_delay : Long = 5000 //milliseconds
    private var handler : Handler = Handler(Looper.getMainLooper())
    private lateinit var myContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


       connection= ConnectSql().dbConnect()!!

        handler.post(object : Runnable{
            override fun run() {
                load_sql()
                handler.postDelayed(this,timer_delay)

            }
        })


        return root
    }




    private fun set_AQI_DESC(PM10 : Int,PM2_5: Int){


        if(PM10 <=20 || PM2_5 <=20){

            binding.textViewAQIDesc.text="Air Quality is Good!"
            binding.imageViewSmile.setImageResource(R.drawable.green_smile)
            binding.textViewAQIDesc.setTextColor(Color.rgb(87,120,49));

        }
        if (PM10 >= 21 && PM10 <= 40 || PM2_5 >= 21 && PM2_5 <= 40) {
            binding.textViewAQIDesc.text="Air Quality is Moderate!"
            binding.imageViewSmile.setImageResource(R.drawable.orange_smile)

            binding.textViewAQIDesc.setTextColor(Color.rgb(255,154,40));

        }
        if (PM10 >= 41 && PM10 <= 100 || PM2_5 >= 41 && PM2_5 <= 100) {
            binding.textViewAQIDesc.text="Air Quality is Unhealthy!"
            binding.imageViewSmile.setImageResource(R.drawable.dark_orange_smile)

            binding.textViewAQIDesc.setTextColor(Color.rgb(230,115,0));

        }
        if (PM10 >=101 || PM2_5 >=101) {
            binding.textViewAQIDesc.text="Air Quality is Hazardous!"
            binding.imageViewSmile.setImageResource(R.drawable.red_smile)
            binding.textViewAQIDesc.setTextColor(Color.RED);

        }


    }
    @SuppressLint("SetTextI18n")
    private fun load_sql() {

            try {
                var stm : PreparedStatement = connection.prepareStatement("select TOP 1 VALUE from VALUES_PM where PM='10' order by ID DESC ")

                var res: ResultSet = stm.executeQuery()
                var pm10 : Int=0;
                var pm2_5: Int=0;

                if(res.next()){
                    binding.textViewPM10Con.text=res.getString(1) + " μg/m3"
                    pm10=Integer.parseInt(res.getString(1))
                }
                stm=connection.prepareStatement("select TOP 1 VALUE from VALUES_PM where PM='2_5' order by ID DESC ")
                res=stm.executeQuery()

                if(res.next()){
                    binding.textViewPM25Con.text=res.getString(1) + " μg/m3"
                    pm2_5=Integer.parseInt(res.getString(1))

                }

                set_AQI_DESC(pm10,pm2_5)

            }catch (e : SQLException){

                Log.e("Error","Fatal error")

            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        connection.close()
    }
}



