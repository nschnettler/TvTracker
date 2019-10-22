package de.schnettler.tvtracker.ui.discover

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.databinding.ShowViewItemBinding

class ShowListAdapter: ListAdapter<Show, ShowListAdapter.ShowViewHolder>(DiffCallBack) {
    class ShowViewHolder(private var binding: ShowViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (show: Show) {
            binding.show = show
            binding.executePendingBindings()
        }
    }

    object DiffCallBack: DiffUtil.ItemCallback<Show>() {
        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean = oldItem.traktId == newItem.traktId
        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        return ShowViewHolder(ShowViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = getItem(position)
        holder.bind(show)
    }
}