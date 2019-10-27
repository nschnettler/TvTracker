package de.schnettler.tvtracker.ui.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.schnettler.tvtracker.data.model.Show
import de.schnettler.tvtracker.databinding.ShowViewItemBinding
import timber.log.Timber

class ShowListAdapter(private val onClickListener: OnClickListener, val type: String): ListAdapter<Show, ShowListAdapter.ShowViewHolder>(DiffCallBack) {
    class ShowViewHolder(private var binding: ShowViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (show: Show, onClickListener: OnClickListener, type: String) {
            ViewCompat.setTransitionName(binding.showPoster, "$type-${show.id}")
            binding.show = show
            binding.executePendingBindings()
            binding.showPoster.setOnClickListener {
                onClickListener.onClick(show, it)
            }
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
        holder.bind(getItem(position), onClickListener, type)
    }

    class OnClickListener(val clickListener: (show: Show, view: View) -> Unit) {
        fun onClick(show: Show, view: View) = clickListener(show, view)
    }
}