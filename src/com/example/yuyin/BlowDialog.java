package com.example.yuyin;

import java.util.Date;

import com.nostra13.example.universalimageloader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BlowDialog extends Activity{  
    
  private Button returnButton;  
  private TextView inputEditor;  
   private  int ultDb = 0;
   private   LinearLayout layout;
   private Button cancelBt;
  // private Button retryBt;
   private RecordThread rt;
   private  Intent mIntent ;  
  protected void onCreate(Bundle savedInstanceState) {  
      super.onCreate(savedInstanceState);  
    //去除title   
      requestWindowFeature(Window.FEATURE_NO_TITLE);   
      //去掉Activity上面的状态栏
      getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
      setContentView(R.layout.testdialog);  
     // 
      final LayoutInflater inflater = LayoutInflater.from(this);
      
      final LinearLayout dynamic = (LinearLayout) findViewById(R.id.dynamic);
	    layout = (LinearLayout) inflater.inflate(
        R.layout.blow_end, null).findViewById(R.id.blowend);
        cancelBt = (Button) layout.findViewById(R.id.b1);
       //  retryBt = (Button) layout.findViewById(R.id.b2);
       mIntent  = new Intent();
       returnButton = (Button)findViewById(R.id.returnButton);  
      inputEditor = (TextView)findViewById(R.id.et);  
      final Handler handler = new Handler()
      {	
    	  @Override
    	  public void handleMessage(Message msg)
    	  {
    		  if(msg.what == 3)
    		  {
    			  inputEditor.setText("吹力十足："+ msg.arg1);
    			  if(msg.arg1>ultDb)
    				  ultDb = msg.arg1;
    			  
    		  }
    		  if(msg.what == 4)
    		  {
    			  inputEditor.setText("您的最终吹力："+ ultDb+"\n"+
    					 "您的广告已经被吹飞了^v^");
    			  
                  mIntent.putExtra("lich", ultDb);  
    			  ultDb = 0;
    		      LinearLayout.LayoutParams lp =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,1);
    		       cancelBt.setLayoutParams(lp);
    		       layout.setLayoutParams(lp);   
    		       dynamic.removeAllViews(); 
    		       dynamic.addView(layout);
    		  }
    		  
    	  }
    	  
      };
      
   cancelBt.setOnClickListener(new OnClickListener() {  
       public void onClick(View v) {  
    	   	 
              // 设置结果，并进行传送  
              BlowDialog.this.setResult(0, mIntent);  
    	   BlowDialog.this.finish();
       }  
   });   
   returnButton.setOnClickListener(new OnClickListener() {  
          public void onClick(View v) {  
        	
           //   mIntent.putExtra("lich", ultDb);  
              // 设置结果，并进行传送  
              BlowDialog.this.setResult(0, mIntent);  
        	  BlowDialog.this.finish();   

          }  
      });  
   
	//启动监听。
   	 rt = new RecordThread(handler);
     rt.start();    
  }  
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
  
  
}  
