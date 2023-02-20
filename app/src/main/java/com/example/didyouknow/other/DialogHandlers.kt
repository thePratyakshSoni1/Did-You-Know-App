package com.example.didyouknow.other

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.didyouknow.R
import com.google.android.material.button.MaterialButton

class DialogHandlers( val context:Context ) {

    fun showProgressDialog(
        lifecycleOwner: LifecycleOwner,
        status: LiveData<Resources<Boolean>>, onDoneClick:()->Unit = { Unit },
        dialogSuccessTxt:String, dialogErrorTxt:String, dialogLoadingTxt:String
    ){

        val uploadStatsDialog = Dialog(context).apply {
            setContentView(R.layout.info_dialog)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            findViewById<Button>(R.id.dialogCancelButton).setOnClickListener {
                dismiss()
            }

            val dialogText = findViewById<TextView>(R.id.dialogText)
            val actionBtn = findViewById<Button>(R.id.dialogActionButton)
            val dialogProgressBar = findViewById<ProgressBar>(R.id.dialogProgressBar).apply {
                visibility = View.GONE
            }

            val dialogImg = findViewById<ImageView>(R.id.dialogImage).apply {
                visibility = View.INVISIBLE
            }

            val statusObserver = Observer<Resources<Boolean>>{

                when(it.status){

                    Status.SUCCESS -> {
                        dialogImg.visibility = View.VISIBLE
                        dialogImg.setImageResource(R.drawable.ic_check_filled)
                        actionBtn.visibility = View.VISIBLE
                        actionBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
                        actionBtn.text = "Done"
                        dialogProgressBar.visibility = View.GONE
                        dialogText.text = dialogSuccessTxt
                    }

                    Status.PARTIAL_SUCCESS -> {
                        dialogImg.visibility = View.VISIBLE
                        dialogImg.setImageResource(R.drawable.ic_check_filled)
                        actionBtn.visibility = View.VISIBLE
                        actionBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
                        actionBtn.text = "Done"
                        dialogProgressBar.visibility = View.GONE
                        dialogText.text = it.message
                    }

                    Status.ERROR ->{
                        dialogImg.visibility = View.VISIBLE
                        dialogImg.setImageResource(R.drawable.ic_error_round)
                        actionBtn.visibility = View.VISIBLE
                        actionBtn.setBackgroundColor(Color.parseColor("#FF3636"))
                        actionBtn.text = "Back"
                        dialogProgressBar.visibility = View.GONE
                        dialogText.text = dialogErrorTxt


                    }

                    Status.LOADING -> {
                        dialogText.text = dialogLoadingTxt
                        dialogProgressBar.visibility = View.VISIBLE
                        dialogImg.visibility = View.INVISIBLE
                        dialogImg.setImageResource(R.drawable.ic_error_round)
                        actionBtn.visibility = View.GONE
                    }


                }

            }

            actionBtn.setOnClickListener {
                status.removeObserver(statusObserver)
                onDoneClick()
                dismiss()

            }
            status.observe(lifecycleOwner, statusObserver)
            show()

        }


    }


    fun showWarningDialog(
        diaogText:String,
        positiveButtonTxt:String = "Yes",
        negativeButtonTxt:String = "Cancel",
        onPositiveButtonClick:()->Unit = { Unit },
        onNegativeButtonClick:()->Unit = { Unit },
        dialogImgRes:Drawable = AppCompatResources.getDrawable(context,R.drawable.ic_error_round)!!,
        buttonColorResId:Int = R.color.button_color_red
    ){

        Dialog(context).apply {
            setContentView(R.layout.warning_dialog)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val dialogPositiveButton = findViewById<Button>(R.id.dialogPositiveButton)
            val dialogNegativeButton = findViewById<MaterialButton>(R.id.dialogNegativeButton)
            val dialogImage =  findViewById<ImageView>(R.id.dialogImage)
            val dialogText =  findViewById<TextView>(R.id.dialogText)

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogPositiveButton.setBackgroundColor(context.getColor(buttonColorResId))

            findViewById<Button>(R.id.dialogDismissButton).setOnClickListener {
                dismiss()
            }

            dialogText.setText(diaogText)
            dialogImage.setImageDrawable(dialogImgRes)

            dialogPositiveButton.apply {
                text = positiveButtonTxt
                setOnClickListener {
                    onPositiveButtonClick()
                    dismiss()
                }
            }

            dialogNegativeButton.apply {
                text = negativeButtonTxt
                setOnClickListener {
                    onNegativeButtonClick()
                    dismiss()
                }
            }

            show()

        }


    }



}