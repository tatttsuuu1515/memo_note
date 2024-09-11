package com.example.memo_note.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var databases: DatabaseReference
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var oldtitle: String
    private lateinit var title: String
    private lateinit var content: String
    private lateinit var currentDate: String
    private var isFromMemoToroku :Boolean = false
    private var isFromMemoEdit :Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebaseのインスタンスを取得
        database = FirebaseDatabase.getInstance().reference
        databases = FirebaseDatabase.getInstance().reference
        title = args.title
        content = args.content
        currentDate = args.currentDate
        oldtitle = args.oldtitles
        isFromMemoToroku = args.isFromMemoToroku
        isFromMemoEdit = args.isFromMemoEdit

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

        binding.floatingActionButton2.setOnClickListener {
            load()
        }

        // Firebaseからデータを読み込み、UIに表示する
        load()


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

        if (isFromMemoEdit) {
            updateMemo(oldtitle, title, content)
            Handler(Looper.getMainLooper()).postDelayed({
                load()
            }, 1000) // 500ミリ秒遅延させる
            isFromMemoEdit = false
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
                    val editButton: Button = memoView.findViewById(R.id.editButton)

                    // メモ情報をセット
                    titleView.text = title
                    contentView.text = content
                    dateView.text = "作成日時：$date"

                    // 編集ボタンにクリックリスナーを設定
                    editButton.setOnClickListener {
                        // 編集ボタンがクリックされたときにも同じ処理を実行
                        val selectedTitle = titleView.text.toString()
                        val selectedContent = contentView.text.toString()
                        val navController = findNavController()
                        val action = HomeFragmentDirections.actionNavigationHomeToMemoEdit(selectedTitle,selectedContent)
                        navController.navigate(action)
                    }

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

    fun updateMemo(oldTitle: String, newTitle: String, newContent: String) {
        val database = FirebaseDatabase.getInstance().reference

        // 元のデータを取得
        val oldMemoRef = database.child("memos").child(oldTitle)
        oldMemoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // 元のデータを取得して、新しいノードにデータを移動する
                val oldContent = snapshot.child("content").getValue(String::class.java) ?: ""
                val oldDate = snapshot.child("date").getValue(String::class.java) ?: ""

                // 元のノードを削除
                oldMemoRef.removeValue().addOnSuccessListener {
                    // 古いノードの削除が成功した場合、新しいノードを作成する
                    val newMemoRef = database.child("memos").child(newTitle)
                    newMemoRef.child("content").setValue(newContent)

                    // 現在の日付と時刻を取得して保存
                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date()
                    )
                    newMemoRef.child("date").setValue(currentDate)
                }.addOnFailureListener {
                    // 古いノードの削除が失敗した場合のエラー
                    println("Error deleting old memo: ${it.message}")
                }
            } else {
                println("Memo with title '$oldTitle' does not exist.")
            }
        }.addOnFailureListener {
            // データ取得エラ
            println("Error getting data: ${it.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
