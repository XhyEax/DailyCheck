package com.xhy.dailycheck.ui.rank;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xhy.dailycheck.MainActivity;
import com.xhy.dailycheck.R;
import com.xhy.dailycheck.adapter.RankAdapter;
import com.xhy.dailycheck.bean.CheckBean;
import com.xhy.dailycheck.bean.ListBean;
import com.xhy.dailycheck.bean.ListItem;
import com.xhy.dailycheck.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RankFragment extends Fragment {

    List<ListItem> rankList = new ArrayList<ListItem>();
    SwipeRefreshLayout mRefreshLayout;
    MainActivity mainActivity;
    RankAdapter rankAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) this.getActivity();
        rankAdapter = new RankAdapter(rankList);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rank, container, false);
        //绑定recyclerView，并设置Adapter
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(mainActivity, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rankAdapter);
        //下拉显示底部导航栏，上滑隐藏
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mainActivity.setNavVisable(false);
                } else {
                    mainActivity.setNavVisable(true);
                }
            }
        });
        //下拉刷新
        mRefreshLayout = root.findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                refresh();
            }
        });
        //刷新
        mRefreshLayout.setRefreshing(true);
        refresh();
        return root;
    }

    public void refresh(){
        Observable<ListBean> observable = mainActivity.retrofitService.getCheckList();//获取签到列表
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(ListBean value) {
                        //更新列表及视图
                        rankList.clear();
                        for(ListItem item : value.data){
                            rankList.add(item);
                        }
                        rankAdapter.notifyDataSetChanged();
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
}
