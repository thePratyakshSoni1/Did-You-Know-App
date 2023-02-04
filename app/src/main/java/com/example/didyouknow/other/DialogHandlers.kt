package com.example.didyouknow.other

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.didyouknow.R

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
                        dialogText.text = dialogSuccessTxt
                    }

                    Status.ERROR ->{
                        dialogImg.visibility = View.VISIBLE
                        dialogImg.setImageResource(R.drawable.ic_error_round)
                        actionBtn.visibility = View.VISIBLE
                        actionBtn.setBackgroundColor(Color.parseColor("#FF3636"))
                        actionBtn.text = "Back"
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

    fun showSuccessFailureDialogue( isSuccess:Boolean, diaogText:String, hasActionButton:Boolean, onActionButtonClick:()->Unit = { Unit }){

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

            if(hasActionButton){
                findViewById<Button>(R.id.dialogActionButton).apply {
                    setOnClickListener {
                        onActionButtonClick()
                        dismiss()
                    }

                    visibility = if( hasActionButton ) View.VISIBLE else View.GONE

                }
            }

            findViewById<ImageView>(R.id.dialogImage).apply {
                setImageResource(
                    if(isSuccess) R.drawable.ic_check_filled else R.drawable.ic_error_round
                )
            }

            findViewById<TextView>(R.id.dialogText).setText(
                diaogText
            )
            show()

        }


    }



}