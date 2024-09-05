package com.jica.cleaningcuisine;

import android.Manifest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ItemsAlarmActivity extends AppCompatActivity {

    ArrayList<AlarmInfo> items;
    ListView listViewItems;
    ItemsAdapter adapter;

    AlarmDBHelper alarmDBHelper;
    NotificationManagerCompat notificationManagerCompat;

    Button btnAdd;

    static final String CHANNEL_ID = "alarmChannel";
    private static final String CHANNEL_NAME = "Alarm Notification Channel";
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;
    private BottomNavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xml 레이아웃 전개
        setContentView(R.layout.activity_items_alarm);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.nav_alarm); // nav_alarm 항목 활성화
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_alarm) {
                return true; // 현재 페이지이므로 아무 작업도 하지 않음
            } else if (itemId == R.id.nav_guide) {
                Intent intent = new Intent(getApplicationContext(), CleanGuideActivity.class);
                startActivity(intent);
                return true;
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




    // UI 객체 찾기
        listViewItems = findViewById(R.id.listViewItems);
        btnAdd = findViewById(R.id.btnAdd);

        // 알림 권한 확인 및 요청


        alarmDBHelper = new AlarmDBHelper(getApplicationContext(), "alarm.db", null, 1);
        // 원본 데이터  SQLite에서 읽어와야 한다.
        items = alarmDBHelper.queryAll();

        // 어댑터 만들어서 리스트뷰와 연결
        adapter = new ItemsAdapter(this, items);
        listViewItems.setAdapter(adapter);

        // 리스트 항목 클릭 시 삭제를 위한 리스너 설정
        listViewItems.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int position = listViewItems.pointToPosition((int) event.getX(), (int) event.getY());
                if (position != ListView.INVALID_POSITION) {
                    View child = listViewItems.getChildAt(position - listViewItems.getFirstVisiblePosition());
                    if (child != null) {
                        child.setOnTouchListener(new SwipeDismissTouchListener(child, new SwipeDismissTouchListener.DismissCallbacks() {
                            @Override
                            public void onDismiss(View view) {
                                confirmDeleteItem(position);
                            }
                        }));
                    }
                }
                return false;
            }
        });

        notificationManagerCompat = NotificationManagerCompat.from(this);

        createNotificationChannel();

        // 버튼 클릭 시의 이벤트 핸들러
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddItemDialog();
            }
        });

        // 연장 알람 설정을 위해 호출된 경우 처리
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_extend_dialog", false)) {
            int position = intent.getIntExtra("item_position", -1);
            if (position != -1) {
                showExtendAlarmDialog(position);
            }
        }
    }

    private void confirmDeleteItem(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("해당 항목을 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ItemsAlarmActivity.this, "항목이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }


    private void showAddItemDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ItemsAlarmActivity.this);
        dlg.setTitle("항목 추가"); // 제목
        final String[] versionArray = new String[]{"냉장고", "오븐", "식기세척기", "전자레인지"};
        final String[] imageArray = new String[]{"fridge", "oven", "dishwasher", "microwave"};
        dlg.setIcon(R.drawable.ic_launcher_foreground); // 아이콘 설정

        dlg.setSingleChoiceItems(versionArray, 0, null);

        // 버튼 클릭 시 동작
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String selectedItem = versionArray[selectedPosition];
                String selectedImage = imageArray[selectedPosition];
                addItem(selectedItem, selectedImage);

                // 원본 데이터에 변화가 생겼다는 것을 어댑터에게 알려주어야 변경된 내용을 화면, 즉 ListView에 반영한다.
                adapter.notifyDataSetChanged();
                // 실제 데이타베이스에도 1건의  row를 추가하자
                int n = alarmDBHelper.insert(selectedItem, selectedImage, null);
                // 토스트 메시지
                if (n == 1) {
                    Toast.makeText(ItemsAlarmActivity.this, selectedItem + "이(가) 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemsAlarmActivity.this, selectedItem + "이(가) 추가 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    private void addItem(String name, String image) {
        AlarmInfo item = new AlarmInfo();
        item.name = name;
        item.image = image;

        items.add(item);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for Alarm Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showAlarmDialog(int position) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ItemsAlarmActivity.this);
        dlg.setTitle("알람 설정"); // 제목

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alarm_setting, null);
        dlg.setView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        // 버튼 클릭 시 동작
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(year, month, day, hour, minute, 0);
            Log.d("TAG",  minute+"");
               setAlarm(alarmTime, position);

                // 토스트 메시지
                Toast.makeText(ItemsAlarmActivity.this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("취소", null);
        dlg.show();
    }


    private void showExtendAlarmDialog(int position) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(ItemsAlarmActivity.this);
        dlg.setTitle("알람 연장"); // 제목

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alarm_setting, null);
        dlg.setView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        // 버튼 클릭 시 동작
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                Calendar alarmTime = Calendar.getInstance();
                alarmTime.set(year, month, day, hour, minute, 0);

                setAlarm(alarmTime, position);

                // 토스트 메시지
                Toast.makeText(ItemsAlarmActivity.this, "알람이 연장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(Calendar alarmTime, int position) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("item_name", items.get(position).name);
        intent.putExtra("item_position", position);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), position, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
    }

}
