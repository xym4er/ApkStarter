package com.chornyiya.example;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.model.AndroidComponent;
import com.jaredrummler.apkparser.model.AndroidManifest;
import com.jaredrummler.apkparser.model.ApkMeta;
import com.jaredrummler.apkparser.model.IntentFilter;
import com.jaredrummler.apkparser.model.UseFeature;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.security.cert.CertificateException;

public class MyService extends Service {
    private String pkg;
    PackageManager pm;
    ApplicationInfo appInfo;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        pm = getPackageManager();
        pkg = intent.getStringExtra("pkg");


        if (!pkg.equals("boot")) {
            Log.d("TAG", "Start service on command: " + pkg);
            showPkgInfo();
        } else {
            Log.d("TAG", "Start service first");
        }
        return Service.START_STICKY;
    }

    private void showPkgInfo() {
        String startActivity = "";
        String firstActivity = "";
        List<String> broadcasts = new ArrayList<>();
        try {

            appInfo = pm.getApplicationInfo(pkg, 0);
            ApkParser apkParser = ApkParser.create(appInfo);
            ApkMeta meta = apkParser.getApkMeta();

            String packageName = meta.packageName;
            long versionCode = meta.versionCode;
            List<UseFeature> usesFeatures = meta.usesFeatures;
            List<String> requestedPermissions = meta.usesPermissions;
            Log.d("TAG", "Package name: " + packageName + ", Version: " + versionCode);

            for (int i = 0; i < requestedPermissions.size(); i++) {
                Log.d("TAG", requestedPermissions.get(i));
            }
            for (int i = 0; i < usesFeatures.size(); i++) {
                Log.d("TAG", usesFeatures.get(i).toString());
            }

            Log.d("TAG", "---------------------------------------------------------------------------------------");

            AndroidManifest androidManifest = apkParser.getAndroidManifest();
            if (!androidManifest.getComponents().isEmpty()) {
                for (AndroidComponent component : androidManifest.getComponents()) {
                    if (component.type == 1) {
                        firstActivity = component.name;
                        Log.d("TAG", "firstActivity: " + firstActivity);
                        break;
                    }
                }
            }


            for (AndroidComponent component : androidManifest.getComponents()) {
                if (component.type == 1) {
//                    Log.d("TAG","--------------| WOW!!!! |"+component.name);
                    if (!component.intentFilters.isEmpty()) {
                        for (IntentFilter intentFilter : component.intentFilters) {
                            if (intentFilter.actions.contains("android.intent.action.MAIN")) {
                                if (intentFilter.categories.contains("android.intent.category.LAUNCHER")) {
                                    startActivity = component.name;
                                }
                            }
                        }
                    }
                }
                if (component.type == 4) {
                    if (!component.intentFilters.isEmpty()) {
                        for (IntentFilter intentFilter : component.intentFilters) {
                            broadcasts.addAll(intentFilter.actions);
                        }
                    }
                }
                if (!component.intentFilters.isEmpty()) {
                    for (IntentFilter intentFilter : component.intentFilters) {
                        Log.d("TAG", component.name);
                        for (String action : intentFilter.actions) {
                            Log.d("TAG", "-a " + action);
                        }
                        for (String category : intentFilter.categories) {
                            Log.d("TAG", "-c " + category);
                        }
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TAG", "Err, getAppInfo - " + pkg);
        } catch (IOException e) {
            Log.d("TAG", "Err, getApkMeta");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!startActivity.equals("")) {
            Intent test = new Intent();
            test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            test.setAction(Intent.ACTION_MAIN);
//            test.addCategory(Intent.CATEGORY_LAUNCHER);
            if (startActivity.startsWith(".")) {
                test.setComponent(new ComponentName(pkg, pkg + startActivity));
            } else {
                test.setComponent(new ComponentName(pkg, startActivity));
            }
            startActivity(test);
            for (String broadcast : broadcasts) {
                Log.d("TAG", "Send broadcast2: " + broadcast);
//                sendBroadcast(new Intent(broadcast));
            }
        } else {
            if (!firstActivity.equals("")) {
                Intent test = new Intent();
                test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            test.setAction(Intent.ACTION_MAIN);
//            test.addCategory(Intent.CATEGORY_LAUNCHER);
                if (startActivity.startsWith(".")) {
                    test.setComponent(new ComponentName(pkg, pkg + firstActivity));
                } else {
                    test.setComponent(new ComponentName(pkg, firstActivity));
                }
                startActivity(test);
            } else {
                for (String broadcast : broadcasts) {
                    Log.d("TAG", "Send broadcast: " + broadcast);
                    sendBroadcast(new Intent(broadcast));
                }
            }
        }

    }
}
