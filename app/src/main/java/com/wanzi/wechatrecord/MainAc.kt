package com.wanzi.wechatrecord

import android.Manifest
import android.app.Activity
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.wanzi.wechatrecord.util.ShellCommand
import android.databinding.DataBindingUtil
import android.view.KeyEvent
import android.widget.Toast
import com.wanzi.wechatrecord.databinding.AcMainBinding
import com.wanzi.wechatrecord.util.LogUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import android.content.Context.ALARM_SERVICE
import android.app.AlarmManager
import android.app.Service
import android.app.PendingIntent
import android.content.Intent
import android.content.Context.ALARM_SERVICE
import java.util.*
import android.content.IntentFilter
import android.text.method.ScrollingMovementMethod
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo






class MainAc : AppCompatActivity() {

    private lateinit var binding: AcMainBinding
    private lateinit var aManager: AlarmManager
    private lateinit var ctx: Context
    private lateinit var chatBroadcastReceiver : ChatBroadcastReceiver
    object staticObj{
        var RUN_STATUS:Boolean=false;
        var ACTION_ALARM:String="ACTION_ALARM"
        var broad_add_msg:String="addMsg"
        var broad_clear_msg:String="clearMsg"
        var broad_start_task:String="startTask"
        var ACTION_CHAT_BROAD:String="ACTION_CHAT_BROAD"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx=this
        aManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //启动定时任务
        setAlarm(this,aManager)

        //绑定
        chatBroadcastReceiver=ChatBroadcastReceiver(this)
        chatBroadcastReceiver.registerAction()

        binding = DataBindingUtil.setContentView(this, R.layout.ac_main)
        binding.msg.setMovementMethod(ScrollingMovementMethod.getInstance())
        binding.msg.setText("")

        // 检查权限
        val rxPermissions = RxPermissions(this)
        rxPermissions
                .request(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED
                )
                .subscribe {
                    if (!it) {
                        toast("请打开相关权限")
                        // 如果权限申请失败，则退出
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                }

        binding.btn.setOnClickListener {
            if(!staticObj.RUN_STATUS) {
                clearMsg()
                checkRoot()
                startService()
            }
        }
        binding.checkBtn.setOnClickListener {

            checkRoot()
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        CustomApplication.getRefWatcher(this).watch(this)
    }

    private fun checkRoot() {
        try {
            log("准备检测Root权限")
            // 检测是否拥有Root权限
            if (!ShellCommand.checkRoot(packageCodePath)) {
                log("检测到未拥有Root权限,申请权限")

                // 申请Root权限（弹出申请root权限框）
                val rootCommand = "chmod 777 $packageCodePath"
                ShellCommand.shellCommand(rootCommand)


            }else {
                log("已经获取root权限")
            }
        } catch (e: Exception) {
            toast("检查Root权限失败：${e.message!!}")
        }
    }

    private fun startService(){
        addMsg("开始执行")
        var serviceIntent=Intent(this, CoreService::class.java)
        stopService(serviceIntent);
        startService(serviceIntent)
    }

    /**
     * 跳转到MIUI的权限管理页面
     */
    private fun goMIUIPermission() {
        val i = Intent("miui.intent.action.APP_PERM_EDITOR")
        val componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.MainAcitivty")
        i.component = componentName
        i.putExtra("extra_pkgname", packageName)
        try {
            startActivity(i)
        } catch (e: Exception) {
            toast("跳转权限管理页面失败：${e.message!!}")
        }
    }

    private fun Activity.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }

    fun log(msg: String) {
        addMsg(msg)
        LogUtils.i(this, msg)
    }

    private fun addMsg(msg:String){
        binding.msg.append(msg+"\n")
    }
    private fun clearMsg(){
        binding.msg.setText("")
    }

    /**
     * 返回键只返回桌面
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    fun setAlarm(context: Context, aManager: AlarmManager) {
        var calendar: Calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH,1)
        var intent =  Intent(this, CoreService::class.java);
        intent.setAction(staticObj.ACTION_ALARM);
        var pendingIntent:PendingIntent  = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        aManager.cancel(pendingIntent)
        aManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    inner class ChatBroadcastReceiver constructor(c: Context): BroadcastReceiver() {
        lateinit var context: Context

        init{
            this.context = c
        }

        fun  registerAction(){
            var filter  =  IntentFilter();
            filter.addAction(MainAc.staticObj.ACTION_CHAT_BROAD);
            //context.unregisterReceiver(this)
            //val pm = context.packageManager
            //val resolveInfos = pm.queryBroadcastReceivers(filter, 0)
            context.registerReceiver(this,filter);
        }


        override fun onReceive(p0: Context?, intent: Intent?) {

            var type = intent!!.extras.get("type")
            if(type==staticObj.broad_add_msg) {
                var msg = intent!!.extras.getString("msg")
                addMsg(msg)
            }else if(type==staticObj.broad_clear_msg){
                clearMsg()
            }else if(type==staticObj.broad_start_task){
                setAlarm(context,aManager)
            }
        }

    }


}