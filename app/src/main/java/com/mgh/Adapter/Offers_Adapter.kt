package com.mgh.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mgh.Model.AllProducts_Response
import com.mgh.R
import com.mgh.SharedPrefManager
import com.mgh.View.DetailsProduct_id
import com.mgh.View.ProductDetails_View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_hero_product.view.*

class Offers_Adapter (context: Context, val userList: List<AllProducts_Response.Data.Data>)
    : RecyclerView.Adapter<Offers_Adapter.ViewHolder>() {
    lateinit var detailsProduct_id: DetailsProduct_id
    lateinit var ProductsDetails: ProductDetails_View
    var con=context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Offers_Adapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_hero_product, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: Offers_Adapter.ViewHolder, position: Int) {
        holder.bindItems(userList.get(position))
        holder.itemView.setOnClickListener(){
            ProductsDetails.Details(userList.get(position))
        }


        holder.Img_Favourit.setOnClickListener(){

            if (userList.get(position).isProductFavoirte==1){
                userList.get(position).isProductFavoirte==0
                holder.Img_Favourit.setImageResource(R.drawable.ic_emptyfavourit)
                detailsProduct_id.AddToFavourit("customer/removeFavorite",userList.get(position).id.toString())
            }else {
                userList.get(position).isProductFavoirte==1
                holder.Img_Favourit.setImageResource(R.drawable.ic_productfavourit)
                detailsProduct_id.AddToFavourit("customer/addFavorite",userList.get(position).id.toString())
            }
        }


    }
    fun onClickFavourit(ProductsDetail: DetailsProduct_id){
        this.detailsProduct_id=ProductsDetail
    }
    fun onClick(ProductsDetail: ProductDetails_View){
        this.ProductsDetails=ProductsDetail
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context =itemView.context
        val Img_Favourit = itemView.findViewById(R.id.Img_Favourit) as ImageView

        fun bindItems(dataModel: AllProducts_Response.Data.Data) {
            val title = itemView.findViewById(R.id.Title) as TextView
            val img = itemView.findViewById(R.id.Img_Product) as ImageView
            val RatingBar = itemView.findViewById(R.id.RatingBar) as RatingBar
            RatingBar.rating=dataModel.totalRate!!.toFloat()

            title.text = dataModel.name
                itemView.T_SalePrice.text = dataModel.total.toString()+" "+
                        dataModel.currency


            if (dataModel.isProductFavoirte==0){
                itemView.Img_Favourit.setImageResource(R.drawable.ic_emptyfavourit)
            }else {
                itemView.Img_Favourit.setImageResource(R.drawable.ic_productfavourit)
            }
            if (dataModel.productInCart==0){
                itemView.Img_Cartt.setImageResource(R.drawable.ic_emptycart)
            }else {
                itemView.Img_Cartt.setImageResource(R.drawable.ic_cart)
            }
                Picasso.get()
                    .load(dataModel.image)
                    .resize(500, 500)
                    .into(img)

        }
    }


}