package com.mastercoding.pagingapp.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.mastercoding.pagingapp.api.APIClient;
import com.mastercoding.pagingapp.model.Movie;
import com.mastercoding.pagingapp.model.MovieResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {


    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> pagingState) {
        return null;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        try{
            // if the page number is already there, then initiate page variable with it
            // otherwise we are going to get the first page

            int page = loadParams.getKey() != null ? loadParams.getKey():1;

            // send the request to the sever with page number by making return API
            return APIClient.getApiInterface()
                    .getMoviesByPage(page)// lấy danh sách phim theo trang
                    .subscribeOn(Schedulers.io())
                    .map(MovieResponse::getMovies)// chuyển đổi dữ liệu
                    .map(movies -> toLoadResult(movies, page))
                    .onErrorReturn(LoadResult.Error::new);// nếu có lỗi trong quá trình gọi API hoặc
                    // hoặc xử lí dữ liệu, trả về một đối tượng LoadResult.Error
        } catch (Exception e) {
            return Single.just(new LoadResult.Error(e));
        }
    }

    private LoadResult<Integer, Movie> toLoadResult(List<Movie> movies, int page){
        return new LoadResult.Page(movies,page ==1 ? null : page -1 , page+1);
    }
}
