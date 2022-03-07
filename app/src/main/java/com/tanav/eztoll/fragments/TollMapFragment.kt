package com.tanav.eztoll.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.tanav.eztoll.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.tanav.eztoll.utilities.Utility


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TollMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var myContext: Context

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireActivity().applicationContext
        val myView =  inflater.inflate(R.layout.fragment_toll_map, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
            TollMapFragment().apply {
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

        var checkPointJsonArray = Utility.readCheckPointData(requireContext())
        var points: ArrayList<LatLng> = ArrayList()

        for (i in 0 until checkPointJsonArray.length()) {
            val checkPoint = checkPointJsonArray.getJSONObject(i)
            val lat = checkPoint.getDouble("lat")
            val lng = checkPoint.getDouble("lng")
            val position = LatLng(lat, lng)
            points.add(position)
        }
        var lineOptions = PolylineOptions()
        lineOptions.addAll(points);
        lineOptions.width(10F);
        lineOptions.color(Color.BLUE);

        //draw a line on Google map
        mMap.addPolyline(lineOptions);

        //show the middle track point of the toll road
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points[checkPointJsonArray.length()/2], 10.0f))
    }
}