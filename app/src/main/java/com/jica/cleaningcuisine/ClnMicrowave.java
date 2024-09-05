package com.jica.cleaningcuisine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.FileProvider;

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
import android.widget.TextView;

import com.github.mikephil.charting.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClnMicrowave extends AppCompatActivity {

    File file;

    Button btnTakePic, btnTakePic2, btnMoveManage;
    Uri uri;
    Bitmap bitmap;

    private BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cln_items);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.nav_guide); // nav_guide 항목 활성화
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_alarm) {
                Intent intent = new Intent(getApplicationContext(), ItemsAlarmActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_guide) {
                return true; // 현재 페이지이므로 아무 작업도 하지 않음
            } else if (itemId == R.id.nav_manage) {
                Intent intent = new Intent(getApplicationContext(), ItemManageActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_info) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });


        TextView tvItemName = findViewById(R.id.tvItemName);
        TextView tvConsider = findViewById(R.id.tvConsider);
        TextView tvCheck = findViewById(R.id.tvCheck);
        TextView checkBox1 = findViewById(R.id.checkBox1);
        TextView checkBox2 = findViewById(R.id.checkBox2);
        TextView checkBox3 = findViewById(R.id.checkBox3);
        TextView checkBox4 = findViewById(R.id.checkBox4);
        TextView checkBox5 = findViewById(R.id.checkBox5);
        TextView checkBox6 = findViewById(R.id.checkBox6);
        TextView tvMainCln = findViewById(R.id.tvMainCln);
        TextView tvStep1 = findViewById(R.id.tvStep1);
        TextView tvStep2 = findViewById(R.id.tvStep2);
        TextView tvStep3 = findViewById(R.id.tvStep3);

        tvItemName.setText("전자레인지 청소하기!"+
                "\n");
        tvConsider.setText("청소 전 고려사항.\n" +
                "1. 적합한 용기 사용.(제품설명서 참조).\n" +
                "  - 모든 금속성 물질 사용금지(화재위험), 전자렌지 전용용기사용.\n" +
                "  - 애완용 동물(햄스터 등) 목욕 후 말리지 않기^^;\n" +
                "2. 상시로 내부 위생을 확인.\n" +
                "  - 조리후 내부에 음식물 많이 튀었거나, 본체에 먼지가 쌓여있다면 언제라도 청소.\n" +
                "3. 암모니아, 표백제, 오븐크리너 같은 연마재 사용 피하기."+
                "\n");
        tvCheck.setText("준비용품");
        checkBox1.setText("식초");
        checkBox2.setText("감귤류 조각(옵션)");
        checkBox3.setText("베이킹소다");
        checkBox4.setText("나무꼬치(숟가락)");
        checkBox5.setText("마른천/헝겊");
        checkBox6.setText("작은 그릇");
        tvMainCln.setText("청소시작");
        tvStep1.setText("파트 1. 용액 수증기로 찌든 때 불리기\n" +
                "1. 세정 용액 만들기.\n" +
                "  - 전자렌지용 그릇에 물(약250ml) + 식초 1티스푼(약15ml) + 감귤류 1조각(선택).\n" +
                "  - (추천) 베이킹소다 1티스푼(약15g) 추가시 탈취의 효과 있음.\n" +
                "2. 그릇에 나무꼬치(숟가락 등) 넣어두기(금속성 금지)\n" +
                "  - 비금속성 도구 통한 열의 분산으로 과열로 인해 그릇이 깨지는 것을 방지하기.\n" +
                "3. 5분 동안 용액 가열하기\n" +
                "  - '강'단계로 가열하여 고온의 수증기를 발생시킨다.\n" +
                "4. 5분 동안 때를 불리기(바로 문 열지 않기)\n" +
                "  - 내부에 고온의 수증기를 가둬서 청소를 용이하게 함" +
                "\n");

        tvStep2.setText("파트 2. 내부 청소하기\n" +
                "1. 내부 턴테이블 분리 후 세척\n" +
                "  - 용액 그릇을 꺼낸 후 턴테이블 분리 후 세척\n" +
                "  - 턴테이블 기름때, 그을린 자국 등의 오염이 심하면 비눗물 당가두기\n" +
                "2. 스펀지나 헝겊으로 내부 문지르기\n" +
                "  - 부스러기 등 큰 잔여물이 있다면 미리 제거하기\n" +
                "  - 안쪽 유리문에 기름때가 있다면 전용기름때 제거 클리너 뿌려놓기\n" +
                "  - 세정 용액을 스펀지나 헝겊에 묻혀서 모두 닦은 후 건조시키기\n" +
                "\n" +
                "@ 주의 사항 : 손쉬운 작업을 위해 천연성분의 전용 클리너 제품등을 이용해도 좋음\n" +
                "  - 좁은 공간에서 이용되기 때문에 성분을 꼭 확인하고 문을 열어두어 환기해 두기\n" +
                "\n" +
                "3. 턴테이블 제자리에 두기\n" +
                "  - 한쪽으로 기울어지지 않게 확인하면서 제자리에 위치시키기\n"+
                "\n");
        tvStep3.setText("파트 3. 외부닦기\n" +
                "1. 온수에 식기세재 넣은 후 행주 담갔다 짜내기\n" +
                "  - 비눗이 잘 흡수되고 오염물의 쉬운 제거를 위해 온수 사용\n" +
                "2. 행주로 상단고 측면 디스플레이를 닦는다.\n" +
                "  - 올려둔 물건들을 제거하고 먼지와 오염물질을 잘 닦아낸다.\n" +
                "  - 손잡이와 디스플레이는 오염에 취약하고 끈적임이 많이 남기에 충분히 닦아낸다. \n" +
                "3. 깨끗한 젖은 행주로 남은 비눗물을 제거한다.\n" +
                "  - 온수로 젹셔서 짜낸 행주로 전체를 다시 닦아 잔류물 방지하기. \n" +
                "3. 깨끗한 젖은 행주로 남은 비눗물을 제거한다.\n" +
                "@ 주의 사항 : 오염이 심한경우 시판용 살균클리너 사용\n" +
                "  - 외부에 직접 분사시 환기 구멍으로 들어가 시스템고장이 있을 수 있으니 유의!\n" +
                "  - 헝겊이나 청소천에 분무해서 외부를 닦기.\n" +
                "4. 마른 헝겊이나 천으로 닦아서 남은 습기를 제거.");




        btnTakePic = findViewById(R.id.btnTakePic);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                Log.d("Main", "카메라띄우기");

            }
        });


        btnTakePic2 = findViewById(R.id.btnTakePic2);
        btnTakePic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });


        btnMoveManage = findViewById(R.id.btnMoveManage);
        btnMoveManage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ItemManageActivity.class);
            startActivity(intent);
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

            // EXIF 데이터를 사용해 회전 각도를 얻습니다.
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