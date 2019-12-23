package ru.geekbrains.gb_android_libraries.mvp.model;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Model {
    private List<Integer> counters;

    public Model() {
        counters = new ArrayList<>();
        counters.add(0);
        counters.add(0);
        counters.add(0);
    }

    public Observable<Integer> getAt(int pos) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return counters.get(pos);
            }
        });
    }

    public Completable setAt(int pos, int value) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                counters.set(pos, value);
            }
        });
    }
}
