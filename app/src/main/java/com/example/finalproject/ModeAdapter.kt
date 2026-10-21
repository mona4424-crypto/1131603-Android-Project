package com.example.finalproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

//定義模式資料
data class ModeData(
    var icon: Int,
    var name: String
)
class ModeViewHolder {
    var icon: ImageView? = null
    var name: TextView? = null
    var check: CheckBox? = null
}

class ModeAdapter(context: Context, data: Array<ModeData>) :
    ArrayAdapter<ModeData>(context, R.layout.mode_item, data) {

    private val dataArray = data
    //記錄哪個項目被選取預設為 -1，表示都沒選
    private var selectedPosition = -1

    override fun getCount(): Int = dataArray.size
    override fun getItem(position: Int): ModeData = dataArray[position]

    //更新選取狀態 單選
    fun setSelectedIndex(position: Int) {
        selectedPosition = position
        notifyDataSetChanged() //刷新清單畫面
    }

    fun getSelectedIndex(): Int = selectedPosition

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val item = dataArray[position]
        val holder: ModeViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.mode_item, parent, false)
            holder = ModeViewHolder()
            holder.icon = view.findViewById(R.id.modeIcon)
            holder.name = view.findViewById(R.id.modeName)
            holder.check = view.findViewById(R.id.modeCheckBox)
            view.tag = holder
        } else {
            holder = view.tag as ModeViewHolder
        }

        holder.icon?.setImageResource(item.icon)
        holder.name?.text = item.name

        //只有被選中的項目，CheckBox 才會打勾
        holder.check?.isChecked = (position == selectedPosition)

        return view!!
    }
}