package com.example.com.xiazaidemo;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;

public class MainActivity extends AppCompatActivity {
    private EditText downloadpathText;
    private TextView resultView;
    private ProgressBar progressBar;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = progressBar.getProgress() / progressBar.getMax();
                    int result = (int) (num * 100);
                    resultView.setText(result+"%");

                    if (progressBar.getProgress()==progressBar.getMax()){
                        Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadpathText =findViewById(R.id.path);
        progressBar =findViewById(R.id.downloadbar);
        resultView = findViewById(R.id.resultView);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = downloadpathText.getText().toString();

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    download(path,Environment.getDownloadCacheDirectory());
                }else{
                    Toast.makeText(MainActivity.this, R.string.sdcarderror, Toast.LENGTH_SHORT).show();
                }
            }

            private void download(final String path, final File savedir) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileDownloader loader=new FileDownloader(MainActivity.this,path,savedir,3);
                        progressBar.setMax(loader.getFileSize());//设置进度条最大刻度为文件长度
                        try {
                            loader.download(new DownloadProgressListener() {
                                @Override
                                public void onDownloadSize(int size) {
                                    Message msg=new Message();
                                    msg.what=1;
                                    msg.getData().putInt("size",size);
                                    handler.sendMessage(msg);//发消息
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
