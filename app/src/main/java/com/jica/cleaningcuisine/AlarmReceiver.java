package com.jica.cleaningcuisine;

import static com.jica.cleaningcuisine.ItemsAlarmActivity.CHANNEL_ID;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarmChannel";
    @Override
    public void onReceive(Context context, Intent intent) {

        if ("ACTION_DISMISS_NOTIFICATION".equals(intent.getAction())) {
            int notificationId = intent.getIntExtra("notification_id", -1);
            if (notificationId != -1) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(notificationId); // 알림 닫기
            }
            return;
        }

        String itemName = intent.getStringExtra("item_name");
        int itemPosition = intent.getIntExtra("item_position", -1);

        Intent extendIntent = new Intent(context, ItemsAlarmActivity.class);
        extendIntent.putExtra("show_extend_dialog", true);
        extendIntent.putExtra("item_position", itemPosition);


        PendingIntent extendPendingIntent = PendingIntent.getActivity(
                context,
                itemPosition,
                extendIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // "가이드 보기" 버튼 클릭 시 CleanGuideActivity로 이동하는 Intent
        Intent guideIntent = new Intent(context, CleanGuideActivity.class);
        PendingIntent guidePendingIntent = PendingIntent.getActivity(
                context,
                itemPosition + 1, // Ensure a unique request code for different PendingIntents
                guideIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // "확인" 버튼 클릭 시 알림을 닫기 위한 Intent
        Intent dismissIntent = new Intent(context, AlarmReceiver.class);
        dismissIntent.setAction("ACTION_DISMISS_NOTIFICATION");
        dismissIntent.putExtra("notification_id", itemPosition);

        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                itemPosition + 2, // Ensure a unique request code
                dismissIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("알람")
                .setContentText(itemName + " 알람 시간 입니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_launcher_foreground, "연장", extendPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "가이드 보기", guidePendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "확인", dismissPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 권한 확인 - 권한이 이미 부여되었을 경우에만 알림 생성
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(itemPosition, builder.build());
        }
    }
}
