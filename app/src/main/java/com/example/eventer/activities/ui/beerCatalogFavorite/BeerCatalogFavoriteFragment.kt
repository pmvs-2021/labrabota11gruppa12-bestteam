package com.example.eventer.activities.ui.eventerCatalogFavorite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eventerproject.R
import com.example.eventerproject.activities.ui.eventerCatalogOnline.eventerInfoList.eventerInfoAdapter
import com.example.eventerproject.database.DataBase
import java.io.ByteArrayOutputStream
import java.util.ArrayList


class eventerCatalogFavoriteFragment : Fragment() {

    private lateinit var eventerCatalogFavoriteViewModel: eventerCatalogFavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventerCatalogFavoriteViewModel =
            ViewModelProvider(this).get(eventerCatalogFavoriteViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_eventer_catalog_favorite, container, false)

        var infoes = loadeventerInfo()

        var adapter = eventerInfoAdapter(infoes, requireContext());

        var listView = root.findViewById<ListView>(R.id.eventerList);

        listView.adapter = adapter;

        adapter

        listView.setOnItemClickListener { parent, view, position, id ->

            var item = adapter.infoes!![position];

            item.isExpanded = !item.isExpanded

            adapter.notifyDataSetChanged()

        }

        listView.setOnItemLongClickListener { parent, view, position, id ->
            removeeventerInfo(adapter.infoes!![position])

            infoes.removeAt(position)

            adapter.notifyDataSetChanged()

            true
        }

        return root
    }

    public fun loadeventerInfo(): ArrayList<eventerInfoAdapter.eventerInfo> {

        var result = ArrayList<eventerInfoAdapter.eventerInfo>()

        var database = DataBase(context)

        var cursor =
            database.readableDatabase.rawQuery("SELECT * FROM " + database.TABLE_eventer, null)

        if (cursor.moveToFirst()) {

            do {

                var name = cursor.getString(cursor.getColumnIndex("name"))

                var description = cursor.getString(cursor.getColumnIndex("description"))

                var photoBytes = cursor.getBlob(cursor.getColumnIndex("photo"))

                var photo = BitmapDrawable(
                    getResources(),
                    BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                )

                result.add(eventerInfoAdapter.eventerInfo(name, photo, description))


            } while (cursor.moveToNext())
        }

        return result;

    }

    public fun removeeventerInfo(info: eventerInfoAdapter.eventerInfo) {

        var database = DataBase(context)

        var querryStr =
            "DELETE FROM " + database.TABLE_eventer + " WHERE name = ? AND description = ? "

        var querry = database.writableDatabase.compileStatement(querryStr);

        querry.bindString(1, info.title);
        querry.bindString(2, info.description);

        if (querry.executeInsert() < 0) {
            System.out.println("Failed to delete")

            Toast.makeText(context, "Failed to remove eventer info", Toast.LENGTH_SHORT).show()
        } else {
            System.out.println("Deleted succesfully")

            Toast.makeText(context, "Removed eventer info", Toast.LENGTH_SHORT).show()
        }

    }

    public fun getDrawableBytes(d: Drawable): ByteArray {

        val bitmap = (d as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitmapdata: ByteArray = stream.toByteArray()

        return bitmapdata
    }
}