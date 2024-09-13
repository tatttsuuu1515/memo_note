package com.example.memo_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.memo_note.databinding.FragmentMemoEditBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class memo_edit : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // 画面遷移情報
    private val args: memo_editArgs by navArgs()
    private lateinit var oldtitle: String
    private lateinit var title: String
    private lateinit var oldcontent: String
    private lateinit var content: String
    private lateinit var currentDate: String

    // bindhing宣言
    private var _binding: FragmentMemoEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        oldtitle = args.oldtitle
        oldcontent = args.oldcontent


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemoEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 編集前データを入力欄に表示
        binding.memoTitleEdit.setText(oldtitle)
        binding.memoNoteEdit.setText(oldcontent)

        // 変更の確定ボタンの動作
        binding.enterButtonEdit.setOnClickListener {

            // 編集内容読み取り
            title = binding.memoTitleEdit.text.toString()
            content = binding.memoNoteEdit.text.toString()
            // 現在の日付を取得
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            // 画面遷移
            val navController = findNavController()
            val action = memo_editDirections.actionMemoEditToNavigationHome(title,content,currentDate, oldtitle,false,true)
            navController.navigate(action)

        }





        return root
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            memo_edit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}