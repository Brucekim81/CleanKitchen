package com.jica.cleaningcuisine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ItemManageActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private String[] items = {"냉장고", "오븐", "식기세척기", "전자레인지"};
    private boolean[] checkedItems = new boolean[items.length];
    private HashMap<CalendarDay, String> selectedItemsMap = new HashMap<>(); // 날짜와 선택된 항목들을 저장하는 Map
    private ManageDBHelper dbHelper;
    private BarChart barChart;
    private List<BarEntry> entries = new ArrayList<>();
    private float xValue = 0f;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);



        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.nav_manage); // nav_manage 항목 활성화
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
                return true;
            } else if (itemId == R.id.nav_manage) {
                return true; // 현재 페이지이므로 아무 작업도 하지 않음
            } else if (itemId == R.id.nav_info) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });


        dbHelper = new ManageDBHelper(this, "manage.db", null, 1);

        barChart = findViewById(R.id.barChart);
        calendarView = findViewById(R.id.calendarview);

        // Load saved data from the database
        loadSavedData();

        // Initialize the bar chart
        initBarChart(barChart);

        // 날짜가 선택될 때 다이얼로그를 표시
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                showSelectionDialog(date);
            }
        });

        // calendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator());
    }

    private void showSelectionDialog(CalendarDay date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("항목을 선택하세요")
                .setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
                    // 항목 선택 처리
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("확인", (dialog, id) -> {
                    updateCalendarWithSelectedItems(date);
                    updateBarChart();
                })
                .setNegativeButton("취소", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCalendarWithSelectedItems(CalendarDay date) {
        StringBuilder selectedItems = new StringBuilder();
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                if (selectedItems.length() > 0) {
                    selectedItems.append(", "); // 항목이 여러 개일 경우 구분자 추가
                }
                selectedItems.append(items[i]);
            }
        }

        // 선택된 항목들을 Map에 저장
        if (selectedItems.length() > 0) {
            selectedItemsMap.put(date, selectedItems.toString());
            //calendarView.addDecorator(new EventDecorator(date, selectedItems.toString()));

            // Save the selection in the database
            dbHelper.insert(date.getYear(), date.getMonth(), date.getDay(), selectedItems.toString());

        }
        // 예시로 선택된 항목을 토스트로 표시
        Toast.makeText(this, "날짜: " + date + "\n선택된 항목: " + selectedItems.toString(), Toast.LENGTH_SHORT).show();
    }

    private void updateBarChart() {
        entries.clear();
        xValue = 0f;

        for (int i = 0; i < items.length; i++) {
            int count = dbHelper.getCountForItem(items[i]);
            entries.add(new BarEntry(xValue++, count, items[i]));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "청소 빈도");
        barDataSet.setColors(
                Color.rgb(207, 248, 246),
                Color.rgb(148, 212, 212),
                Color.rgb(136, 180, 187),
                Color.rgb(118, 174, 175)
        );

        BarData data = new BarData(barDataSet);
        data.setValueTextSize(10f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return barEntry.getData().toString(); // 각 막대 끝에 항목 이름 표시
            }
        });

        barChart.setData(data);
        barChart.invalidate(); // 차트를 갱신
    }

    private void initBarChart(BarChart barChart) {
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawBorders(false);

        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        barChart.animateY(1000);
        barChart.animateX(1000);

        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(items.length); // x축 레이블 개수 설정
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""; // x축 값 제거 (해당 월을 가리킴)
            }
        });

        // Y축 설정
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setAxisMinimum(0f); // y축 최소값 설정
        leftAxis.setAxisMaximum(8f); // y축 최대값 설정

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setTextColor(Color.GREEN);
        rightAxis.setAxisMinimum(0f); // y축 최소값 설정
        rightAxis.setAxisMaximum(8f); // y축 최대값 설정

        // 범례 설정
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(20f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void loadSavedData() {
        List<CalendarData> savedData = dbHelper.queryAll();
        for (CalendarData data : savedData) {
            CalendarDay date = CalendarDay.from(data.getYear(), data.getMonth(), data.getDay());
            selectedItemsMap.put(date, data.getSelectedItems());
            calendarView.addDecorator(new EventDecorator(date, data.getSelectedItems()));
        }
        updateBarChart();
    }

    // 날짜를 장식하는 데 사용되는 클래스
    public class EventDecorator implements DayViewDecorator {

        private final CalendarDay date;
        private final String label;

        public EventDecorator(CalendarDay date, String label) {
            this.date = date;
            this.label = label;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(date); // 선택된 날짜와 일치하는지 확인
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
            view.addSpan(new RelativeSizeSpan(1.2f));
        }
    }

    public class SundayDecorator implements DayViewDecorator {
        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() { }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    public class SaturdayDecorator implements DayViewDecorator {
        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() { }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }
}
