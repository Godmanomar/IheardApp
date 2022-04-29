package com.novusvista.omar.iheardapp.Interface

interface EditImageFragmentListener {
    fun onBrightnessChanged(brightness:Int)
    fun onSaturationChanged(saturation:Float)
    fun onConstrantChanged(contrast:Float)
    fun onEditStarted()
    fun onEditCompleted()
}