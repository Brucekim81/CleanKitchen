package com.jica.cleaningcuisine;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;


public class CleanGuideActivity extends AppCompatActivity {

    ArrayList<HashMap<String, Object>> itemsData;   //원본 데이타

    ListView listViewItems;      // ListView
    SimpleAdapter simpleAdapter; // 어탭터-원본데이타를 key-value의 쌍으로 제공해야 한다.

    private BottomNavigationView navigationView;


    String[] itemsNames = {"냉장고", "오븐", "식기세척기", "전자레인지"};
    int[] itemsImageIds = {R.drawable.fridge,
            R.drawable.oven,
            R.drawable.dishwasher,
            R.drawable.microwave};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_guide);


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

        //UI객체 찿기
        listViewItems = findViewById(R.id.listViewItems);

        //원본 데이타 만들기
        itemsData = new ArrayList<>();

        for (int i = 0; i < itemsNames.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemsName", itemsNames[i]);
            map.put("itemsImage", itemsImageIds[i]);
            itemsData.add(map);

        }

        //SimpleAdapter 만들기 - 항목뷰가 2개이므로 해당정보 설정
        String[] from = {"itemsName", "itemsImage"};      //원본데이타에서 key


        int to[] = {R.id.tvItemsName, R.id.ivItemsImage}; //원본데이타를 항목뷰에 연결시킬때의 항목뷰의 개별위젯 id
        //int to[] = {android.R.id.text1, android.R.id.text2};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), itemsData, R.layout.list_row_items, from, to);
        //SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), phoneData, android.R.layout.simple_list_item_2, from, to);

        //리스트뷰와 어탭터 연결
        listViewItems.setAdapter(simpleAdapter);

        // 리스트뷰 항목 클릭 리스너 설정
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (itemsNames[position]) {
                    case "냉장고":
                        intent = new Intent(CleanGuideActivity.this, ClnFridge.class);
                        break;
                    case "오븐":
                        intent = new Intent(CleanGuideActivity.this, ClnOven.class);
                        break;
                    case "식기세척기":
                        intent = new Intent(CleanGuideActivity.this, ClnDishwasher.class);
                        break;
                    case "전자레인지":
                        intent = new Intent(CleanGuideActivity.this, ClnMicrowave.class);
                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }
}