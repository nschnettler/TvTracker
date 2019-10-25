package de.schnettler.tvtracker.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.databinding.ShowViewItemBinding
import timber.log.Timber

class ShowListAdapter(private val onClickListener: OnClickListener): ListAdapter<Show, ShowListAdapter.ShowViewHolder>(DiffCallBack) {
    class ShowViewHolder(private var binding: ShowViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (show: Show) {
            binding.show = show
            binding.executePendingBindings()
        }
    }

    object DiffCallBack: DiffUtil.ItemCallback<Show>() {
        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        return ShowViewHolder(ShowViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = getItem(position)
        holder.itemView.setOnClickListener {
            Timber.i("Item Clicked")
            onClickListener.onClick(show)
        }
        holder.bind(show)
    }

    class OnClickListener(val clickListener: (show: Show) -> Unit) {
        fun onClick(show: Show) = clickListener(show)
    }
}