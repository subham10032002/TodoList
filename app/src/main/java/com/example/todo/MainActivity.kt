package com.example.todo


import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy{
        AppDatabase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        floatingButton.setOnClickListener{

            startActivity(Intent(this, TaskActivity::class.java))

        }
        todoRV.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        initSwipe()

        db.todoDao().getTask().observe(this, Observer {
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                adapter.notifyDataSetChanged()
            }

        })



    }

    fun initSwipe(){
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if(direction == ItemTouchHelper.LEFT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                }
                else if(direction == ItemTouchHelper.RIGHT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView

                   val paint = Paint()
                    var icon:Bitmap

                    if(dX > 0){ // moving on x direction means going from left to right

//                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_add_24)?.toBitmap()
//
//
//                        icon = BitmapFactory.decodeResource(resources,R.drawable.ic_baseline_add_24,)

                        paint.color = Color.parseColor("#388E3C")
                        c.drawRect(
                            itemView.left.toFloat(),itemView.top.toFloat(),
                            itemView.left.toFloat() + dX , itemView.bottom.toFloat(),paint
                        )

//                        c.drawBitmap(
//                            icon,
//                            itemView.left.toFloat() ,
//                            itemView.top.toFloat() + (itemView.bottom.toFloat() -
//                                    itemView.top.toFloat() -
//                                    icon.height.toFloat()) /  2 ,
//                            paint
//                        )
                    }
                    else {
//                        ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_add_24)?.toBitmap()
//                        icon = BitmapFactory.decodeResource(resources,R.drawable.ic_baseline_add_24,null)

                        paint.color = Color.parseColor("#D32F2F")
                        c.drawRect(
                            itemView.right.toFloat() + dX ,itemView.top.toFloat(),
                            itemView.right.toFloat()  , itemView.bottom.toFloat(),paint
                        )

//                        c.drawBitmap(
//                            icon,
//                            itemView.right.toFloat() - icon.width,
//                            itemView.top.toFloat() + (itemView.bottom.toFloat() -
//                                    itemView.top.toFloat() -
//                                    icon.height.toFloat())/2,
//                            paint
//                        )
                    }
                    viewHolder.itemView.translationX = dX
                }
                 else {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }



        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(todoRV)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                displayTask()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                displayTask()
                return true
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTask(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun displayTask(newText: String="") {
       db.todoDao().getTask().observe(this, Observer {
           if(it.isNotEmpty()){
               list.clear()
               list.addAll(
                   it.filter {
                       todo -> todo.title.contains(newText,true)
                   }
               )
               adapter.notifyDataSetChanged()
           }
       })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when(item.itemId){
             R.id.history ->{
                 startActivity(Intent(this,HistoryActivity::class.java))
             }
         }
        return super.onOptionsItemSelected(item)
    }


}




