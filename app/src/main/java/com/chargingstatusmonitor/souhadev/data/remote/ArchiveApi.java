package com.chargingstatusmonitor.souhadev.data.remote;

import com.chargingstatusmonitor.souhadev.model.ApiResponse;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface ArchiveApi {

    @GET("metadata/{identifier}/files")
    Call<ApiResponse> getArchiveDetails(@Path("identifier") String identifier);

    @Streaming
    @GET("download/{identifier}/{filename}")
    Observable<ResponseBody> downloadFile(@Path("identifier") String identifier,
                                          @Path("filename") String filename);
}

