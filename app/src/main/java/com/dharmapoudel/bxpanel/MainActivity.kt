package com.dharmapoudel.bxpanel

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler
import com.anjlab.android.iab.v3.PurchaseInfo
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity(), IBillingHandler {

    private lateinit var prefs: Preferences
    private var accessibilityManager: AccessibilityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //remove the shadow
        supportActionBar!!.elevation = 0f
        supportActionBar!!.setTitle(R.string.app_name)

        accessibilityManager = applicationContext.getSystemService(AccessibilityManager::class.java)

        //init the preference
        prefs = Preferences(this)
        if (!hasPermissions()) { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }


        //initialize billing
        val bp = BillingProcessor(this, Constants.LICENSE_KEY, this)
        bp.initialize()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        prefs.isEnabled = hasPermissions()

        //update the panel items shown
        updatePanelItems()
    }

    private fun updatePanelItems() {
        val sharedPreferences = prefs.getPref(this)
        val panelItemsLayout = findViewById<LinearLayout>(R.id.panelItems)
        val count = sharedPreferences.getInt(Constants.NO_OF_ITEMS, 5)
        //show these items
        for (i in 0 until count) {
            val type = sharedPreferences.getString(Constants.LIST_ITEM + "_" + i + "_type", "")
            val action = sharedPreferences.getString(Constants.LIST_ITEM + "_" + i + "_action", "")
            val extra = sharedPreferences.getString(Constants.LIST_ITEM + "_" + i + "_extra", "")
            val linearLayout = panelItemsLayout.getChildAt(i)
            updatePanelItemText(linearLayout, type, action, extra)
            updatePanelItemIcon(linearLayout, type, action, extra)
            panelItemsLayout.getChildAt(i).visibility = View.VISIBLE
        }

        //hide the rest
        val editor = sharedPreferences.edit()
        for (i in count..9) {
            editor.remove(Constants.LIST_ITEM + "_" + i + "_type")
            editor.remove(Constants.LIST_ITEM + "_" + i + "_action")
            editor.remove(Constants.LIST_ITEM + "_" + i + "_extra")
            editor.apply()
            panelItemsLayout.getChildAt(i).visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        Toast.makeText(
            applicationContext,
            resources.getString(R.string.thank_you),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPurchaseHistoryRestored() {
        //Toast.makeText(getApplicationContext(), "History Restored!", Toast.LENGTH_SHORT).show();
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.next_time), Toast.LENGTH_SHORT).show();
    }

    override fun onBillingInitialized() {
        //Toast.makeText(getApplicationContext(), "Billing initialized!", Toast.LENGTH_SHORT).show();
    }

    fun openAppSettings(v: View?) {
        startActivity(Intent(this, PreferenceActivity::class.java))
    }

    fun openPanelItemSettings(v: View) {
        val intent = Intent(this, PanelItemSettingActivity::class.java)
        intent.putExtra(Constants.CURRENT_ITEM, v.tag.toString())
        startActivity(intent)
    }

    private fun updatePanelItemText(
        view: View,
        actionType: String?,
        action: String?,
        extra: String?
    ) {
        val vg = (view as ViewGroup).getChildAt(1) as ViewGroup
        val itemText =
            getActionText(actionType) //Constants.ACTION_OPEN_APP.equalsIgnoreCase(actionType) ? getString(R.string.open_app): getString(R.string.panel_item);
        val textViewTitle = vg.getChildAt(0) as TextView
        textViewTitle.text = itemText
        val textViewDescription = vg.getChildAt(1) as TextView
        textViewDescription.text = if (action == ""
        ) getString(R.string.panel_item_description) else action
    }

    private fun updatePanelItemIcon(
        view: View,
        actionType: String?,
        action: String?,
        extra: String?
    ) {
        val imageView = (view as ViewGroup).getChildAt(0) as ImageView
        imageView.background = null
        val iconDrawable = IconFactory.getIconFromTag(actionType, action, extra, view.getContext())
        Glide.with(applicationContext).load(iconDrawable).into(imageView)
    }

    private fun getActionText(actionType: String?): String {
        val actionText: Int = when (actionType) {
            Constants.ACTION_OPEN_APP -> R.string.open_app
            Constants.ACTION_NAVIGATE -> R.string.navigation
            Constants.ACTION_MEDIA -> R.string.media
            Constants.ACTION_UTILS -> R.string.utils
            else -> R.string.panel_item
        }
        return resources.getString(actionText)
    }

    private fun hasPermissions(): Boolean {
        for (info in accessibilityManager?.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC,
        ) ?: return true) {
            if (info.resolveInfo.serviceInfo.packageName == applicationContext.packageName) return true
        }
        return false
    }
}