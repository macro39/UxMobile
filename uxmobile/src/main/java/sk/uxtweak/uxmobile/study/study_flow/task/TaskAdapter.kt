package sk.uxtweak.uxmobile.study.study_flow.task

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.RadioButton
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyTask


/**
 * Created by Kamil Macek on 25.2.2020.
 */
class TaskAdapter(
    private val context: Context,
    private val dataSource: ArrayList<StudyTask>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mSelectedRadioButton: RadioButton? = null
    private var selectedPosition = -1

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = inflater.inflate(R.layout.adapter_task, parent, false)
            viewHolder =
                ViewHolder(
                    view.findViewById<RadioButton>(R.id.radioButton_task),
                    view.findViewById<ImageButton>(R.id.imageButton_task)
                )

            (view as View).setTag(viewHolder)
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.radioButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (position != selectedPosition && mSelectedRadioButton != null) {
                    mSelectedRadioButton!!.isChecked = false
                }

                selectedPosition = position
                mSelectedRadioButton = v as RadioButton
            }
        })

        val task = dataSource[position]

        viewHolder.imageButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(task.name)
            alertDialog.setMessage(task.description)
            alertDialog.show()
        }

        viewHolder.radioButton.text = task.name

        if (position == 0) {
            selectedPosition = 0
            mSelectedRadioButton = viewHolder.radioButton
            viewHolder.radioButton.isChecked = true
            return view
        }

        if (selectedPosition != position) {
            viewHolder.radioButton.isChecked = false
        } else {
            viewHolder.radioButton.isChecked = false
            if (mSelectedRadioButton != null && viewHolder.radioButton != mSelectedRadioButton) {
                mSelectedRadioButton = viewHolder.radioButton
            }
        }

        return view
    }

    fun getSelectedTask(): String {
        return dataSource.first { it.name == mSelectedRadioButton!!.text }.name
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    class ViewHolder(
        var radioButton: RadioButton,
        var imageButton: ImageButton
    )
}
