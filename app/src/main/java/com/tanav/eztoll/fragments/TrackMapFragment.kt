package com.tanav.eztoll.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.tanav.eztoll.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tanav.eztoll.AppConst
import com.tanav.eztoll.database.*
import com.tanav.eztoll.utilities.Utility
import kotlinx.android.synthetic.main.fragment_track_map.*
import org.json.JSONArray
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.util.concurrent.Executors
import kotlin.math.roundToInt


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TrackMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var myContext: Context

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var trackingViewModel: TrackingViewModel
    private lateinit var chargesStatusViewModel: ChargesStatusViewModel
    private lateinit var chargesDetailsViewModel: ChargesDetailsViewModel

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var initLocation: Location

    private lateinit var mMap: GoogleMap
    private lateinit var myView: View
    private var checkPoints: ArrayList<LatLng> = ArrayList()
    private lateinit var  tollPolyline: Polyline
    private var routePolyline: Polyline? = null

    private var isShowingTollRoads: Boolean = false
    private lateinit var btnChooseDate: Button
    private lateinit var btnToggleTollRoads: Button
    private lateinit var txtMessage: TextView
    private lateinit var txtMessageHidden: TextView
    private lateinit var fabCalculate: FloatingActionButton
    private lateinit var majorLayout: ConstraintLayout
    private lateinit var progressbar: ProgressBar

    private var targetDate: Int = 20220101      //initialized to today's date onCreate()
    private var initialDate: Int = 20220101      //initialized to today's date onCreate()

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var isRouteInfoExists: Boolean = false
    private var workDateTrackingList: List<TrackingModel>? = null
    private lateinit var checkPointJsonArray: JSONArray
    private var tempCharges = HashMap<Int, String>()

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        trackingViewModel = ViewModelProvider(this).get(TrackingViewModel::class.java)
        chargesStatusViewModel = ViewModelProvider(this).get(ChargesStatusViewModel::class.java)
        chargesDetailsViewModel = ViewModelProvider(this).get(ChargesDetailsViewModel::class.java)

        //initialize the toll check points
        checkPointJsonArray = Utility.readCheckPointData(requireContext())
        for (i in 0 until checkPointJsonArray.length()) {
            val checkPoint = checkPointJsonArray.getJSONObject(i)
            val lat = checkPoint.getDouble("lat")
            val lng = checkPoint.getDouble("lng")
            val position = LatLng(lat, lng)
            checkPoints.add(position)
        }

        //get the last know gps location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    initLocation = location
                }
            }

        //date picker to choose the route of date
        dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                targetDate = (year * 10000) + ((monthOfYear + 1) * 100) + (dayOfMonth)
                fabCalculate.isEnabled = false
                updateMapView()
            }
        initialDate = Utility.todayInInt()
        targetDate = initialDate

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireActivity().applicationContext
        myView =  inflater.inflate(R.layout.fragment_track_map, container, false)

        majorLayout = myView.findViewById(R.id.major_layout)
        progressbar = myView.findViewById(R.id.progressbar)
        btnChooseDate = myView.findViewById(R.id.btn_choose_date)
        btnToggleTollRoads = myView.findViewById(R.id.btn_toggle_toll_route)
        txtMessage = myView.findViewById(R.id.txt_message)
        txtMessageHidden = myView.findViewById(R.id.txt_message_hidden)
        fabCalculate = myView.findViewById(R.id.fab_calculate)
        fabCalculate.isEnabled = false

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnChooseDate.setOnClickListener{
            onClickChooseDate(it)
        }

        btnToggleTollRoads.setOnClickListener{
            onClickToggleTollRoads(it)
        }

        txtMessage.setOnClickListener {
            txtMessage.visibility = View.GONE
            txtMessageHidden.visibility = View.VISIBLE
        }

        txtMessageHidden.setOnClickListener {
            txtMessageHidden.visibility = View.GONE
            txtMessage.visibility = View.VISIBLE
        }

        fabCalculate.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            majorLayout.visibility = View.GONE
            calculateDayCharges()
        }
        return myView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        updateMapView()
    }

    private fun updateMapView() {
        Log.d("sch", "TrackMapFragment, updateMapView(), targetDate=$targetDate")

        if (targetDate == initialDate) {
            btn_choose_date.text = getString(R.string.btn_today)
        } else {
            val theYear = (targetDate/10000).toInt()
            val theMonth = ((targetDate - theYear * 10000) /100).toInt()
            val theDay = (targetDate - theYear * 10000 - theMonth * 100).toInt()
            btn_choose_date.text = getString(R.string.btn_chosen_date, theYear, theMonth, theDay)
        }
        showRoute(targetDate)
    }

    private fun showRoute(workDate: Int) {
        //clear rout lines of the previous draw if any
        routePolyline?.remove()

        var points: ArrayList<LatLng> = ArrayList()
        // read from room db all gps records of the workDate
        trackingViewModel.getTrackingByDate(myContext, workDate)!!.observe(this, {
            //Log.d("sch", "TrackMapFragment, DB list = $it")
            if (it == null || it.size <= 1) {
                Log.d("sch", "TrackMapFragment, No DB record")
                isRouteInfoExists = false
                workDateTrackingList = null
                centerMapOnMyLocation()
            } else {
                Log.d("sch", "TrackMapFragment, DB tracking record found")
                isRouteInfoExists = true
                workDateTrackingList = it
                for (pt: TrackingModel in it) {
                    val lat: Double = pt.lat
                    val lng: Double = pt.lng
                    //Log.d("sch", "TrackMapFragment, db record location =$lat,$lng")
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                var lineOptions = PolylineOptions()
                lineOptions.addAll(points)
                lineOptions.width(5F)
                lineOptions.color(Color.RED)

                //draw a line on Google map
                routePolyline = mMap.addPolyline(lineOptions)

                //show the final track point of the toll road
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points[it.size-1], 12.0f))
            }
            showSystemMessage()
        })
    }

    //    @SuppressLint("MissingPermission")
    private fun centerMapOnMyLocation() {
        Log.d("sch", "TrackMapFragment, centerMapOnMyLocation()")
        val myLatLng = LatLng(initLocation.latitude, initLocation.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14.0f))
    }

    private fun onClickToggleTollRoads(view: View?) {
        Log.d("sch", "TrackMapFragment, onClickToggleTollRoads()")
        isShowingTollRoads = !isShowingTollRoads
        if (isShowingTollRoads) {
            btnToggleTollRoads.text = getString(R.string.btn_show_toll_route)
            showTollRoads()
        } else {
            btnToggleTollRoads.text = getString(R.string.btn_hide_toll_route)
            hideTollRoads()
        }
    }

    private fun showTollRoads() {
        var lineOptions = PolylineOptions()
        lineOptions.addAll(checkPoints)
        lineOptions.width(10F)
        lineOptions.color(Color.BLUE)

        //draw a line on Google map
        tollPolyline = mMap.addPolyline(lineOptions)
    }
    private fun hideTollRoads() {
        tollPolyline?.remove()
    }

    private fun onClickChooseDate(view: View?) {
        val theYear = (targetDate/10000).toInt()
        var theMonth = ((targetDate - theYear * 10000) /100).toInt()
        val theDay = (targetDate - theYear * 10000 - theMonth * 100).toInt()
        Log.d("sch", "TrackMapFragment, onClickChooseDate(), $theYear-$theMonth-$theDay")
        //adjust to base 0 for the month
        theMonth--
        val dialog = DatePickerDialog(requireActivity(), dateSetListener, theYear, theMonth, theDay)
        dialog.datePicker.maxDate = System.currentTimeMillis()      //max day is today
        dialog.show()
    }

    private fun showSystemMessage() {
        Log.d("sch", "TrackMapFragment, showSystemMessage(), targetDate:$targetDate")

        txtMessage.text = getString(R.string.msg_loading)
        //observer and observe() used to work with live-data
        chargesStatusViewModel.getChargesStatusByDate(myContext, targetDate)!!.observe(this, Observer {
            var isChargesInfoFound: Boolean = true
            if (it == null) {
                //no record in the status table
                isChargesInfoFound = false
            }
            fabCalculate.isEnabled = (!isChargesInfoFound) && isRouteInfoExists

            if (!isRouteInfoExists) {
                if (targetDate == initialDate)
                    txtMessage.text = getString(R.string.msg_no_route_info_today)
                else
                    txtMessage.text = getString(R.string.msg_no_route_info)
            } else if (isChargesInfoFound) {
                txtMessage.text = getString(R.string.msg_mileages, it?.totalAmount, it?.totalKm )
            } else if(tempCharges[targetDate] != null) {
                txtMessage.text = tempCharges[targetDate]
            } else {
                txtMessage.text = getString(R.string.msg_can_refresh)
            }
            Log.d("sch", "TrackMapFragment, getChargesStatusByDate called")
            Log.d("sch", "TrackMapFragment, totalKm:" + it?.totalKm + ", totalAmount:" + it?.totalAmount)
            Log.d("sch", "TrackMapFragment, isRouteInfoExists:$isRouteInfoExists")
        })
    }

    private fun calculateDayCharges() {
        myExecutor.execute {
            //sleep(1000)
            val chargesDetailsList = Utility.findDailyChargesDetails(myContext, targetDate, checkPointJsonArray)
            myHandler.post {
                setAdhocMsg(chargesDetailsList)
                progressbar.visibility = View.GONE
                majorLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdhocMsg(cdList: List<ChargesDetailsModel>) {
        var totalMeters = 0F
        for (cd in cdList) {
            totalMeters += cd.meters
        }
        var totalCharges = (AppConst.UNIT_CHARGES_PER_METER * totalMeters).toFloat()
        if (totalCharges > 0) {
            tempCharges[targetDate] = getString(R.string.msg_est_charges, totalCharges, totalMeters/1000)
        } else {
            tempCharges[targetDate] = getString(R.string.msg_est_charges_zero)
        }
        showSystemMessage()
    }
}