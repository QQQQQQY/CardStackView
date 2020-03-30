package com.example.cardstackview;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        cardStackView.setCardStackViewShadow(8);
        cardStackView.setCardStackViewSpacing(16);
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
}
