package com.example.wsins.wifiviewer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wsins.wifiviewer.adapter.WifiAdapter;
import com.example.wsins.wifiviewer.info.WifiInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManage wifiManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManage = new WifiManage();
        try {
            Init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Init() throws Exception {
        final List<WifiInfo> wifiInfos = wifiManage.Read();
        ListView wifiInfosView = (ListView) findViewById(R.id.wifi_list);
        WifiAdapter ad = new WifiAdapter(wifiInfos, MainActivity.this);
        wifiInfosView.setAdapter(ad);
        wifiInfosView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mclipData = ClipData.newPlainText("wifipwd", wifiInfos.get(position).password);
                cm.setPrimaryClip(mclipData);
                Toast.makeText(MainActivity.this, "密码复制成功！", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }


}
