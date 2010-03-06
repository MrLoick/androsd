package com.gaojice.androsd;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.gaojice.androsd.constants.Constants;
import com.gaojice.androsd.http.RequestListenerThread;

public class WebService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread t;
		try {
			t = new RequestListenerThread(Constants.DEFAULT_PORT,
					Constants.ROOT_DIR);
			t.setDaemon(false);
			t.start();
			Toast.makeText(this, "服务已启动...", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "服务已停止...", Toast.LENGTH_LONG).show();
	}

}
