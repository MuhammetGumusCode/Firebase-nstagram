package com.muhammetgumus.kotlincookbook.adapter


import android.R.attr.text
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.muhammetgumus.photosharing.Post
import com.muhammetgumus.photosharing.databinding.RecyclerRowBinding
import com.squareup.picasso.Picasso


class ListAdapter(val ListeList: ArrayList<Post>) : RecyclerView.Adapter<ListAdapter.PostHolder>() {

    // ArtHolder sınıfınız zaten doğruydu, bir değişiklik gerekmedi.
    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }



    

    // 2. onCreateViewHolder: Her bir satırın layout'unu (recycler_row.xml) bağlar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)

    }





    // 3. onBindViewHolder: Her bir satırın içeriğini doldurur.
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerRowcTextcomment.text = ListeList.get(position).comment
        holder.binding.recyclerRowTextView.text = ListeList.get(position).userEmail


        Picasso.get().load(ListeList.get(position).downloadUrl).into(holder.binding.recyclerRowImageview)









    }



    // 4. getItemCount: Listenin toplam eleman sayısını döndürür.
    override fun getItemCount(): Int {
        return ListeList.size
    }
}