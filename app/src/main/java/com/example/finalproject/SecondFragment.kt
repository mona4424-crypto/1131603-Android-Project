package com.example.finalproject

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var fqPermission = true
    lateinit var picURI: Uri
    var currentSelectedMode = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private val requestCameraPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "相機權限已開啟！準備拍照", Toast.LENGTH_SHORT).show()
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(requireContext(), "需要取得照相機權限才能拍照喔！", Toast.LENGTH_SHORT).show()
        }
    }
    private val takePictureLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            view?.findViewById<ImageView>(R.id.imageView)?.setImageBitmap(bitmap)
        } else {
            Toast.makeText(requireContext(), "你取消了拍照喔", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tb = view.findViewById<Toolbar>(R.id.toolbar)

        val modeDataList = arrayOf(
            ModeData(R.drawable.banana, "香蕉模式(簡單)"),
            ModeData(R.drawable.apple, "蘋果模式(簡單的相反)")
        )

        val listViewMode = view.findViewById<ListView>(R.id.listViewMode)
        val resultTextView = view.findViewById<TextView>(R.id.textView16)

        val modeAdapter = ModeAdapter(requireContext(), modeDataList)
        listViewMode.adapter = modeAdapter
        modeAdapter.setSelectedIndex(0)
        currentSelectedMode = modeDataList[0].name
        resultTextView.text = "你選擇了: $currentSelectedMode"

        listViewMode.setOnItemClickListener { _, _, position, _ ->
            modeAdapter.setSelectedIndex(position)
            currentSelectedMode = modeDataList[position].name
            resultTextView.text = "你選擇了: $currentSelectedMode"
        }

        //////////////////開始按鈕 //////////////---
        view.findViewById<Button>(R.id.button7).setOnClickListener {

            val nameInput = view.findViewById<EditText>(R.id.editTextText).text.toString()
            val ageInput = view.findViewById<EditText>(R.id.editTextText2).text.toString()

            if (nameInput.isEmpty() || ageInput.isEmpty()) {
                Toast.makeText(requireContext(), "請輸入完整資料", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), TypingActivity::class.java)

            intent.putExtra("PLAYER_NAME", nameInput)
            intent.putExtra("PLAYER_AGE", ageInput)
            intent.putExtra("GAME_MODE", currentSelectedMode)
            startActivity(intent)
        }


        val permission = checkPermissions()
        val reqPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            it.forEach { _, isGranted ->
                if (!isGranted) fqPermission = false
            }
        }
        if (permission.isNotEmpty()) {
            reqPermission.launch(permission.toTypedArray())
        }

        val getPicture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                view.findViewById<ImageView>(R.id.imageView).setImageURI(picURI)
            }
        }

        view.findViewById<Button>(R.id.button).setOnClickListener {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "準備開啟相機...", Toast.LENGTH_SHORT).show()
                takePictureLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
        //點擊輸入資料按鈕
        view.findViewById<Button>(R.id.button2).setOnClickListener {
            //取得名字與年齡
            val nameInput = view.findViewById<EditText>(R.id.editTextText).text.toString()
            val ageInput = view.findViewById<EditText>(R.id.editTextText2).text.toString()

            if (nameInput.isEmpty() || ageInput.isEmpty()) {
                Toast.makeText(requireContext(), "未輸入完整資料", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val confirmMessage = "確認輸入資料如下：\n名字：$nameInput\n年齡：$ageInput 歲"
            val alDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            alDialog.setTitle("確認訊息")
            alDialog.setMessage(confirmMessage)
            alDialog.setIcon(R.drawable.inform)

            alDialog.setPositiveButton("確定", null)
            alDialog.setNegativeButton("重新輸入") { _, _ ->
                view.findViewById<EditText>(R.id.editTextText).text.clear()
                view.findViewById<EditText>(R.id.editTextText2).text.clear()
            }
            alDialog.show()
        }

        tb.inflateMenu(R.menu.menu_main)
        tb.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item1 -> {
                    val memeImage = view.findViewById<ImageView>(R.id.imageView2)
                    memeImage.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }
    private fun checkPermissions(): ArrayList<String> {
        val permissions = ArrayList<String>()
        val camera = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        val writeStorage = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (camera != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (writeStorage != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}