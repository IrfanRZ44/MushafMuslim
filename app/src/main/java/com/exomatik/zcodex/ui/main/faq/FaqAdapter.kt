package com.exomatik.zcodex.ui.main.faq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.model.ModelFaq
import com.exomatik.zcodex.services.expandableLayout.ExpandableLayout
import kotlinx.android.synthetic.main.item_faq.view.*

class FaqAdapter(private val itemsCells: ArrayList<ModelFaq>) :
    RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    // Save the expanded row position
    private val expandedPositionSet: HashSet<Int> = HashSet()
    lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        val vh = ViewHolder(v)
        context = parent.context
        return vh
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Add data to cells
        holder.itemView.textQuestion.text = itemsCells[position].question
        holder.itemView.textAnswer.text = itemsCells[position].answer

        // Expand when you click on cell
        holder.itemView.expand_layout.setOnExpandListener(object :
            ExpandableLayout.OnExpandListener {
            override fun onExpand(expanded: Boolean) {
                if (expandedPositionSet.contains(position)) {
                    holder.itemView.imgDropdown.setImageResource(R.drawable.ic_arrow_drop_down_black)
                    expandedPositionSet.remove(position)
                } else {
                    holder.itemView.imgDropdown.setImageResource(R.drawable.ic_arrow_drop_up_black)
                    expandedPositionSet.add(position)
                }
            }
        })
        holder.itemView.expand_layout.setExpand(expandedPositionSet.contains(position))
    }
}