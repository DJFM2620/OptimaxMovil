package pe.idat.optimax

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import pe.idat.optimax.databinding.ItemArticleCartBinding
import pe.idat.optimax.databinding.ItemListOptionsBinding
import pe.idat.optimax.model.ListViewOption

class ListViewAdapter(
    private val mContext: Context,
    private val listOptions: List<ListViewOption>) : ArrayAdapter<ListViewOption>(mContext, 0, listOptions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layout = LayoutInflater.from(mContext).inflate(R.layout.item_list_options, parent, false)

        val binding = ItemListOptionsBinding.bind(layout)
        val option = listOptions[position]

        binding.ivImage.setImageResource(option.image)
        binding.tvNameOption.text = option.name

        return binding.root
    }
}