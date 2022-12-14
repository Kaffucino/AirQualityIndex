import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.marina.pokusajstoti.R
import com.marina.pokusajstoti.util.ItemsViewModel

class CustomAdapter(private val mList: List<ItemsViewModel>, val clickListener: (ItemsViewModel) -> Unit) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(ItemsViewModel.image)
        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.name
        holder.bind(mList[position], position, clickListener)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewDevice)
        val textView: TextView = itemView.findViewById(R.id.textViewDevice)
        fun bind(itemsViewModel: ItemsViewModel, position: Int, clickListener: (ItemsViewModel) -> Unit ) {
            itemView.setOnClickListener{clickListener(itemsViewModel)}
        }
    }
}
