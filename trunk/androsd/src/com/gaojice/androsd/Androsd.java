package com.gaojice.androsd;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gaojice.androsd.constants.Constants;
import com.gaojice.androsd.template.UiTemplate;

public class Androsd extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		new UiTemplate(this);
		setContentView(R.layout.main);
		TextView textView = (TextView) findViewById(R.id.tv);
		Enumeration<NetworkInterface> netInterfaces = null;
		boolean hasNetWork = false;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				textView.append("\nDisplayName:" + ni.getDisplayName() + "\n");
				textView.append("Name:" + ni.getName() + "\n");
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					String hostAddress = ips.nextElement().getHostAddress();
					textView.append("IP:" + hostAddress + "\n");
					if (!hostAddress.equals("127.0.0.1")) {
						hasNetWork = true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		textView.append("\nPort: " + Constants.DEFAULT_PORT);

		Button startbtn = (Button) findViewById(R.id.btn_start);
		Button stopbtn = (Button) findViewById(R.id.btn_stop);
		startbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.gaojice.intent.filter.WebService");
				startService(serviceIntent);

			}
		});
		stopbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.gaojice.intent.filter.WebService");
				stopService(serviceIntent);
			}
		});
		if (!hasNetWork) {
			textView.append("\nNo network detected.");
			startbtn.setClickable(false);
			stopbtn.setClickable(false);
		}
	}

}