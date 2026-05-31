package com.sean.pictureaudiowidget.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sean.pictureaudiowidget.R

class FolderTreeAdapter(
    private val onToggleExpanded: (String) -> Unit,
    private val onToggleSelected: (String) -> Unit,
) : RecyclerView.Adapter<FolderTreeAdapter.ViewHolder>() {
    private var items: List<VisibleSourceFolderNode> = emptyList()

    fun submitList(newItems: List<VisibleSourceFolderNode>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_tree_node, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onToggleExpanded, onToggleSelected)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val container: LinearLayout = view.findViewById(R.id.rowContainer)
        private val expandIcon: TextView = view.findViewById(R.id.expandIcon)
        private val folderName: TextView = view.findViewById(R.id.folderName)
        private val folderMeta: TextView = view.findViewById(R.id.folderMeta)
        private val checkbox: CheckBox = view.findViewById(R.id.folderCheckbox)
        private val basePadding = container.paddingStart

        fun bind(
            item: VisibleSourceFolderNode,
            onToggleExpanded: (String) -> Unit,
            onToggleSelected: (String) -> Unit,
        ) {
            val indent = itemView.resources.displayMetrics.density.times(item.depth * 18).toInt()
            container.setPaddingRelative(basePadding + indent, container.paddingTop, container.paddingEnd, container.paddingBottom)

            folderName.text = item.name
            folderMeta.text = buildMeta(item)
            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = item.isChecked
            checkbox.setOnClickListener { onToggleSelected(item.path) }

            expandIcon.visibility = if (item.isExpandable) View.VISIBLE else View.INVISIBLE
            expandIcon.text = if (item.isExpanded) "▾" else "▸"
            expandIcon.setOnClickListener {
                if (item.isExpandable) onToggleExpanded(item.path)
            }
            container.setOnClickListener {
                if (item.isExpandable) onToggleExpanded(item.path) else onToggleSelected(item.path)
            }
        }

        private fun buildMeta(item: VisibleSourceFolderNode): String {
            return when {
                item.selectedDescendantCount > 0 && !item.isChecked -> "${item.totalItemCount} items • ${item.selectedDescendantCount} selected below"
                item.isExpandable -> "${item.totalItemCount} items • tap to expand"
                else -> "${item.totalItemCount} items"
            }
        }
    }
}
