package app.simple.peri.receivers

import android.app.NotificationManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.simple.peri.abstraction.AbstractComposeAutoWallpaperService
import app.simple.peri.database.instances.WallpaperDatabase
import app.simple.peri.services.AutoWallpaperService
import app.simple.peri.utils.FileUtils.toFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class WallpaperActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AbstractComposeAutoWallpaperService.ACTION_DELETE_WALLPAPER_HOME -> {
                handleWallpaperAction(
                        context,
                        intent,
                        WallpaperManager.FLAG_SYSTEM,
                        AbstractComposeAutoWallpaperService.HOME_NOTIFICATION_ID)
            }
            AbstractComposeAutoWallpaperService.ACTION_DELETE_WALLPAPER_LOCK -> {
                handleWallpaperAction(
                        context,
                        intent,
                        WallpaperManager.FLAG_LOCK,
                        AbstractComposeAutoWallpaperService.LOCK_NOTIFICATION_ID)
            }
        }
    }

    private fun handleWallpaperAction(context: Context, intent: Intent, flag: Int, notificationId: Int) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val file = intent.getStringExtra(AbstractComposeAutoWallpaperService.EXTRA_WALLPAPER_PATH)!!.toFile()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        wallpaperManager.clear(flag)
        launchDeleteService(file, context)
        notificationManager.cancel(notificationId)
    }

    private fun launchDeleteService(file: File, context: Context) {
        if (file.exists()) {
            if (file.delete()) {
                updateDatabase(file, context)
            }
        }
    }

    private fun updateDatabase(file: File, context: Context) {
        val applicationContext = context.applicationContext // Avoid memory leak
        CoroutineScope(Dispatchers.IO).launch {
            val wallpaperDatabase = WallpaperDatabase.getInstance(applicationContext)
            val wallpaperDao = wallpaperDatabase?.wallpaperDao()!!
            wallpaperDao.deleteByFile(file.absolutePath.toString())
            wallpaperDatabase.close()
        }
    }

    private fun sendNextWallpaperIntent(context: Context) {
        val intent = Intent(context, AutoWallpaperService::class.java)
        intent.action = AutoWallpaperService.ACTION_NEXT_WALLPAPER
        context.startService(intent)
    }

    companion object {
        private const val TAG = "WallpaperActionReceiver"
    }
}
