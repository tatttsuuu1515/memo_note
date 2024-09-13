package com.example.memo_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.memo_note.databinding.FragmentMemoTorokuBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [memo_toroku.newInstance] factory method to
 * create an instance of this fragment.
 */
class memo_toroku : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    // Firebase Databaseのリファレンスを宣言
    private lateinit var database: DatabaseReference

    // bindhing宣言
    private var _binding: FragmentMemoTorokuBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebaseのインスタンスを取得
        database = FirebaseDatabase.getInstance().reference
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemoTorokuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.enterButton.setOnClickListener {

            // それぞれ変数に格納
            val title = binding.memoTitle.text.toString()
            val content = binding.memoNote.text.toString()

            // 現在の日付を取得
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())


            // 画面遷移
            val navController = findNavController()
            val action = memo_torokuDirections.actionMemoTorokuToNavigationHome(title,content,currentDate,"",true,false)
            navController.navigate(action)

        }




        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            memo_toroku().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}