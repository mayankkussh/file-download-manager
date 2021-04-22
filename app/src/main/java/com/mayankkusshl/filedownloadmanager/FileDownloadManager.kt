package com.mayankkusshl.filedownloadmanager

import com.mayankkusshl.filedownloadmanager.FileHelper.saveFile
import com.mayankkusshl.filedownloadmanager.ProgressBody.ProgressListener
import io.reactivex.rxjava3.core.Observable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.Response
import java.io.File
import java.io.IOException

class FileDownloadManager  constructor(private val oktHttpClientBuilder: OkHttpClient.Builder) {

    fun requestFileDownload(dowloadBean: FileRequestBean): Observable<DownloadBean> {
        return observable {emitter ->
            val call: Call = getOkHttpClient(object : ProgressListener {
                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                    if (!done) {
                        if (contentLength != -1L) {
                            emitter.onSuccessObservable(DownloadBean((100 * bytesRead) / contentLength, null))
                        }
                    }
                }
            }).newCall(Builder().url(dowloadBean.url).get().build())

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    emitter.onErrorObservable(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val file = processResponse(dowloadBean, response)
                            emitter.onSuccessObservable(DownloadBean(100, file))
                            emitter.onCompeteObservable()
                        } catch (e: Exception) {
                            emitter.onErrorObservable(e)
                        }
                    }
                }
            })
        }
    }

    private fun getOkHttpClient(progressListener: ProgressListener): OkHttpClient {
      return oktHttpClientBuilder
              .addNetworkInterceptor { chain: Chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                        .body(ProgressBody(originalResponse.body()!!, progressListener))
                        .build()
              }
              .build()
    }

    private fun processResponse(dowloadBean: FileRequestBean, response: Response): File {
        try {
            return saveFile(response, dowloadBean.fileName,
                FileLocationFactory.getStorageDirectory(dowloadBean.path)
            )
        } catch (e: Exception) {
            throw e
        }
    }
}
