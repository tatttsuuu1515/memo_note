package com.example.memo_note.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.memo_note.R
import com.example.memo_note.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var databases: DatabaseReference
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var currentDate: String
    private var isFromMemoToroku :Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebaseのインスタンスを取得
        database = FirebaseDatabase.getInstance().reference
        databases = FirebaseDatabase.getInstance().reference
        title = args.title
        content = args.content
        currentDate = args.currentDate

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseDatabase.getInstance().reference.child("memos")


        binding.floatingActionButton.setOnClickListener {
            val navigation = findNavController()
            navigation.navigate(R.id.action_navigation_home_to_memo_toroku)
        }


        // Firebaseからデータを読み込み、UIに表示する
        load()
        isFromMemoToroku = args.isFromMemoToroku

        // 画面遷移時に渡された引数がtrueの場合に処理を実行
        if (isFromMemoToroku) {
            // メモ内容と日付をMapに格納
            val memoDetails = mapOf(
                "content" to content,
                "date" to currentDate
            )
            // タイトルを親ノードとしてデータを保存
            databases.child("memos").child(title).setValue(memoDetails)
            load()
            isFromMemoToroku = false
        }

        return root
    }


    private fun load() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val linearLayout: LinearLayout = binding.scrollview.getChildAt(0) as LinearLayout
                linearLayout.removeAllViews()

                for (memoSnapshot in snapshot.children) {
                    val title = memoSnapshot.key ?: ""
                    val content = memoSnapshot.child("content").getValue(String::class.java) ?: ""
                    val date = memoSnapshot.child("date").getValue(String::class.java) ?: ""

                    // メモビューの作成
                    val memoView = LayoutInflater.from(context).inflate(R.layout.memo_item, linearLayout, false)
                    val titleView: TextView = memoView.findViewById(R.id.memoTitle)
                    val contentView: TextView = memoView.findViewById(R.id.memoContent)
                    val dateView: TextView = memoView.findViewById(R.id.memoDate)
                    val deleteButton: ImageButton = memoView.findViewById(R.id.deleteButton)

                    // メモ情報をセット
                    titleView.text = title
                    contentView.text = content
                    dateView.text = "作成日時：$date"

                    // 削除ボタンのクリックリスナーを設定
                    deleteButton.setOnClickListener {
                        // Firebase Realtime Databaseからメモを削除
                        database.child(title).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // 削除成功時の処理
                                load()
                            } else {
                                // エラーハンドリング
                            }
                        }
                    }

                    // LinearLayoutにメモビューを追加
                    linearLayout.addView(memoView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // エラーハンドリング
            }
        })
    }


    /*
    private fun loadMemos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val linearLayout: LinearLayout = binding.scrollview.getChildAt(0) as LinearLayout
                linearLayout.removeAllViews()

                for (memoSnapshot in snapshot.children) {
                    val title = memoSnapshot.key ?: ""
                    val content = memoSnapshot.child("content").getValue(String::class.java) ?: ""
                    val date = memoSnapshot.child("date").getValue(String::class.java) ?: ""

                    // メモビューの作成
                    val memoView = LayoutInflater.from(context).inflate(R.layout.memo_item, linearLayout, false)
                    val titleView: TextView = memoView.findViewById(R.id.memoTitle)
                    val contentView: TextView = memoView.findViewById(R.id.memoContent)
                    val dateView: TextView = memoView.findViewById(R.id.memoDate)
                    val deleteButton: Button = memoView.findViewById(R.id.deleteButton)

                    // メモ情報をセット
                    titleView.text = title
                    contentView.text = content
                    dateView.text = date

                    // 削除ボタンのクリックリスナーを設定
                    deleteButton.setOnClickListener {
                        // Firebase Realtime Databaseからメモを削除
                        database.child(title).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // 削除成功時の処理（必要なら）
                            } else {
                                // エラーハンドリング
                            }
                        }
                    }

                    // LinearLayoutにメモビューを追加
                    linearLayout.addView(memoView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // エラーハンドリング
            }
        })
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
