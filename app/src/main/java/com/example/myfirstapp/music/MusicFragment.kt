package com.example.myfirstapp.music

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Modelclass
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.RecyclerUtils.withLinearAdapter
import com.example.myfirstapp.databinding.FragmentMusicBinding
import com.example.myfirstapp.extra.isMyServiceRunning
import com.example.myfirstapp.extra.toggleVisibility
import com.example.myfirstapp.music.adapter.MusicAdapter
import com.example.myfirstapp.music.helper.MusicHelper
import com.example.myfirstapp.notifications.MyService_Music
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MusicModel(
    var id: Long? = -1,
    var path: String? = null,
    var name: String? = null,
    var album: String? = null,
    var artist: String? = null,
    var duration: String? = null,
)

class MusicFragment : Fragment() {
    lateinit var binding: FragmentMusicBinding
    lateinit var adapter: MusicAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    var mService: MyService_Music? = null
    var mIsBound: Boolean? = null
    var firstLaunch = true
    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService_Music.MyBinder
            mService = binder.service
            mIsBound = true
            Log.e(Myconstants.TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIsBound = false
            Log.e(Myconstants.TAG, "onServiceDisconnected: ")
        }
    }
    companion object{
        lateinit var model: Modelclass
        fun MusicFragmentWithParams(model: Modelclass) : MusicFragment{
            this.model  = model
            return MusicFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? { binding = FragmentMusicBinding.inflate(layoutInflater); return binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.sheet.sheet)
        adapter = MusicAdapter(ArrayList())
        binding.mRecycler.withLinearAdapter(adapter)


        lifecycleScope.launch {
            progress_bar.toggleVisibility()
            delay(500)
            adapter.updateAdapter(MusicHelper.getAllAudioFromDevice(requireActivity()))
            progress_bar.toggleVisibility()
        }


        adapter.onItemClick = { position, model ->
            if (firstLaunch){
                firstLaunch=false
                val intent = Intent(requireActivity(),MyService_Music::class.java)
                requireActivity().startService(intent)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            mService?.playSong(model.id!!,model)
            changePlayPauseIcon(true)
            binding.sheet.title.text = model.name

        }

        addListeners()


        binding.swap.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                binding.swap.setImageResource(R.drawable.swipe)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else{
                binding.swap.setImageResource(R.drawable.swipe_down)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun addListeners() {
        binding.sheet.sheet.play_pause.setOnClickListener {
            if (mService?.isPlaying()!!){
                mService?.pausePlay()
                changePlayPauseIcon(false)
            } else {
                changePlayPauseIcon(true)
                mService?.resumePlay()
            }
        }
    }

    private fun changePlayPauseIcon(playing:Boolean){
        if (playing){
            binding.sheet.sheet.play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }else{
            binding.sheet.sheet.play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }


    //Connection to the Service
    private fun doBindService() {
        val intent = Intent(requireActivity(), MyService_Music::class.java)
        requireActivity().bindService(intent, serviceConnection,
            AppCompatActivity.BIND_AUTO_CREATE)
    }

    private fun unBindService(){
        val intent = Intent(requireActivity(), MyService_Music::class.java)
        requireActivity().unbindService(serviceConnection)
        requireActivity().stopService(intent)
    }

    override fun onResume() {
        if (!requireActivity().isMyServiceRunning(MyService_Music::class.java)){
            doBindService()
        }
        super.onResume()
    }

    override fun onDestroy() {

        mIsBound?.let {
            if (it and !mService?.isPlaying()!!){
                Log.e("//", "onStop: stopservice" )
                unBindService()
            }
        }
        super.onDestroy()
    }
}