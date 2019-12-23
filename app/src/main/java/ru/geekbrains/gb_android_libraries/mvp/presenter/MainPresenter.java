package ru.geekbrains.gb_android_libraries.mvp.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.subjects.PublishSubject;
import ru.geekbrains.gb_android_libraries.mvp.model.entity.Repository;
import ru.geekbrains.gb_android_libraries.mvp.model.repo.RealmUsersRepo;
import ru.geekbrains.gb_android_libraries.mvp.presenter.list.IRepositoryListPresenter;
import ru.geekbrains.gb_android_libraries.mvp.view.MainView;
import ru.geekbrains.gb_android_libraries.mvp.view.list.ReposotoryItemView;
import timber.log.Timber;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    class RepositoryListPresenter implements IRepositoryListPresenter {

        PublishSubject<ReposotoryItemView> clickSubject = PublishSubject.create();
        List<Repository> repositories = new ArrayList<>();

        @Override
        public void bind(ReposotoryItemView view) {
            view.setTitle(repositories.get(view.getPos()).getName());
        }

        @Override
        public int getCount() {
            return repositories.size();
        }

        @Override
        public PublishSubject<ReposotoryItemView> getClickSubject() {
            return clickSubject;
        }
    }

    private RealmUsersRepo usersRepo;
    private Scheduler mainThreadScheduler;
    private RepositoryListPresenter repositoryListPresenter;

    public MainPresenter(Scheduler mainThreadScheduler) {
        this.usersRepo = new RealmUsersRepo();
        this.mainThreadScheduler = mainThreadScheduler;
        repositoryListPresenter = new RepositoryListPresenter();
    }

    public IRepositoryListPresenter getCountriesListPresenter() {
        return repositoryListPresenter;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().init();
        loadData();

        repositoryListPresenter.getClickSubject().subscribe(countryRowView -> {
            getViewState().showMessage(repositoryListPresenter.repositories.get(countryRowView.getPos()).getName());
        });
    }

    @SuppressLint("CheckResult")
    private void loadData() {
        getViewState().showLoading();
        usersRepo.getUser("googlesamples")
                .observeOn(mainThreadScheduler)
                .flatMap(user -> {
                    getViewState().setUsername(user.getLogin());
                    getViewState().loadImage(user.getAvatarUrl());
                    return usersRepo.getUserRepos(user)
                            .observeOn(mainThreadScheduler);
                })
                .subscribe(repositories -> {
                    repositoryListPresenter.repositories.clear();
                    repositoryListPresenter.repositories.addAll(repositories);
                    getViewState().updateList();
                    getViewState().hideLoading();
                }, t -> {
                    getViewState().hideLoading();
                    Timber.e(t);
                });
    }
}
