package com.gazr.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gazr.Activities.Login
import com.gazr.Activities.NavigationDrawer.Companion.drawerLayout
import com.gazr.Adapter.*
import com.gazr.ChangeLanguage
import com.gazr.Model.*
import com.gazr.R
import com.gazr.View.DetailsArticatal_View
import com.gazr.View.DetailsProduct_id
import com.gazr.View.ProductBytUd_View
import com.gazr.View.ProductDetails_View
import com.gazr.ViewModel.*
import com.gazr.utils.Loading
import com.google.firebase.iid.FirebaseInstanceId
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.recycler_Categroies
import kotlinx.android.synthetic.main.fragment_offers__details.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, DetailsProduct_id,
    ProductDetails_View , DetailsArticatal_View, ProductBytUd_View {


    //    lateinit var allproducts: Cart_ViewModel
    var toolbarhome: Toolbar? = null
    private lateinit var DataSaver: SharedPreferences
    var swipeTimer: Timer? = null
    val handler = Handler()

    private var tokenfirebae:String?= String()
    companion object {
         var UserToken:String?= String()
    }
    lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        DataSaver = PreferenceManager.getDefaultSharedPreferences(requireContext())
        UserToken=DataSaver.getString("token", null)

        SwipRefresh()
        openAllBestSalling()
        SearchKeyBoard()
        openAllCategories()

        return root
    }

    private fun getTokenn() {
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnSuccessListener(requireActivity(), { instanceIdResult ->
                tokenfirebae = instanceIdResult.token
                SentToken()
            })
    }

    private fun SentToken() {
        val tokenn = ViewModelProvider.NewInstanceFactory().create(SentToken_ViewModel::class.java)
        context?.let {
            tokenn.Requests(tokenfirebae,DataSaver.getString("token",null)!!, it)?.observe(viewLifecycleOwner, Observer<SentMessage_Response> { loginmodel ->
                if(loginmodel!=null) {

                }
            })
        }
    }

    private fun openAllBestSalling() {
        root.T_SeeBestSall.setOnClickListener() {
            var productsByid = AllFilterProducts()
            val bundle = Bundle()
            bundle.putString("type", "all")
            bundle.putString("name", null)
            bundle.putString("link", "bestSelling")
            productsByid.arguments = bundle
            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_allFilterProducts,bundle);
        }
    }

    fun getArticatles(){
        var allproducts: Articatles_ViewModel =  ViewModelProvider.NewInstanceFactory().create(
            Articatles_ViewModel::class.java)
        requireContext()?.let {
            allproducts.getCategories("blogs",ChangeLanguage.getLanguage(it), it).observe(viewLifecycleOwner,
                Observer<Blogs_Response> { loginmodel ->
                    if(loginmodel!=null) {
                        var listAdapter =
                            Articatles_Adapter(loginmodel.data as List<Blogs_Response.Data>)
                        listAdapter.onClick(this)
                        root.recycler_Blog.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false )
                        root.recycler_Blog.setAdapter(listAdapter)
                    }
                })
        }
    }

    private fun openAllCategories() {
        root.T_SeeCategorues.setOnClickListener() {
            var productsByid = Menu_Categories()
            val bundle = Bundle()
            productsByid.arguments = bundle
            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_T_Categories,bundle);

        }
    }






    fun SwipRefresh() {
        root.SwipHome.setOnRefreshListener(this)
        root.SwipHome.isRefreshing = true
        root.SwipHome.isEnabled = true
        root.SwipHome.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
        root.SwipHome.post(Runnable {
            getSlider()
            getDealsProducts()
            getBestSalling()
            getAllCategories()
            getArticatles()

        })
    }

    fun getAllCategories() {
        var categories: Categories_ViewModel = ViewModelProvider.NewInstanceFactory().create(
            Categories_ViewModel::class.java
        )
        requireContext()?.let {
            categories.getCategories("1",ChangeLanguage.getLanguage(it), it)
                .observe(viewLifecycleOwner, Observer<Categories_Response>
                { loginmodel ->
                    if (loginmodel != null) {
                        root.T_TotalCatrgories.text = "( " + loginmodel.data?.meta?.total + " "+resources.getString(R.string.category) + " )"
                        val listAdapter =
                            CategoriesHome_Adapter(
                                requireContext(),
                                loginmodel.data?.data as List<Categories_Response.Data.Data>
                            )
                    listAdapter.onClick(this)
                        root.recycler_Categroies.setLayoutManager(
                            GridLayoutManager(
                                requireContext()
                                , 2
                            )
                        )
                        root.recycler_Categroies.setAdapter(listAdapter)
                        root.recycler_Categroies.overScrollMode = View.OVER_SCROLL_NEVER


                    }
                })
        }
    }


    fun getSlider() {
        var SliderHome: SliderHome_ViewModel = ViewModelProvider.NewInstanceFactory().create(
            SliderHome_ViewModel::class.java
        )
        requireContext()?.let {
            SliderHome.getData(ChangeLanguage.getLanguage(it), it)
                .observe(viewLifecycleOwner, Observer<SliderHome_Model> { loginmodel ->
                    if (loginmodel != null) {
                        val adapter = SliderHomeAdapter(loginmodel.data,requireContext())
                        sliderView.setSliderAdapter(adapter)
                        root.sliderView.startAutoCycle();
                        root.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    }
                })
        }
    }


    fun getBestSalling() {
        var allproducts: getAllProducts_ViewModel = ViewModelProvider.NewInstanceFactory().create(
            getAllProducts_ViewModel::class.java
        )
        context?.let { ChangeLanguage.getLanguage(it) }?.let {
            allproducts.getLatest(
                "1", it
                , "bestSelling", UserToken
            ).observe(viewLifecycleOwner, Observer<AllProducts_Response> { loginmodel ->
                root.SwipHome.isRefreshing = false
                if (loginmodel != null) {
                    root.T_TotalBestSalling.text = "( " + loginmodel.data?.meta?.total + " " +
                            resources.getString(R.string.product) + " )"
                    var listAdapter =
                        Offers_Adapter(
                            requireContext(),
                            loginmodel.data?.data as List<AllProducts_Response.Data.Data>
                        )
                    listAdapter.onClick(this)
                    listAdapter.onClickFavourit(this)
                    root.recycler_BestSales.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    root.recycler_BestSales.setAdapter(listAdapter)

                }
            })
        }



    }

    fun getDealsProducts() {
        var allproducts: getAllProducts_ViewModel = ViewModelProvider.NewInstanceFactory().create(
            getAllProducts_ViewModel::class.java
        )

        context?.let { ChangeLanguage.getLanguage(it) }?.let {
            allproducts.getLatest(
                "1", it
                , "hotDeal",UserToken
            ).observe(viewLifecycleOwner, Observer<AllProducts_Response> { loginmodel ->
                root.SwipHome.isRefreshing = false
                if (loginmodel != null) {
                    if(loginmodel.data!!.data.size>0) {
                        root.T_TotalOffers.text =
                            "( " + loginmodel.data?.meta?.total + " " + resources.getString(R.string.product) + " )"
                        var listAdapter =
                            Deails_Adapter(
                                requireContext(),
                                loginmodel.data?.data as List<AllProducts_Response.Data.Data>
                            )
                        listAdapter.onClick(this)
                        listAdapter.onClickFavourit(this)
                        root.recycler_Deals.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        root.recycler_Deals.setAdapter(listAdapter)
                        root.recycler_Deals.overScrollMode = View.OVER_SCROLL_NEVER
                    }else {
                        root.T_Offers.visibility=View.GONE
                        root.T_TotalOffers.visibility=View.GONE
                        root.recycler_Deals.visibility=View.GONE

                    }
                }
            })
        }



    }


    override fun onRefresh() {
        getSlider()
        getDealsProducts()
        getBestSalling()
        getAllCategories()
        getArticatles()
    }

    override fun DetailsProducts(detailsProduct: AllProducts_Response.Data.Data) {

    }

    override fun Details(detailsProduct: AllProducts_Response.Data.Data) {
        var productsByid = Product_Details()
        val bundle = Bundle()
        bundle.putString("type", null)
        bundle.putParcelable("ProductItem", detailsProduct)
        productsByid.arguments = bundle
        Navigation.findNavController(root).navigate(R.id.action_nav_home_to_product_Details,bundle);
    }

    override fun AddToFavourit(api: String, Productid: String) {
            var allproducts: getAllProducts_ViewModel = ViewModelProvider.NewInstanceFactory().create(
                getAllProducts_ViewModel::class.java
            )

                allproducts.getAddFavouritProducts(
                    api, Productid,
                    DataSaver.getString("token", null)!!).observe(viewLifecycleOwner, Observer<AddFav_Response> {

                })

    }

    override fun AddToFavouritCart(api: String, Productid: String) {
        if(UserToken!=null) {
            Loading.Show(requireContext())
            var addtocart: AddToCart_ViewModel =
                ViewModelProvider.NewInstanceFactory().create(
                    AddToCart_ViewModel::class.java
                )
            requireContext()?.let {
                addtocart.getData(
                    DataSaver.getString("token", null)!!,
                    Productid,
                   "1"
                    ,
                    it
                )
                    .observe(viewLifecycleOwner, Observer<AddToCart_Response> { loginmodel ->
                        Loading.Disable()
                        if (loginmodel != null) {
                            EventBus.getDefault().postSticky(MessageEvent("cart"))
                            var mp  = MediaPlayer.create(it, R.raw.alert);
                            mp.start()
                            SuccessAdCart(resources.getString(R.string.addtocartsuccess))
                        }else {
                            Toast.makeText(context, AddToCart_ViewModel.ErrorAddCart, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }else {
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)

        }



    }


    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messsg: MessageEvent) {/* Do something */
        Log.d("IGNORE", "Logging view to curb warnings: $messsg")
        UserToken=DataSaver.getString("token", null)

        getDealsProducts()
        getBestSalling()


    };
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

    }


    override fun details(details: Blogs_Response.Data) {
        var productsByid = DetailsArticatle_Fragment()
        val bundle = Bundle()
        bundle.putParcelable("articitalItem", details)
        productsByid.arguments = bundle
        Navigation.findNavController(root).navigate(R.id.detailsArticatle_Fragment,bundle);

    }

    private fun SearchKeyBoard() {
        root.E_Search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!root.E_Search.text.toString().isEmpty()) {
                    var productsByid = AllFilterProducts()
                    val bundle = Bundle()
                    bundle.putString("type", "search")
                    bundle.putString("link", "searchProduct")
                    bundle.putString("name", root.E_Search.text.toString())
                    productsByid.arguments = bundle
                    Navigation.findNavController(root).navigate(R.id.action_nav_home_to_allFilterProducts,bundle);
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun Id(categories: Categories_Response.Data.Data) {
        if(categories.subcategories.size>0){
            var productsByid = SubCategories()
            val bundle = Bundle()
            bundle.putParcelable("items", categories)
            productsByid.arguments = bundle
            Navigation.findNavController(root)
                .navigate(R.id.action_nav_home_to_subCategories, bundle);
        }else {
            var productsByid = AllFilterProducts()
            val bundle = Bundle()
            bundle.putString("type", "search")
            bundle.putString("link", "searchProduct")
            bundle.putString("name", null)
            bundle.putString("id", categories.id.toString())
            productsByid.arguments = bundle
            Navigation.findNavController(root)
                .navigate(R.id.action_nav_home_to_allFilterProducts, bundle);
        }

    }

    override fun Sub_Id(categories: Categories_Response.Data.Data.Subcategory) {

    }


    fun SuccessAdCart(Message:String) {
        val alt_bld: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alt_bld.setMessage(Message).setCancelable(false)
            .setPositiveButton("ok", DialogInterface.OnClickListener { dialog, id ->

            })
        val alert: AlertDialog = alt_bld.create()
        // Title for AlertDialog
        alert.setTitle(resources.getString(R.string.cartt))
        alert.setCancelable(false)
        alert.show()
    }


}