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
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InfoActivity extends AppCompatActivity {


    private BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.nav_info); // nav_guide 항목 활성화
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
                Intent intent = new Intent(getApplicationContext(), CleanGuideActivity.class);
                startActivity(intent);
                return true; // 현재 페이지이므로 아무 작업도 하지 않음
            } else if (itemId == R.id.nav_manage) {
                Intent intent = new Intent(getApplicationContext(), ItemManageActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_info) {
                return true;
            }

            return false;
        });


        TextView tvItemName = findViewById(R.id.tvItemName_info);
        TextView tvStep1 = findViewById(R.id.tvStep1_info);
        TextView tvStep2 = findViewById(R.id.tvStep2_info);

        tvItemName.setText("주방 안전정보");
        tvStep1.setText("- 화재예방 관련사항들(소방청) -\n" +
                "\n"+
                "1. 주택용 소방시설\n" +
                " https://www.nfa.go.kr/nfa/safetyinfo/residentialfire/residentialfire/\n" +
                "\n"+
                "2. 생활 안전정보(응급상황 대처)\n" +
                " https://www.nfa.go.kr/nfa/safetyinfo/lifesafety/0001/\n" +
                "\n"+
                "3. 자연재난 행동요령\n" +
                " https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/prevent01.html?menuSeq=126\n" +
                "\n"+
                "4. 우리집 안전점검\n" +
                " https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/contents/prevent/SDIJKM5301.html?menuSeq=136" +
                "\n");

        Linkify.addLinks(tvStep1, Linkify.WEB_URLS);
        tvStep1.setMovementMethod(LinkMovementMethod.getInstance());

        //String step2Link = getResources().getString(R.string.Step2_link);

        tvStep2.setText("- 안전 사항들 -\n" +
                "1. 각종 안전수칙 및 행동요령(유투브)\n" +
                "  https://www.youtube.com/@user-ho3ub4bw1i/videos\n" +
                "\n" +
                "2. 주방 칼의 사용 요령\n" +
                " https://ckjumpup.imweb.me/41/?q=YToyOntzOjEyOiJrZXl3b3JkX3R5cGUiO3M6MzoiYWxsIjtzOjQ6InBhZ2UiO2k6Mjt9&bmode=view&idx=2939260&t=board\n" +
                "\n" +
                "3. 전자레인지 전용 식품용기\n" +
                " https://www.foodsafetykorea.go.kr/portal/board/boardDetail.do?menu_no=2694&bbs_no=bbs231&ntctxt_no=1082132&menu_grp=MENU_NEW01\n"+
                "\n" +
                "4. 식품안전 지식\n" +
                " https://www.foodsafetykorea.go.kr/portal/board/board.do?menu_grp=MENU_NEW01&menu_no=3120\n"+
                "\n");

        Linkify.addLinks(tvStep2, Linkify.WEB_URLS);
        tvStep2.setMovementMethod(LinkMovementMethod.getInstance());

    }
}