package com.jica.cleaningcuisine;

import static com.jica.cleaningcuisine.R.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class ShowPicActivity extends AppCompatActivity {

    ImageView ivPic;
    Button btnRetakePic, btnSavePic;
    File file;
    Uri uri;
    Bitmap bitmap;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);


        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            ivPic = findViewById(R.id.ivPic);
            ivPic.setImageURI(imageUri);
        }

        btnRetakePic = findViewById(R.id.btnRetakePic);
        btnRetakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnSavePic = findViewById(R.id.btnSavePic);
        btnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClnMicrowave.class);
                startActivity(intent);
                Toast.makeText(ShowPicActivity.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void takePicture() {
        try {
            file = creatFile();
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();


            if(Build.VERSION.SDK_INT >= 24) {

                uri = FileProvider.getUriForFile(this, "com.jica.cleaningcuisine", file);

            } else {
                uri = Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "takePicture()에서 예외 발생!");

        }



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 101);


    }

    private File creatFile() {
        String filename = "bfCleaning.jpg";
        File outFile = new File(getFilesDir(), filename);
        Log.d("Main", "파일생성 : " + outFile.getAbsolutePath());

        return outFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                // 이미지가 회전이 필요하면 회전시킵니다.
                if (rotationInDegrees != 0) {
                    bitmap = rotateBitmap(bitmap, rotationInDegrees);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 회전이 적용된 bitmap을 이미지뷰에 설정합니다.
            ivPic.setImageBitmap(bitmap);

        }

        Uri imageUri = saveImage(bitmap);
        Intent intent = new Intent(this, ShowPicActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);


    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    private Uri saveImage(Bitmap bitmap) {
        // 파일 이름과 저장 경로 설정
        String fileName = "bfCleaning" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 저장할 파일 생성
        File imageFile = new File(storageDir, fileName);

        try {
            // 파일 출력 스트림 생성
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            //비트맵을 jpeg 형식으로 압축하여 저장
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null; //오류 발생시 null 반환
        }

        //저장한 파일의 URI 반환
        return  Uri.fromFile(imageFile);
    }


    }
