package com.chornyiya.example;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.model.AndroidComponent;
import com.jaredrummler.apkparser.model.AndroidManifest;
import com.jaredrummler.apkparser.model.ApkMeta;
import com.jaredrummler.apkparser.model.IntentFilter;
import com.jaredrummler.apkparser.model.UseFeature;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity {
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pm = getPackageManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplicationInfo appInfo = null;
                try {
                    appInfo = pm.getApplicationInfo("com.url.webviewerapp", 0);
                    ApkParser apkParser = ApkParser.create(appInfo);
                    ApkMeta meta = null;
                    meta = apkParser.getApkMeta();
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
                    Log.d("TAG", "---------------------------------------------------------------------------------------");

                    if (apkParser.verifyApk() == ApkParser.ApkSignStatus.SIGNED) {
                        Log.d("TAG", apkParser.getCertificateMeta().signAlgorithm);
                    }
                    Log.d("TAG", "---------------------------------------------------------------------------------------");
                    Log.d("TAG", "---------------------------------------------------------------------------------------");

                    AndroidManifest androidManifest = apkParser.getAndroidManifest();
                    for (AndroidComponent component : androidManifest.getComponents()) {
                        if (!component.intentFilters.isEmpty()) {
                            for (IntentFilter intentFilter : component.intentFilters) {
                                Log.d("TAG", component.name);
                                for (String action:intentFilter.actions){

                                    Log.d("TAG","----| "+ action);
                                }
                            }
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("TAG", "Err, getAppInfo - "+"com.mobilonia.appdater.AppdaterApp");
                } catch (IOException e) {
                    Log.d("TAG", "Err, getApkMeta");
                } catch (CertificateException | ParseException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
