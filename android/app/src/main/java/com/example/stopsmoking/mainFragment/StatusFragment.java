package com.example.stopsmoking.mainFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stopsmoking.LoginActivity;
import com.example.stopsmoking.R;
import com.example.stopsmoking.data.UserData;
import com.example.stopsmoking.retrofit.api.ApiUtils;
import com.example.stopsmoking.retrofit.api.UserClient;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatusFragment extends Fragment {
    private TextView dateText;
    private GridAdapter gridAdapter;
    private ArrayList<String> dayList;
    private GridView gridView;
    private Calendar mCal;
    private ProgressBar progressBar;
    private TextView progressBarText;
    private int value1 = 0;
    private int value2 = 0;
    private int dayNum;
    private SimpleDateFormat curYearFormat;
    private SimpleDateFormat curMonthFormat;
    private SimpleDateFormat curDayFormat;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private long now;
    private Button prevButton;
    private Button nextButton;
    private int count = 0;
    private int progressCount;
    private int progressSum;
    private int sMonth;
    private int registration;
    private UserData user;
    private String DateString;
    ProgressDialog dialog;
    private List<JsonObject> logs;
    private Date registeredDate;
    SimpleDateFormat format;
    private boolean uploadFlag;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        dateText = (TextView)view.findViewById(R.id.dateText);
        gridView = (GridView)view.findViewById(R.id.gridView);
//        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
//        progressBarText = (TextView)view.findViewById(R.id.progressBarText);
        prevButton = (Button)view.findViewById(R.id.prevButton);
        nextButton = (Button)view.findViewById(R.id.nextButton);


//        progressCount = 0;
//        progressSum = 0;
//        sMonth = 5;
//        registration = 25;

//        mCal = Calendar.getInstance();
//        if(sMonth == (mCal.get(Calendar.MONTH) + 1)){
//           progressSum = mCal.get(Calendar.DAY_OF_MONTH) - registration + 1;
//        }
//        else {
//            progressSum += mCal.get(Calendar.DAY_OF_MONTH);
//            progressSum += (getMonthLastDay(mCal.get(Calendar.YEAR), sMonth) - registration);
//            while (sMonth < mCal.get(Calendar.MONTH) - progressCount){
//                progressSum += getMonthLastDay(mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH) - progressCount);
//
//                progressCount++;
//            }
//        }
//        progressBar.setProgress((int)(progressSum/56));
//        progressBarText.setText("진행률 : " + progressBar.getProgress() + "%");

        // 오늘에 날짜를 세팅 해준다.
        // 1. 현재 시간 가져오기
        // 2. 현재 시간에 해당하는 Date 생성하기

        now = System.currentTimeMillis();
        date = new Date(now);
        //연,월,일을 따로 저장
        // 3. 가져오고 싶은 형식으로 가져오기
//        if(!uploadFlag){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridAdapter.setPreviousMonth();
                gridAdapter.notifyDataSetChanged();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridAdapter.setNextMonth();
                gridAdapter.notifyDataSetChanged();
            }
        });

        //gridview 요일 표시
        dayList = new ArrayList<String>();


        gridAdapter = new GridAdapter(getActivity(), dayList);
        gridView.setAdapter(gridAdapter);



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            user = getArguments().getParcelable("OBJECT");
        }
        uploadFlag = false;



        curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateString = user.getRegisteredDate();
        format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("UTC/GMT +9"));
        try {
            registeredDate = format.parse(DateString);
//            Toast.makeText(getContext(),DateString,Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sMonth = Integer.parseInt(curMonthFormat.format(registeredDate));
        registration = Integer.parseInt(curDayFormat.format(registeredDate));
        Log.d("calendar", "sMonth: "+sMonth +"/ registration: "+ registration);

        // Async Task 로 measure log 받기
        LogDownloadTask logDownloadTask = new LogDownloadTask();
        logDownloadTask.execute();

    }

    class LogDownloadTask extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("잠시만 기다려 주세요...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            gridAdapter.setNextMonth();
//            gridAdapter.notifyDataSetChanged();
//            gridAdapter.setPreviousMonth();
//            gridAdapter.notifyDataSetChanged();
//            gridAdapter.setNextMonth();
//            gridAdapter.notifyDataSetChanged();
//            gridAdapter.setPreviousMonth();
//            gridAdapter.notifyDataSetChanged();

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            Call<List<JsonObject>> res = ApiUtils.getUserClient().getLogs(user.getUserId());
            res.enqueue(new Callback<List<JsonObject>>() {
                @Override
                public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                    if(response.isSuccessful()){
                       int numLogs = response.body().size();
                       logs = new ArrayList<>();
                       for(int i = 0 ; i<numLogs; i++){
                           logs.add(response.body().get(i));
                       }
                        for(int i = 0 ; i<numLogs; i++){
                            Log.d("calendar", "log data: "+logs.get(i));
                        }
                        uploadFlag = true;
                       dialog.dismiss();
                        gridAdapter.setNextMonth();
                        gridAdapter.notifyDataSetChanged();
                        gridAdapter.setPreviousMonth();
                        gridAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                    Log.d("Calendar", "onFailure: "+t);
                }
            });

            return null;
        }
    }

    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getMonthLastDay(int year, int month){
        switch (month) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return (31);

            case 3:
            case 5:
            case 8:
            case 10:
                return (30);

            default:
                if(((year%4==0)&&(year%100!=0)) || (year%400==0) ) {
                    return (29);   // 2월 윤년계산
                } else {
                    return (28);
                }
        }
    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;


        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            init();
        }

        private void init(){
            //현재 날짜 텍스트뷰에 뿌려줌
            dateText.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

            dayList.add("일");
            dayList.add("월");
            dayList.add("화");
            dayList.add("수");
            dayList.add("목");
            dayList.add("금");
            dayList.add("토");

            mCal = Calendar.getInstance();
            mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
            dayNum = mCal.get(Calendar.DAY_OF_WEEK);

            //1일 - 요일 매칭 시키기 위해 공백 add
            for (int i = 1; i < dayNum; i++) {
                dayList.add("");
            }
            setCalendarDate(mCal.get(Calendar.MONTH) + 1);
        }

        public void setPreviousMonth(){
            count -= 1;
            dayList.clear();
            if(count == 0){
                init();
            }
            else {
                mCal.add(Calendar.MONTH, count);
                dateText.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(mCal.getTime()));

                dayList.add("일");
                dayList.add("월");
                dayList.add("화");
                dayList.add("수");
                dayList.add("목");
                dayList.add("금");
                dayList.add("토");

                dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);
            }
        }

        public void setNextMonth(){
            count +=1;
            dayList.clear();
            if(count == 0){
                init();
            }
            else {
                mCal.add(Calendar.MONTH, count);

                dateText.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(mCal.getTime()));

                dayList.add("일");
                dayList.add("월");
                dayList.add("화");
                dayList.add("수");
                dayList.add("목");
                dayList.add("금");
                dayList.add("토");

                dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                //1일 - 요일 매칭 시키기 위해 공백 add
                for (int i = 1; i < dayNum; i++) {
                    dayList.add("");
                }
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);
            }
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;



            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar, parent, false);
                holder = new ViewHolder();
                holder.itemText = (TextView) convertView.findViewById(R.id.itemText);
                holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemText.setText(getItem(position));


            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();
            //오늘 day 가져옴 day랑 position이랑 비교
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            if (count == 0) {
                if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                    holder.itemText.setTextColor(getResources().getColor(R.color.colorRed));
//                    holder.itemImage.setImageResource(R.drawable.x);
                }
            } else {
                holder.itemText.setTextColor(getResources().getColor(R.color.colorText));
            }



            int nMonth = Integer.parseInt(curMonthFormat.format(mCal.getTime()));
            int chMonth = Integer.parseInt(curMonthFormat.format(mCal.getTime())) + count;

            // 시작 달이 현재 달일때 시작일 ~ 오늘날까지
            if (nMonth == sMonth) {
                if(count == 0) {
                    if (position >= 6 + dayNum) {
                        if (today >= Integer.parseInt(getItem(position)) && Integer.parseInt(getItem(position)) >= registration) {
                            holder.itemImage.setImageResource(R.drawable.x);
                            if(logs != null){
                                for(JsonObject log : logs){
                                    Date temp_date;
                                    int log_day = 1;
                                    try {
                                        temp_date = format.parse(log.get("createdAt").getAsString());
                                        log_day = Integer.parseInt(curDayFormat.format(temp_date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();                                }

                                    if(log_day == Integer.parseInt(getItem(position))){
                                        if(log.get("result").getAsInt() == 0){
                                            holder.itemImage.setImageResource(R.drawable.icon_cigar);
                                        }
                                        else{
                                            holder.itemImage.setImageResource(R.drawable.stop_icon);
                                        }
                                    }

                                }
                            }

                        }
                    }
                }
                else{
                    holder.itemImage.setImageResource(android.R.color.transparent);
                }
            }
            // 시작달이 현재달이 아닐때
            else {
                // 변경한 달이 시작달과 같을 때
                if (chMonth == sMonth) {
                    holder.itemImage.setImageResource(android.R.color.transparent);
                    if (position >= 6 + dayNum) {
                        if (Integer.parseInt(getItem(position)) >= registration && Integer.parseInt(getItem(position)) <= 31) {
                            holder.itemImage.setImageResource(R.drawable.x);
                            if(logs != null){
                                for(JsonObject log : logs){
                                    Date temp_date;
                                    int log_day = 1;
                                    int log_month = 1;
                                    try {
                                        temp_date = format.parse(log.get("createdAt").getAsString());
                                        log_day = Integer.parseInt(curDayFormat.format(temp_date));
                                        log_month = Integer.parseInt(curMonthFormat.format(temp_date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if(log_day == Integer.parseInt(getItem(position)) && log_month == sMonth){
                                        if(log.get("result").getAsInt() == 0){
                                            holder.itemImage.setImageResource(R.drawable.icon_cigar);
                                        }
                                        else{
                                            holder.itemImage.setImageResource(R.drawable.stop_icon);
                                        }
                                    }

                                }
                            }

//                            holder.itemImage.setImageResource(R.drawable.x); // 얘는 뭐임?
                        }
                    }
                }
                // 현재 달일때
                else if(chMonth == nMonth){
                    holder.itemImage.setImageResource(android.R.color.transparent);
                    if (position >= 6 + dayNum) {
                        if (today >= Integer.parseInt(getItem(position))) {
                            holder.itemImage.setImageResource(R.drawable.x);
                            if(logs != null){
                                for(JsonObject log : logs){
                                    Date temp_date;
                                    int log_day = 1;
                                    int log_month = 1;
                                    try {
                                        temp_date = format.parse(log.get("createdAt").getAsString());
                                        log_day = Integer.parseInt(curDayFormat.format(temp_date));
                                        log_month = Integer.parseInt(curMonthFormat.format(temp_date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if(log_day == Integer.parseInt(getItem(position)) && log_month == nMonth){
                                        if(log.get("result").getAsInt() == 0){
                                            holder.itemImage.setImageResource(R.drawable.icon_cigar);
                                        }
                                        else{
                                            holder.itemImage.setImageResource(R.drawable.stop_icon);
                                        }
                                    }

                                }
                            }

//                            holder.itemImage.setImageResource(R.drawable.circle); // 얘 뭐임?
                        }
                    }
                }
                // 시작달과 현재 달 사이에 있는 달
                else if(sMonth < chMonth && chMonth < nMonth){
                    if (position >= 6 + dayNum) {
                        if (Integer.parseInt(getItem(position)) <= 31) {
                            holder.itemImage.setImageResource(R.drawable.stop_icon);
                        }
                    }
                }
                else{
                    holder.itemImage.setImageResource(android.R.color.transparent);
                }
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView itemText;
        ImageView itemImage;
    }

}