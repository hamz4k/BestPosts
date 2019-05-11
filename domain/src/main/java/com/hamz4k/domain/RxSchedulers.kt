package com.hamz4k.domain

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

object RxSchedulers {

    private lateinit var io: Scheduler
    private lateinit var computation: Scheduler
    private lateinit var trampoline: Scheduler
    private lateinit var mainThread: Scheduler

    fun init(computation: Scheduler = Schedulers.computation(),
             io: Scheduler = Schedulers.io(),
             trampoline: Scheduler = Schedulers.trampoline(),
             mainThread: Scheduler = Schedulers.trampoline()) {

        RxSchedulers.io = io
        RxSchedulers.computation = computation
        RxSchedulers.trampoline = trampoline
        RxSchedulers.mainThread = mainThread

    }

    fun computation() = computation
    fun io() = io
    fun trampoline() = trampoline
    fun mainThread() = mainThread
}

fun RxSchedulers.initForTests() {
    init(io = Schedulers.trampoline(),
         computation = Schedulers.trampoline(),
         mainThread = Schedulers.trampoline())
}
