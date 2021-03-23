package com.example.tryingsomething.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tryingsomething.dto.Another
import com.example.tryingsomething.dto.Runner
import com.example.tryingsomething.dto.ControlPoint
import com.example.tryingsomething.service.RunnerService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainViewModel : ViewModel() {
    private var _plants: MutableLiveData<ArrayList<Another>> = MutableLiveData<ArrayList<Another>>()
    internal var plants: MutableLiveData<ArrayList<Another>>
        get() {return _plants}
        set(value) { _plants = value}
    var runnerService: RunnerService = RunnerService()
    private var firestore: FirebaseFirestore
    private var _controlPoints: MutableLiveData<ArrayList<ControlPoint>> = MutableLiveData<ArrayList<ControlPoint>>()
    internal var controlPoints: MutableLiveData<ArrayList<ControlPoint>>
    get() {return _controlPoints}
    set(value) {_controlPoints = value}
    private var _controlPoint = ControlPoint()
    internal var controlPoint: ControlPoint
        get() {return _controlPoint}
        set(value) {_controlPoint = value}

    init {
        fetchRunners("e")
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenForChangesInCloud()
    }

    private fun listenForChangesInCloud() {
        firestore.collection("specimens").addSnapshotListener{
            snapshot, e ->
            //if we have exeption we want to skip
             if (e != null){ Log.d("Firebase", "FAIL$e")
             return@addSnapshotListener
             }
            //it we are here exeption not ocured
            if (snapshot != null){
                val allSpeciments = ArrayList<ControlPoint>()
                val documents = snapshot.documents
                documents.forEach {
                    val controlPoint = it.toObject(ControlPoint::class.java)
                    if (controlPoint != null){
                        controlPoint.cpId = it.id
                        allSpeciments.add(controlPoint)
                    }
                }
            _controlPoints.value = allSpeciments
            }
        }
    }

    fun fetchRunners(runnerName: String) {
        _plants = runnerService.fetchRunners(runnerName)
    }

    fun save(controlPoint: ControlPoint, user: FirebaseUser, runners: ArrayList<Runner>) {
        val userEmail = user.displayName
    val document  = firestore.collection("specimens").document(userEmail?:"empty_name")
        document.id
        controlPoint.cpId = document.id
           val set = document.set(controlPoint)
            set.addOnSuccessListener {
                Log.d("Firebase", "document saved")
                if(runners != null && runners.size > 0){
                    saveRunners(controlPoint, runners, user)
                }
            }
            set.addOnFailureListener {
                Log.d("Firebase", "document NOT saved")
            }
    }

    private fun saveRunners(specimen: ControlPoint, runners: ArrayList<Runner>, user: FirebaseUser) {
       val collection =  firestore.collection("specimens").document(user.displayName ?: "")
                .collection("runners")
                runners.forEach {
                    runner -> val task = collection.add(runner)
                    task.addOnSuccessListener {
                        runner.id = it.id
                    }
                }
    }
    // TODO: Implement the ViewModel
}