package com.jica.cleaningcuisine;


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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.mikephil.charting.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ClnFridge extends AppCompatActivity {

    ImageView ivPic;
    File file;

    Button btnTakePic, btnTakePic2, btnMoveManage;
    Bitmap bitmap;
    Uri uri;
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

        tvItemName.setText("냉장고 청소하기!");
        tvConsider.setText("청소 전 고려사항\n" +
                "1. 각 제품 특성에 맞는 전용 세척용품을 사용한다.(제품설명서 참조필수)\n" +
                "  - 일반 설겆이용 주방세제 사용 불가.\n" +
                "2. 오염이 너무 심하거나, 건더기 등이 많이 남은 채로 세척기 안에 넣지 않는다.\n" +
                "  - 배수 필터가 막히거나, 세척 잔여물이 남고 냄새가 날 수 있음.");
        tvCheck.setText("준비용품");
        checkBox1.setText("고무장갑");
        checkBox2.setText("세척솔");
        checkBox3.setText("식초(구연산)");
        checkBox4.setText("베이킹소다");
        checkBox5.setText("마른천");
        checkBox6.setText("작은 그릇");
        tvMainCln.setText("청소시작");
        tvStep1.setText("파트 1. 잔여 음식물 제거하기\n" +
                "1. 식기를 모두 제거 후 하단 랙 꺼내기.\n" +
                "2. 바닥 배출구 큰 음식 찌꺼기를 제거하기\n" +
                "  - 가능한 자주 살피고 제거해서 위생, 제품 효율성 높히고, 고장율 낮춤.\n" +
                "3. 필터 제거 후 세척하기\n" +
                "  - 나사를 풀거나 돌려서 분리한 후, 온수와 주방 세제 섞은 용액에 10분 정도 담근 뒤 세척.\n" +
                "4. 필터와 하단 랙을 다시 넣어주기\n" +
                "  - 파트 2 내부청소를 위해 다시 장착하기");
        tvStep2.setText("파트 2. 내부 청소 하기\n" +
                "1. 상단 랙에 식초 약 250ml 넣은 (적합한) 용기를 올려두기\n" +
                "  - 식초로 인한 살균소독 및 쌓여 있는 경수 제거에 도움\n" +
                "  - PH3 정도의 산성인 구연산 10g + 물250ml정도로 만들어 사용해도 되지만\n" +
                "    충분히 녹여야 하는 번거로움과 산성분으로 피부자극이 있을 수 있기에 식초 권장\n" +
                "2. 베이킹 소다 약 150g을 세척기 하단에 골고루 뿌려두기\n" +
                "  - 불쾌한 냄새(곰팡이 및 잔류 찌꺼기 등)를 제거 흡수\n" +
                "  - 의외로 베이킹소다의 세정력은 약하기에 세정보다는 탈취에 초첨\n" +
                "\n" +
                "@ 주의 사항 : 베이킹소다 + 식초/구연산 + 뜨거운 물 등의 혼합 용액 사용 피하기\n" +
                "  - 온라인 상의 여러 정보 중의 하나인 위와 같은 천연가루 혼합 형태의\n" +
                "    청소 방법을 제시하고 있으나, 좋은 방법은 아님.\n" +
                "  - 약알칼리성인 베이킹소다와 산성인 식초/구연산을 섞어 물에 녹이면 중성에 가까운\n" +
                "    물이 되어(중화반응) 세정력이 많이 떨어짐. 보글보글은 거품은 그저 반응일뿐.\n" +
                "  - 섞지 말고 위처럼 따로 따로 놓고 각자의 활용법대로 사용을 권장.\n" +
                "  - 자세한 베이킹소다/구연산/과탄산소다 의 정보는 이곳으로.\n" +
                "\n" +
                "3. 자체 내부 세척기능 실행\n" +
                "  - 제품마다 다양한 명칭으로 자체 클린모드가 있음(제품설명서 확인)\n" +
                "  - 완료후 한 번 더 뜨거운 물로 사이클을 돌리면 식초를 완전 제거하면서 부식예방\n" +
                "4. 세척기 내부 닦기\n" +
                "  - 마른천이나 헝겊으로 물기를 완전히 제거");

        tvStep3.setText("파트 3. 관리하기\n" +
                "1. 세척기 외부와 문이 닫히는 부분을 닦아주기\n" +
                "  - 문 상단에 작동 스위치가 오염되기 쉽기 때문에 자주 닦아 주기\n" +
                "  - 닫히는 부분 패킹과 주변부 위생도 잘 살펴서 청결 유지하기\n" +
                "2. 파트 2의 내부 청소를 주기적으로 실행하기\n" +
                "3. 내부의 랙과 분사 노즐을 식초 혹은 살균수 등으로 닦아주기\n" +
                "  - 이물질과 묶은 때 등을 제거  ");

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