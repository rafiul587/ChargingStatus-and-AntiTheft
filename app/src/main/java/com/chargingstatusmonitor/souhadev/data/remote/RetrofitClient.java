package com.chargingstatusmonitor.souhadev.data.remote;

import com.chargingstatusmonitor.souhadev.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static volatile RetrofitClient instance;
    private final Retrofit retrofit;
    private final ArchiveApi archiveApi;

    private RetrofitClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        archiveApi = retrofit.create(ArchiveApi.class);
    }

    public static RetrofitClient getInstance() {
        RetrofitClient result = instance;
        if (result == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
                result = instance;
            }
        }
        return result;
    }

    public ArchiveApi getArchiveApi() {
        return archiveApi;
    }

    public Flowable<DownloadFileState> downloadToFileWithProgress(ResponseBody responseBody, File directory, String filename) {
        return Flowable.create(emitter -> {
            emitter.onNext(new DownloadFileState.Progress(0));

            // flag to delete file if download errors or is cancelled
            boolean deleteFile = true;
            File file = new File(directory, filename);

            try {
                InputStream inputStream = responseBody.byteStream();
                OutputStream outputStream = new FileOutputStream(file);
                long totalBytes = responseBody.contentLength();
                byte[] data = new byte[8192];
                long progressBytes = 0;

                while (true) {
                    int bytesRead = inputStream.read(data);
                    if (bytesRead == -1) {
                        break;
                    }

                    outputStream.write(data, 0, bytesRead);
                    progressBytes += bytesRead;

                    int progress = (int) ((progressBytes * 100) / totalBytes);
                    emitter.onNext(new DownloadFileState.Progress(progress));
                }

                if (progressBytes < totalBytes) {
                    throw new Exception("missing bytes");
                } else if (progressBytes > totalBytes) {
                    throw new Exception("too many bytes");
                } else {
                    deleteFile = false;
                }

                emitter.onNext(new DownloadFileState.Finished(file));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                try {
                    responseBody.close();
                } catch (Exception e) {
                    // ignore
                }

                if (deleteFile) {
                    file.delete();
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Observable<DownloadFileState> downloadFile(String identifier, File directory, String fileName) {
        return Observable.create(emitter -> {
            ResponseBodyListener listener = responseBody -> {
                Disposable disposable = downloadToFileWithProgress(responseBody, directory, fileName)
                        .subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
                emitter.setDisposable(disposable);
            };
            Disposable disposable = getDownloaderRetrofit(listener)
                    .create(ArchiveApi.class)
                    .downloadFile(identifier, fileName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            emitter.setDisposable(disposable);
        });
    }

    interface ResponseBodyListener {
        void update(ResponseBody responseBody);
    }


    private OkHttpClient initHttpDownloadListenerClient(ResponseBodyListener listener) {
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    ResponseBody body = response.body();
                    if (body != null) {
                        listener.update(body);
                    }
                    return response;
                })
                .build();
    }

    private Retrofit getDownloaderRetrofit(ResponseBodyListener listener) {
        return retrofit.newBuilder().client(initHttpDownloadListenerClient(listener))
                .build();
    }
}
