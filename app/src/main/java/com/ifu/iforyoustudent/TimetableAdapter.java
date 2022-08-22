package com.ifu.iforyoustudent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TimetableAdapter extends BaseAdapter {
    private Activity context;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    private ArrayList<String> items;
    public static List<String> day_string;
    public ArrayList<Timetable>  date_collection_arr;
    private String gridvalue;
    private ListView listDetails;
    private ArrayList<DialogTimetable> dialogTimetableArrayList=new ArrayList<DialogTimetable>();

    public TimetableAdapter(Activity context, GregorianCalendar monthCalendar,
                            ArrayList<Timetable> date_collection_arr) {
        this.date_collection_arr=date_collection_arr;
        TimetableAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public int getCount() {
        return day_string.size();
    }

    public Object getItem(int position) {
        return day_string.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }


        dayView = (TextView) v.findViewById(R.id.date);
        String[] separatedTime = day_string.get(position).split("-");


        gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 20) && (position > 28)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            dayView.setTextColor(Color.parseColor("#696969"));
        }


        if (day_string.get(position).equals(curentDateString)) {

            v.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }


        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        setEventView(v, position,dayView);

        return v;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        pmonthmaxset = (GregorianCalendar) pmonth.clone();

        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);


        for (int n = 0; n < 42; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }




    public void setEventView(View v,int pos,TextView txt){

        int len=Timetable.timetableArrayList.size();
        for (int i = 0; i < len; i++) {
            Timetable cal_obj=Timetable.timetableArrayList.get(i);
            String date=cal_obj.date;
            int len1=day_string.size();
            if (len1>pos) {

                if (day_string.get(pos).equals(date)) {
                    if ((Integer.parseInt(gridvalue) > 1) && (pos < firstDay)) {

                    } else if ((Integer.parseInt(gridvalue) < 7) && (pos > 28)) {

                    } else {
                        v.setBackgroundColor(Color.parseColor("#343434"));
                        v.setBackgroundResource(R.drawable.rounded_calender);
                        txt.setTextColor(Color.parseColor("#696969"));
                    }

                }
            }}
    }

    public void getPositionList(String date,final Activity act){

        int len= Timetable.timetableArrayList.size();

        JSONArray jsonArray=new JSONArray();
        for (int j=0; j<len; j++){
            if (Timetable.timetableArrayList.get(j).date.equals(date)){
                HashMap<String, String> mapList = new HashMap<String, String>();
                mapList.put("date",Timetable.timetableArrayList.get(j).date);
                mapList.put("module",Timetable.timetableArrayList.get(j).module);
                mapList.put("startTime",Timetable.timetableArrayList.get(j).startTime);
                mapList.put("endTime",Timetable.timetableArrayList.get(j).endTime);
                mapList.put("lecturer",Timetable.timetableArrayList.get(j).lecturer);
                mapList.put("type",Timetable.timetableArrayList.get(j).type);
                mapList.put("location",Timetable.timetableArrayList.get(j).location);

                JSONObject json = new JSONObject(mapList);
                jsonArray.put(json);
            }
        }
        if (jsonArray.length()!=0) {
            final Dialog dialogs = new Dialog(context);
            dialogs.setContentView(R.layout.dialog_timetable);
            listDetails = (ListView) dialogs.findViewById(R.id.list_view);
            listDetails.setAdapter(new DialogAdaptorTimetable(context, getMatchList(jsonArray + "")));
            dialogs.show();

        }

    }

    private ArrayList<DialogTimetable> getMatchList(String detail) {
        try {
            JSONArray jsonArray = new JSONArray(detail);
            dialogTimetableArrayList = new ArrayList<DialogTimetable>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.optJSONObject(i);

                DialogTimetable dialogTimetable = new DialogTimetable();

                dialogTimetable.setDate(jsonObject.optString("date"));
                dialogTimetable.setStartTime(jsonObject.optString("startTime"));
                dialogTimetable.setEndTime(jsonObject.optString("endTime"));
                dialogTimetable.setLecturer(jsonObject.optString("lecturer"));
                dialogTimetable.setModule(jsonObject.optString("module"));
                dialogTimetable.setType(jsonObject.optString("type"));
                dialogTimetable.setLocation(jsonObject.optString("location"));
                dialogTimetableArrayList.add(dialogTimetable);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dialogTimetableArrayList;
    }
}
