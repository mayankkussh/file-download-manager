# file-download-manager
```
fileDownloadManager.requestFileDownload(FileRequestBean("file_id", resourceUrl,
                FileLocationFactory.zipsExtracted, fileName)
        )
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.scheduler)
                .subscribeWith(object : DisposableObserver<DownloadBean>() {
                    override fun onNext(downloadBean: DownloadBean) {
                        
                    }

                    override fun onError(e: Throwable) {
                        
                    }

                    override fun onComplete() {
                        // Do nothing
                    }
                })
```