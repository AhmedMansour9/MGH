package com.mgh.ViewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mgh.Model.Register_Model
import com.mgh.Retrofit.ApiClient
import com.mgh.Retrofit.Service
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class Register_ViewModel : ViewModel() {

    public var listProductsMutableLiveData: MutableLiveData<Register_Model>? = null
    private lateinit var context: Context
    private var Wron_Email:Boolean=false
    companion object {

        var LastPhone: String? = ""
        var LastEmail: String? = ""
    }

    fun getStatus():Boolean{
        return Wron_Email
    }

    public fun getData(
        Email: String,
        Password:String,
        Name:String,
        phone:String,
        context: Context
    ): LiveData<Register_Model> {
        listProductsMutableLiveData = MutableLiveData<Register_Model>()
        this.context = context
        getDataValues(Email,Password,Name,phone)
        return listProductsMutableLiveData as MutableLiveData<Register_Model>
    }
    public fun getLogin(
        Email: String,
        Password:String,
        context: Context
    ): LiveData<Register_Model> {
        listProductsMutableLiveData = MutableLiveData<Register_Model>()
        this.context = context
        getLoginValues(Email,Password)
        return listProductsMutableLiveData as MutableLiveData<Register_Model>

    }

    private fun getDataValues(Email: String,Password:String,Name:String,phone:String) {
      var map= HashMap<String,String>()
        map.put("email",Email)
        map.put("password",Password)
        map.put("password_confirmation",Password)
        map.put("phone",phone)
        map.put("full_name",Name)
        map.put("requestType","create")
        map.put("gender","male")

        var service = ApiClient.getClient()?.create(Service::class.java)
        val call = service?.userRegister(map)
        call?.enqueue(object : Callback, retrofit2.Callback<Register_Model> {
            override fun onResponse(call: Call<Register_Model>, response: Response<Register_Model>) {

                if (response.code() == 200) {
                    listProductsMutableLiveData?.setValue(response.body()!!)

                } else if (response.code() == 422) {
                        Wron_Email=true
                        var jObjError = JSONObject(response.errorBody()!!.string())
                        var jOsonData = jObjError.getJSONObject("data")
                        if(let { jOsonData.has("phone")}){
                            LastPhone=jOsonData.getJSONArray("phone").get(0).toString()
                        }
                        if(let { jOsonData.has("email")}){
                            LastEmail=jOsonData.getJSONArray("email").get(0).toString()
                        }
                        listProductsMutableLiveData?.setValue(null)

                    }
                    listProductsMutableLiveData?.setValue(null)
                }


            override fun onFailure(call: Call<Register_Model>, t: Throwable) {
                listProductsMutableLiveData?.setValue(null)

            }
        })
    }
    private fun getLoginValues(Email: String,Password:String) {
        var map= HashMap<String,String>()
        map.put("email",Email)
        map.put("password",Password)


        var service = ApiClient.getClient()?.create(Service::class.java)
        val call = service?.userLogin(map)


        call?.enqueue(object : Callback, retrofit2.Callback<Register_Model> {
            override fun onResponse(call: Call<Register_Model>, response: Response<Register_Model>) {

                if (response.code() == 200) {
                    listProductsMutableLiveData?.setValue(response.body()!!)

                } else {
                    Wron_Email=true
                    listProductsMutableLiveData?.setValue(null)
                }
            }

            override fun onFailure(call: Call<Register_Model>, t: Throwable) {
                listProductsMutableLiveData?.setValue(null)

            }
        })
    }
}