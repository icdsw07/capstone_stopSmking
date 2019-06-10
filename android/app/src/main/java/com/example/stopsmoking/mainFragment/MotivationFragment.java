package com.example.stopsmoking.mainFragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.stopsmoking.R;

public class MotivationFragment extends Fragment {

    private int image[] = {R.drawable.smoke1, R.drawable.smoke2, R.drawable.smoke3, R.drawable.smoke4, R.drawable.smoke5, R.drawable.smoke6,
            R.drawable.smoke7, R.drawable.smoke8};
    private String method[] = {"양치질 하기", "심호흡 하기", "샤워하기", "껌 20분 이상 씹기", "물 한잔 천천히 마시기", "인터넷 쇼핑하기",
            "얼음 입에 넣고 녹이기", "과일 먹기", "금연 자랑하기", "커피대신 차 마셔보기", "부모님한테 전화하기", "친구에게 안부묻기",
            "주변 산책해보기"};
    private int randomNum_method;
    private int randomNum_image;
    private int randomNum_effect;
    private String effect[] = {"금연은 혈액순환을 좋게합니다.\n" +
            "예를 들어 우리몸에 염증이 생겼다고 가정해보겠습니다. \n" +
            "그러면 그 부위에는 붓고 피가 몰리게됩니다. 바로 백혈구를 포함한 혈액이 염증부위로 \n" +
            "몰려들어 그 염증을 치료하려는것이죠. \n" +
            "그런데 담배는 혈관을 좁게만들어 이러한 혈액순환을 방해하게 됩니다.\n" +
            "그래서 수술전후에는 흡연을 하지말라고 하는 이유가 여기에 있습니다.\n" +
            "그리고 금연 약 6개월정도 들어서면, 손발이 저린다던지 하는 증상이 점점 좋아집니다.",
            "잇몸건강을 개선합니다. \n" +
                    "금연 초기에는 오히려 잇몸에서 피가난다던지, 구내염이 생긴다던지\n" +
                    "각종 입병이 생길수 있습니다.\n" +
                    "이를 명현현상이라고도 하죠.\n" +
                    "몸이 좋아지는 과정으로 이해를 하면 되겠습니다.\n" +
                    "금연기간이 길어질수록 잇몸이 정상으로 돌아오게됩니다.\n" +
                    "흡연을 하면서 칫솔질할때 피가 자주 비친다는 분들은 꼭 금연하세요.",
            "얼굴색(안색)이 밝아지고, 피부가 달라집니다.\n" +
                    "흡연을 오래하면 할수록 얼굴색이 짙어집니다.\n" +
                    "그러다 금연을 하게되면 서서히 얼굴색이 붉은 혈색을 되찾고,\n" +
                    "다크서클도 점점 사라집니다.\n" +
                    "아울러 푸석푸석하던 피부도 촉촉하게 돌아옵니다.",
            "몸에서 냄새가 나지않습니다.\n" +
                    "흡연자분들은 담배피우고 머지않아 담배냄새는 사라진다고 생각합니다.\n" +
                    "그만큼 담배냄새가 후각을 방해하고 있어서 잘 못느끼는거죠.\n" +
                    "비흡연자들은 자신이 담배를 피지않더라도, 담배연기가 많은 공간에 잠시 있다가\n" +
                    "나와도 옷에 베인 담배냄새가 몇일동안 간다는것을 알 수 있습니다.\n" +
                    "몸에서 담배냄새가 날까봐 늘 맞닥뜨리는 스트레스를 금연으로 날려버리세요!",
            "기억력이 좋아집니다.\n" +
                    "금연초기에는 오히려 기억력과 집중력이 떨어진다고 하시는분들이 많습니다.\n" +
                    "흡연을 하게되면 잠시 동안 뇌로 피가 몰리면서 집중력이 증가한다네요.\n" +
                    "하지만 그 집중력이 오래가지 않는것이 단점입니다.\n" +
                    "흡연 - >집중력 상승 -> 집중력 떨어짐 -> 또 흡연 -> 집중력 상승\n" +
                    "이런식으로 계속 금단증상을 일으켜서 담배를 계속 피우게 만듭니다.\n" +
                    "금연초기에는 기억력도 더 떨어지고,\n" +
                    "집중력도 더 떨어지는 증상을 보이다가 흡연이 습관에서 헤어나올때 즈음\n" +
                    "떨어졌다고 생각했던 기억력과 집중력이 오히려 흡연때보다 더 좋아집니다.\n" +
                    "그리고 집중력을 유지하는 시간또한 길어집니다.",
            "오감이 살아납니다.\n" +
                    "미각, 후각, 촉각 등의 신체의 오감이 되살아나기 시작합니다.\n" +
                    "가장먼저 찾아오는 금연의 긍정적 효과중에 하나입니다.\n" +
                    "이때문에 식욕이 늘어서 살이 찐다고 하시는분들도 많으세요.\n" +
                    "이것두 시간이 지나면 점점 조절이 됩니다.",
            "폐활량이 좋아지고, 운동능력이 향상됩니다. \n" +
                    "금연후 등산이나 수영을 해보면,\n" +
                    "늘어난 폐활량을 느끼실수 있습니다.\n" +
                    "이러한 폐활량을 바탕으로 운동능력이 늘어나서 흡연시보다 운동을 하게되면\n" +
                    "신체능력이 더욱 발달하게 됩니다."};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motivation, container, false);
        randomNum_method = (int)(Math.random() * method.length);
        randomNum_effect = (int)(Math.random() * effect.length);

        TextView methodText = (TextView)view.findViewById(R.id.methodText);
        TextView effectText = (TextView)view.findViewById(R.id.effectText);
        ScrollView scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        Button imageButton = (Button)view.findViewById(R.id.imageButton);

        methodText.setText(method[randomNum_method]);
        methodText.setTypeface(null, Typeface.BOLD);
//        methodText.setPaintFlags(methodText.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        effectText.setText(effect[randomNum_effect]);
//        effectText.setPaintFlags(effectText.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        effectText.setTypeface(null, Typeface.BOLD);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomNum_image = (int)(Math.random() * image.length);
                View dlgView = (View) View.inflate(getActivity(), R.layout.dialog_image, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                ImageView imageView= (ImageView)dlgView.findViewById(R.id.imageView);
                imageView.setImageResource(image[randomNum_image]);
                if(randomNum_image == 0 || randomNum_image == 7) {
                    dlg.setTitle("아이에게 폭력을 가할 수 있습니다.");
                }
                else
                {
                    dlg.setTitle("당신이 될 수 있습니다.");
                }
                dlg.setView(dlgView);
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });
        return view;
    }
}