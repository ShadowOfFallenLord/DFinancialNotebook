package dark.andapp.dfinnb.presentaion.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dark.andapp.dfinnb.R
import dark.andapp.dfinnb.databinding.RecyclerViewNamedListItemBinding
import dark.andapp.dfinnb.domain.entity.NamedEntity

class NamedAdapter(
    private val entities: List<NamedEntity>
) : RecyclerView.Adapter<NamedAdapter.ViewHolder>() {

    class ViewHolder(val binding: RecyclerViewNamedListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewNamedListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            val entity = entities[position]

            tvId.text = entity.id.toString()
            tvName.text = entity.name
            tvCount.text = entity.count.toString()
            setTextWithColor(tvAmount, entity.amount, holder)
        }
    }

    private fun setTextWithColor(textView: TextView, value: Number, holder: ViewHolder) {
        if (value.toDouble() < 0) {
            textView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        } else {
            textView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
        }

        if (value.toDouble() <= 0) {
            textView.text = value.toString()
        } else {
            textView.text = "+${value}"
        }
    }

    override fun getItemCount(): Int {
        return entities.count()
    }
}