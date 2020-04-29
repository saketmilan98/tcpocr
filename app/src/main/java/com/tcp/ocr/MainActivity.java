package com.tcp.ocr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String str="";
    String str4="";
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    ClipboardManager clipboardManager;
    ClipData clipData;
    String s5="";

    final int RequestCameraPermissionID = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        RequestCameraPermissionID);
                             return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {

                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {


                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i =0;i<items.size();++i)
                                {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                str=stringBuilder.toString();

                                //textView.setText(stringBuilder.toString());
                                textView.setText(str+""+calc(str));
                            }
                        });
                    }
                }
            });
        }
    }
    public String deletespaces(String s1){
        s1=s1.trim();
        s1=s1.replaceAll(" ","");
        return s1;

    }
    public String calc(String s2) {
        s2 = deletespaces(s2);
        String s3 = "";
        char ch1 = ' ', ch2 = ' ', ch3 = ' ';
        int ch1n = 0, ch2n = 0, ch3n = 0;
        if (s2.length() >= 3) {
            ch1 = s2.charAt(0);
            ch2 = s2.charAt(1);
            ch3 = s2.charAt(2);
            if ((ch1 >= 48 && ch1 <= 57) && (ch3 >= 48 && ch3 <= 57) && ((ch2 >= 42 && ch2 <= 43) || ch2 == 45 || ch2 == 47 || ch2=='x' || ch2=='X' || ch2=='%')) {
                ch1n = Character.getNumericValue(ch1);
                ch3n = Character.getNumericValue(ch3);
                switch (ch2) {
                    case '+':
                        s3 = ("=" + (ch1n + ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case '-':
                        s3 = ("=" + (ch1n - ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case '/':
                        s3 = ("=" + ((ch1n * (1.00)) / ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case 'x':
                        s3 = ("=" + (ch1n * ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case 'X':
                        s3 = ("=" + (ch1n * ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case '*':
                        s3 = ("=" + (ch1n * ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    case '%':
                        s3 = ("=" + (ch1n % ch3n));
                        str4=s2+s3;
                        popup();
                        break;
                    default:
                        s3 = ("");
                }
            } else {
                s3 = ("");

            }
        }
            return s3;
        }

        void popup()
        {
            clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(str4)
                    .setPositiveButton("copy to clipboard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clipData=ClipData.newPlainText("str4",str4);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getApplicationContext(), "Text Copied Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("close", null);
            AlertDialog alert= builder.create();
            alert.show();

        }

}
