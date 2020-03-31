package com.example.cardstackview;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private NestedScrollView nsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nsv = findViewById(R.id.nsv);
        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        CardStackView.Adapter adapter = new CardStackView.Adapter<User>(
                mockData(),
                R.layout.view_card_stack_header,
                R.layout.view_card_stack_content) {
            @Override
            public void bindView(CardStackView.CollapsibleCardView v, int pos, User user) {
                TextView tvName = v.getViewHeader().findViewById(R.id.tv_name);
                tvName.setText(user.getName());
                TextView tvGender = v.getViewContent().findViewById(R.id.tv_gender);
                tvGender.setText(user.getGender());
                TextView tvAge = v.getViewContent().findViewById(R.id.tv_age);
                tvAge.setText(user.getAge());
                TextView tvAddress = v.getViewContent().findViewById(R.id.tv_address);
                tvAddress.setText(user.getAddress());
            }
        };
        cardStackView.setAdapter(adapter);
        cardStackView.setCardStackViewRadius(12);
        cardStackView.setCardStackViewSpacing(4);

        // 自动滚动到点击的 view 附近。
        cardStackView.setOnCardStackViewStateChangedListener(collapsibleCardView -> {
            if (collapsibleCardView.isExpand()) {
                collapsibleCardView.post(() -> scrollCardStackView(collapsibleCardView, dp2px(cardStackView.getCardStackViewSpacing() / 2)));
            }
        });
    }

    /**
     * @param dy Y 轴偏移量.
     */
    public void scrollCardStackView(CardStackView.CollapsibleCardView collapsibleCardView, int dy) {
        int[] locAryNsv = new int[2];
        nsv.getLocationOnScreen(locAryNsv);
        int locYNsv = locAryNsv[1]; // NestedScrollView 离屏幕顶部的距离。

        int[] locAryView = new int[2];
        collapsibleCardView.getLocationOnScreen(locAryView);
        int locYView = locAryView[1]; // CardStackView 离屏幕顶部的距离。

        if (locYView < locYNsv) {
            int distance = locYView - locYNsv - dy;
            nsv.smoothScrollBy(0, distance);
        }
    }

    private List<User> mockData() {
        List<User> list = new ArrayList<>();
        for (int i = 1; i < 15; i++) {
            User user = new User();
            user.setName("张" + i);
            user.setAge(new Random().nextInt(60) + "");
            user.setGender("男");
            user.setAddress("上海市浦东新区xxx");
            list.add(user);
        }
        return list;
    }

    private static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
