package com.xhy.dailycheck;

import com.xhy.dailycheck.bean.CheckBean;
import com.xhy.dailycheck.bean.ListBean;
import com.xhy.dailycheck.bean.RegBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("register")
    Observable<RegBean> register(@Query("name") String name);

    @GET("getCheckState")
    Observable<CheckBean> getCheckState(@Query("uid") String uid);

    @GET("check")
    Observable<CheckBean> check(@Query("uid") String uid);

    @GET("getCheckList")
    Observable<ListBean> getCheckList();
}
