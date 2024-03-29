package com.example.thirdbluetoothconnect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.R.string;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class MainActivity extends Activity {

	TextView show;
	Intent serviceintent;
//	private List<string> devices;
    private List<string> deviceList;
    private ListView listView;
    String getFromService;
    BluetoothAdapter adapter;
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    private InputStream blueStream;    //输入流，用来接收蓝牙数据
    private OutputStream outstream;
    String devicename = "";
    String deviceaddress = "";
    int SELECT = 0;
    myHandler mmhandler;
    connectThread cThread;
     
    sendThread sThread;
    Button startandconnect,getinputstream,send;
    EditText outputstream;
    public boolean flag_rec_thread=false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		获取程序界面中的按钮
		startandconnect = (Button) findViewById(R.id.start_and_connect);
		getinputstream = (Button) findViewById(R.id.get_inputstream);
		send = (Button) findViewById(R.id.send);
		
//		获取程序界面中的TextView
		show = (TextView) findViewById(R.id.show);
		
//		获取程序界面中的EditText
		outputstream = (EditText) findViewById(R.id.output);
		
//		获取程序界面中的ListView
		listView = (ListView) findViewById(R.id.listView1);
		
//		获取系统默认蓝牙
		adapter = BluetoothAdapter.getDefaultAdapter();
		
//		注册BroadcastReceiver
		ActivityReceiver activityRceciver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(activityRceciver, filter);
		getThread.start();//线程启动  
		mmhandler = new myHandler();

		startandconnect.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				直接打开蓝牙
				adapter.enable();
//				开始搜索
			//	adapter.startDiscovery();
				_device = adapter.getRemoteDevice("81:F2:6D:97:2C:8D");
	            // 用服务号得到socket
	            try{
	            	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
	            }catch(IOException e){
//	            	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
	            }
	            try
				{	
					_socket.connect();
					Log.i("SOCKET", "连接"+_device.getName()+"成功！");
					startandconnect.setText("连接成功");
					//Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
				} catch (IOException e)
				{
					
	        		try
					{
//	        		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
					_socket.close();
					startandconnect.setText("重新连接");
					_socket = null;
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
//						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();	
					}            		
					// TODO Auto-generated catch block
					return;
				}
	   
	            //打开接收线程
	            try{
	        		blueStream = _socket.getInputStream();   //得到蓝牙数据输入流
	        		//blueoutOutputStream=_socket.getOutputStream();//得到蓝牙输出数据
//	        		Toast.makeText(this, "绑定数据流成功", Toast.LENGTH_SHORT).show();
	        		}catch(IOException e){
//	        			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
	        			return;
	        		}
			
				
			}
		});

		getinputstream.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				flag_rec_thread=!flag_rec_thread;
				if (flag_rec_thread)
				{
					getinputstream.setText("正在接受");
				}else {
					getinputstream.setText("停止接受");
				}
			
			}
		});
		
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				String add = parent.toString();
			/*	if(cThread == null) {  
					cThread = new connectThread();  
					cThread.start();//线程启动  
                }         
*/			
				_device = adapter.getRemoteDevice("81:F2:6D:97:2C:8D");
	            // 用服务号得到socket
	            try{
	            	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
	            }catch(IOException e){
//	            	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
	            }
	            try
				{	
					_socket.connect();
					Log.i("SOCKET", "连接"+_device.getName()+"成功！");
					//Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
				} catch (IOException e)
				{
					
	        		try
					{
//	        		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
					_socket.close();
					_socket = null;
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
//						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();	
					}            		
					// TODO Auto-generated catch block
					return;
				}
	   
	            //打开接收线程
	            try{
	        		blueStream = _socket.getInputStream();   //得到蓝牙数据输入流
	        		//blueoutOutputStream=_socket.getOutputStream();//得到蓝牙输出数据
//	        		Toast.makeText(this, "绑定数据流成功", Toast.LENGTH_SHORT).show();
	        		}catch(IOException e){
//	        			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
	        			return;
	        		}
			
			}
			
		});

	}
	
	public class ActivityReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//	        	判断是否有搜索结果
//	        	Toast.makeText(MainActivity.this, "搜索到蓝牙设备", Toast.LENGTH_SHORT).show();
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            devicename = device.getName();
	            deviceaddress = device.getAddress();
	            getFromService = devicename + "   " + deviceaddress;
	        }
			listView = (ListView) findViewById(R.id.listView1);
			
			deviceList = new ArrayList<string>();
			ArrayList<String> devices = new ArrayList<String>();
			devices.add(getFromService);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.layoutstyle,
	                devices);
	        listView.setAdapter(adapter);
			
		}
		 
	}
	
	public class myHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x123){ 
				String text;
				text = (String) msg.obj;
                show.setText(text);  
                show.setTextSize(32);
            }  
			super.handleMessage(msg);
		}
	}
	
	
	public void on_send(View v)
	{
				byte []bs={0x01,0x02,0x03};
				String sendoutput = outputstream.getText().toString();
				try{
					outstream = _socket.getOutputStream();
					outstream.write(bs); 
				}catch(IOException e) {  
	                
	            }  
	}
		 
		

	
	public class connectThread extends Thread{
		@Override
		public void run() {
		//	_device = adapter.getRemoteDevice(deviceaddress);
			_device = adapter.getRemoteDevice("81:F2:6D:97:2C:8D");
            // 用服务号得到socket
            try{
            	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            }catch(IOException e){
//            	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
            }
            try
			{	
				_socket.connect();
				Log.i("SOCKET", "连接"+_device.getName()+"成功！");
				//Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
			} catch (IOException e)
			{
				
        		try
				{
//        		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
				_socket.close();
				_socket = null;
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
//					Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();	
				}            		
				// TODO Auto-generated catch block
				return;
			}
   
            //打开接收线程
            try{
        		blueStream = _socket.getInputStream();   //得到蓝牙数据输入流
        		//blueoutOutputStream=_socket.getOutputStream();//得到蓝牙输出数据
//        		Toast.makeText(this, "绑定数据流成功", Toast.LENGTH_SHORT).show();
        		}catch(IOException e){
//        			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
        			return;
        		}
			super.run();
		}
	}
	
	Thread getThread =new Thread(){
		@Override
		public void run() {
			
			while (!currentThread().isInterrupted()) {
				String rec="";
				if(flag_rec_thread)
				{
					try{
						blueStream = _socket.getInputStream();
						int num;
						byte[] buffer =new byte[1024];
						num = blueStream.read(buffer);
						
						for(int i = 0 ; i < num; i++)
						{ 		  
						//readMessage[i]=String.format("%2x", bytes[i-count]);	
						rec+=Integer.toHexString(buffer[i]&0xff);
						}
						
						Message message = mmhandler.obtainMessage();  
			            message.what = 0x123;  
			            message.obj = rec;  
			           
			            mmhandler.sendMessage(message);  
					}catch(IOException e) {  
		                break;  
		            }  
					
					try
					{
						sleep(100);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			
			
			 
		}
	};
	
	public class sendThread extends Thread{
		
		String sendoutput = outputstream.getText().toString();

		
		@Override
		public void run() {
			try{
					outstream = _socket.getOutputStream();
					outstream.write(getHexBytes(sendoutput)); 
				}catch(IOException e) {  
	                
	            }  
					
			super.run();
		}
	}
	private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }
}
