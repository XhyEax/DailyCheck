package com.xhy.dailycheck.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xhy.dailycheck.MainActivity;
import com.xhy.dailycheck.R;
import com.xhy.dailycheck.RetrofitService;
import com.xhy.dailycheck.bean.CheckBean;
import com.xhy.dailycheck.bean.Record;
import com.xhy.dailycheck.bean.RegBean;
import com.xhy.dailycheck.dao.SQLiteManager;
import com.xhy.dailycheck.util.DateUtil;
import com.xhy.dailycheck.util.SPUtil;
import com.xhy.dailycheck.util.ToastUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    SwipeRefreshLayout mRefreshLayout;
    MainActivity mainActivity;
    AlertDialog adName;
    Button btnCheck;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) this.getActivity();
        //初始化对话框
        initAlertDialog();
        //初始化数据库
        SQLiteManager.initDB(mainActivity);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        com.alarmclockview.AlarmClockView mClock = root.findViewById(R.id.clock);
        //启动时钟
        mClock.start();
        btnCheck = root.findViewById(R.id.btnCheck);
        //绑定点击事件
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //未注册
                if (!checkUid()) {
                    return;
                }
                //已签到
                if (btnCheck.getText().toString().equals("已签到")) {
                    ToastUtil.ToastMsg(mainActivity, "你今天已经签过到了");
                    return;
                }
                //签到
                doCheck();
            }
        });
        //下拉刷新
        mRefreshLayout = root.findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                getCheckState();
            }
        });
        //如果uid已存在，则获取是否已签到
        if (checkUid()) {
            mRefreshLayout.setRefreshing(true);
            getCheckState();
        }
        return root;
    }

    //初始化对话框
    private void initAlertDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("请设置姓名：");
        //自定义布局，输入过滤条件
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_name, null, false);
        builder.setView(view);
        final EditText etName = view.findViewById(R.id.etName);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        builder.setPositiveButton("确定", null);
        adName = builder.create();
        //点击确定后不关闭对话框
        adName.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                adName.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etName.getText().toString();
                        //检查姓名
                        if (name.isEmpty()) {
                            ToastUtil.ToastMsg(mainActivity, "姓名不能为空");
                        } else if (name.contains(" ")) {
                            ToastUtil.ToastMsg(mainActivity, "姓名不能包含空格");
                        } else if (name.length() > 20) {
                            ToastUtil.ToastMsg(mainActivity, "姓名长度不能大于20");
                        } else {
                            //向服务器注册，获取uid
                            registerByName(name);
                        }
                    }
                });
            }
        });
    }


    // 注册，完成后关闭对话框
    private void registerByName(String name) {
        Observable<RegBean> observable = mainActivity.retrofitService.register(name);//注册
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(RegBean value) {
                        if (value.success) {
                            ToastUtil.ToastMsg(mainActivity, "注册成功");
                            SPUtil.setUid(mainActivity, value.uid);
                        } else {
                            ToastUtil.ToastMsg(mainActivity, value.msg);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.ToastMsg(mainActivity, e.toString());
                    }
                    @Override
                    public void onComplete() {
                        //完成后关闭对话框
                        adName.dismiss();
                    }
                });
    }

    //获取签到状态
    private void getCheckState() {
        String uid = SPUtil.getUid(mainActivity);
        Observable<CheckBean> observable = mainActivity.retrofitService.getCheckState(uid);//获取签到状态
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(CheckBean value) {
                        if (value.success) {
                            if (value.isChecked) {
                                changeButtonState(true);
                            } else {
                                changeButtonState(false);
                            }
                        } else {
                            //出错才提示
                            ToastUtil.ToastMsg(mainActivity, value.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.ToastMsg(mainActivity, e.toString());
                    }
                    @Override
                    public void onComplete() {
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    //签到
    private void doCheck() {
        String uid = SPUtil.getUid(mainActivity);
        Observable<CheckBean> observable = mainActivity.retrofitService.check(uid);//签到
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(CheckBean value) {
                        if (value.success) {
                            if (value.isChecked) {
                                changeButtonState(true);
                                //签到成功，写入本地数据库
                                long code = SQLiteManager.insert(new Record(DateUtil.getNowTimeStamp()));
                                if (code == -1) {
                                    ToastUtil.ToastMsg(mainActivity, "插入数据失败！请确保时间设置正确");
                                }
                            } else {
                                changeButtonState(false);
                            }
                        }
                        //任何情况下都提示
                        ToastUtil.ToastMsg(mainActivity, value.msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.ToastMsg(mainActivity, e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    //判断是否无uid，若无则设置姓名并获取uid
    private boolean checkUid() {
        String uid = SPUtil.getUid(mainActivity);
        if (uid.isEmpty()) {
            ToastUtil.ToastMsg(mainActivity, "请先设置姓名后再签到");
            adName.show();
            return false;
        }
        return true;
    }

    //更改签到按钮状态
    private void changeButtonState(boolean state) {
        if (state) {
            btnCheck.setText("已签到");
            btnCheck.setTextColor(Color.BLACK);
        } else {
            btnCheck.setText("签到");
            btnCheck.setTextColor(Color.WHITE);
        }
    }

}