package com.marina.pokusajstoti.ui.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.marina.pokusajstoti.ConnectSql
import com.marina.pokusajstoti.MyValueFormatter
import com.marina.pokusajstoti.R
import com.marina.pokusajstoti.databinding.FragmentStatisticsBinding
import java.sql.*
import java.time.LocalDateTime
import kotlin.random.Random

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit  var pm10_bar_chart : BarChart;
    private  var pm10_entries:ArrayList<BarEntry> = ArrayList<BarEntry>()
    private lateinit  var pm2_5_bar_chart : BarChart;
    private  var pm2_5_entries:ArrayList<BarEntry> = ArrayList<BarEntry>()


    private lateinit  var connection:Connection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(StatisticsViewModel::class.java)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        connection= ConnectSql().dbConnect()!!

        initBarPM10Chart()
        initBarPM2_5Chart()

        return root
    }




    @SuppressLint("NewApi")
    fun initBarPM10Chart(){
        pm10_bar_chart=binding.barChartPM10
        val current_day_offset = LocalDateTime.now().toLocalDate().dayOfWeek.ordinal

        val barDataset:BarDataSet= load_values("pm10_avg",current_day_offset)
        //set colors
        barDataset.setColors(Color.rgb(83,94,75), Color.rgb(193,194,173))
        //hide draw values
        // barDataset.setDrawValues(false)


        pm10_bar_chart.data= BarData(barDataset)
        pm10_bar_chart.description.text=""
        pm10_bar_chart.animateY(3000)
        pm10_bar_chart.xAxis.valueFormatter= MyValueFormatter(current_day_offset.toFloat())
    }

    @SuppressLint("NewApi")
    fun initBarPM2_5Chart(){
        pm2_5_bar_chart=binding.barChartPM25
        val current_day_offset = LocalDateTime.now().toLocalDate().dayOfWeek.ordinal

        val barDataset:BarDataSet= load_values("pm2_5_avg",current_day_offset)
        //set colors
        barDataset.setColors(Color.rgb(83,94,75), Color.rgb(193,194,173))
        //hide draw values
        // barDataset.setDrawValues(false)


        pm2_5_bar_chart.data= BarData(barDataset)
        pm2_5_bar_chart.description.text=""
        pm2_5_bar_chart.animateY(3000)
        pm2_5_bar_chart.xAxis.valueFormatter= MyValueFormatter(current_day_offset.toFloat())

    }


    fun load_values(pm:String, current_day:Int):BarDataSet{
        try {
            var stm : PreparedStatement = connection.prepareStatement("select  TOP 7" + pm + " from AVG_VALUES order by id desc  ")

            var res: ResultSet = stm.executeQuery()

            var date_offset:Int = (current_day - 1)%7

            while(res.next()){
                val entry:BarEntry = BarEntry(date_offset.toFloat(),res.getFloat(1))
                if(pm.equals("pm10_avg"))
                pm10_entries.add(entry)
                else
                    pm2_5_entries.add(entry)

                date_offset=(date_offset-1)%7
                Log.i("ENTRY",""+entry.toString())
            }


        }catch (e : SQLException){

            Log.e("Error","Fatal error")

        }
        if(pm.equals("pm10_avg"))
        return BarDataSet(pm10_entries,"PM10")
        else
            return BarDataSet(pm2_5_entries,"PM2.5")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        connection.close()
    }
}