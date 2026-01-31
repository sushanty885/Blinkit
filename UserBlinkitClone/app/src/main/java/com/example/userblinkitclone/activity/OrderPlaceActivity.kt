package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.adapter.AdapterCartProducts
import com.example.userblinkitclone.databinding.ActivityOrderPlaceBinding
import com.example.userblinkitclone.databinding.AddressLayoutBinding
import com.example.userblinkitclone.models.Users
import com.example.userblinkitclone.repository.OrderRepository
import com.example.userblinkitclone.roomdb.CartProducts
import com.example.userblinkitclone.viewmodel.UserViewModel
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import java.util.Locale

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrderPlaceBinding
    val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var grandTotal = 0.0
    private lateinit var cartList: List<CartProducts>
    private val orderRepo = OrderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        onPlaceOrderClicked()
    }

    private fun onPlaceOrderClicked() {
        binding.llPlaceOrder.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status) {
                    lifecycleScope.launch {
                        try {
                            val orderId = orderRepo.saveOrder(
                                cartList = cartList,
                                address = viewModel.getAddress().toString()
                            )
                            viewModel.deleteAllProductsFromCart()
                            // startPhonePePayment(orderId)
                            Toast.makeText(this@OrderPlaceActivity, "Order placed successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@OrderPlaceActivity,
                                e.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    val addressLayoutBinding = AddressLayoutBinding.inflate(layoutInflater)
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this, "Processing...")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhone.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etAddress.text.toString()

        if (userAddress.isEmpty() || userPinCode.isEmpty() || userPhoneNumber.isEmpty() || userState.isEmpty() || userDistrict.isEmpty()) {
            Utils.showToast(this, "Please fill all the fields")
            Utils.hideDialog()
            return
        }

        lifecycleScope.launch {
            val uid = Utils.getSupabase().auth.currentUserOrNull()?.id
            val address = Users(
                uid = uid,
                userPhoneNumber = userPhoneNumber,
                userAddress = userAddress,
                userState = userState,
                userDistrict = userDistrict,
                userPinCode = userPinCode
            )
            viewModel.saveAddress(address)
            viewModel.saveAddressStatus()
            Utils.hideDialog()
            alertDialog.dismiss()
            try {
                val orderId = orderRepo.saveOrder(
                    cartList = cartList,
                    address = address.userAddress.toString()
                )
                 viewModel.deleteAllProductsFromCart()
                // startPhonePePayment(orderId)
                Toast.makeText(this@OrderPlaceActivity, "Order placed successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@OrderPlaceActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getAllCartProducts() {
        adapterCartProducts = AdapterCartProducts()
        binding.rvProductsItems.adapter = adapterCartProducts

        lifecycleScope.launch {
            viewModel.getAllCartProducts().collect { cartProductList ->
                adapterCartProducts.submitList(cartProductList)
                cartList = cartProductList

                var totalPrice = 0.0
                for (product in cartProductList) {
                    val price = product.price ?: 0.0
                    val itemCount = product.quantity ?: 0
                    totalPrice += (price * itemCount)
                }

                binding.tvSubTotal.text = String.format(Locale.getDefault(), "₹%.2f", totalPrice)

                if (totalPrice < 200) {
                    binding.tvDelivery.text = "₹15"
                    totalPrice += 15
                } else {
                    binding.tvDelivery.text = "Free"
                }

                grandTotal = totalPrice
                binding.tvGrandTotal.text = String.format(Locale.getDefault(), "₹%.2f", totalPrice)
            }
        }
    }
}
