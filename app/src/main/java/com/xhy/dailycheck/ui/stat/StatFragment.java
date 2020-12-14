package com.xhy.dailycheck.ui.stat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.xhy.dailycheck.MainActivity;
import com.xhy.dailycheck.R;
import com.xhy.dailycheck.bean.Record;
import com.xhy.dailycheck.dao.SQLiteManager;
import com.xhy.dailycheck.util.DateUtil;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;


public class StatFragment extends Fragment {

    ArrayList<Record> recordList = new ArrayList<Record>();//保存所有记录
    ArrayList<Record> filterRecordList = new ArrayList<Record>();//保存符合条件的记录
    EditText etTimeStart, etTimeEnd;
    PieChartView pieChartView;
    MainActivity mainActivity;
    AlertDialog adDetail;
    PieChartData pc_data;
    long timeStart;
    long timeEnd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) this.getActivity();
        //启动菜单
        setHasOptionsMenu(true);
        initAlertDialog();
    }

    private void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("签到记录：");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        adDetail = builder.create();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stat, container, false);
        pieChartView = root.findViewById(R.id.pie_chart);
        pieChartView.setCircleFillRatio(0.8f);
        pc_data = new PieChartData();
        pc_data.setHasLabels(true);
        etTimeStart = root.findViewById(R.id.etTimeStart);
        etTimeEnd = root.findViewById(R.id.etTimeEnd);
        //设置默认时间，默认七天（包括今天）
        etTimeStart.setText(DateUtil.getPastDate(6));//六天前
        etTimeEnd.setText(DateUtil.getNowDateString());//今天
        addListener();
        //去除焦点，防止触发焦点事件
        if (mainActivity.getCurrentFocus() != null)
            mainActivity.getCurrentFocus().clearFocus();
        //更新图表
        updatePieChart();
        return root;
    }

    //增加事件监听器
    private void addListener() {
        etTimeStart.setKeyListener(null);//禁止用户通过键盘编辑
        etTimeEnd.setKeyListener(null);//禁止用户通过键盘编辑
        //监听焦点切换事件和点击事件，弹出时间选择框
        etTimeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DateUtil.showDateDialogPick(mainActivity, etTimeStart);
                }
            }
        });
        etTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateUtil.showDateDialogPick(mainActivity, etTimeStart);
            }
        });
        //文本内容修改后，更新图表
        etTimeStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                updatePieChart();
            }
        });
        //监听焦点切换事件和点击事件，弹出时间选择框
        etTimeEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DateUtil.showDateDialogPick(mainActivity, etTimeEnd);
                }
            }
        });
        etTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateUtil.showDateDialogPick(mainActivity, etTimeEnd);
            }
        });
        //文本内容修改后，更新图表
        etTimeEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                updatePieChart();
            }
        });
    }

    //更新图表
    private void updatePieChart() {
        //获取起止日期
        String start = etTimeStart.getText().toString();
        String end = etTimeEnd.getText().toString();
        //转换为时间戳
        timeStart = DateUtil.DateString2TimeStamp(start + " 00:00");
        timeEnd = DateUtil.DateString2TimeStamp(end + " 23:59");
        SQLiteManager.queryRecordList(recordList);//查询数据库，获取全部记录
        List<SliceValue> pc_values = new ArrayList<SliceValue>();
        filterRecordList.clear();
        for (Record r : recordList) {
            long ts = r.getTimestamp();
            //时间不在范围内，则跳过
            if (ts < timeStart || ts > timeEnd) {
                continue;
            }
            filterRecordList.add(r);
        }
        //计算天数
        int totalDays = DateUtil.getPassDays(start, end);
        int checkedDays = filterRecordList.size();
        int uncheckedDays = (totalDays - checkedDays);
        //更新图表
        if (checkedDays > 0) {
            SliceValue sliceValue = new SliceValue(checkedDays, ChartUtils.COLOR_GREEN);
            sliceValue.setLabel("已签到: " + checkedDays);
            pc_values.add(sliceValue);
        }
        if (uncheckedDays > 0) {
            SliceValue sliceValue2 = new SliceValue(uncheckedDays, ChartUtils.COLOR_RED);
            sliceValue2.setLabel("未签到: " + uncheckedDays);
            pc_values.add(sliceValue2);
        }
        pc_data.setValues(pc_values);
        pieChartView.setPieChartData(pc_data);
    }

    //显示详情
    private void showDetails() {
        String msg = "";
        int cnt = 0;
        for (Record r : filterRecordList) {
            cnt++;
            long ts = r.getTimestamp();
            msg += "" + cnt + "：" + DateUtil.TimeStamp2Date(ts) + "\n";
        }
        adDetail.setMessage(msg);
        adDetail.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_details:
                showDetails();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}