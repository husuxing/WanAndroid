package com.cxz.wanandroid.mvp.presenter

import com.cxz.wanandroid.http.exception.ExceptionHandle
import com.cxz.wanandroid.http.function.RetryWithDelay
import com.cxz.wanandroid.mvp.contract.KnowledgeContract
import com.cxz.wanandroid.mvp.model.KnowledgeModel

/**
 * Created by chenxz on 2018/5/12.
 */
class KnowledgePresenter : CommonPresenter<KnowledgeContract.View>(), KnowledgeContract.Presenter {

    private val knowledgeModel: KnowledgeModel by lazy {
        KnowledgeModel()
    }

    override fun requestKnowledgeList(page: Int, cid: Int) {
        mView?.showLoading()
        val disposable = knowledgeModel.requestKnowledgeList(page, cid)
                .retryWhen(RetryWithDelay())
                .subscribe({ results ->
                    mView?.apply {
                        if (results.errorCode != 0) {
                            showError(results.errorMsg)
                        } else {
                            setKnowledgeList(results.data)
                        }
                        hideLoading()
                    }
                }, { t ->
                    mView?.apply {
                        hideLoading()
                        showError(ExceptionHandle.handleException(t))
                    }
                })
        addSubscription(disposable)
    }

}